package io.github.kimmking.kksharding.engine;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.ast.expr.SQLNullExpr;
import com.alibaba.druid.sql.ast.expr.SQLValuableExpr;
import com.alibaba.druid.sql.ast.expr.SQLVariantRefExpr;
import com.alibaba.druid.sql.ast.statement.SQLInsertStatement;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlSchemaStatVisitor;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;
import com.alibaba.druid.stat.TableStat;
import io.github.kimmking.kksharding.strategy.ShardingStrategy;

import java.util.*;

/**
 * Core engine.
 *
 * @Author : kimmking(kimmking@apache.org)
 * @create 2024/1/14 22:08 */
public class StandardShardingEngine implements ShardingEngine {

    private final Map<String, List<String>> actaulDatabaseNames = new HashMap<>();
    private final Map<String, List<String>> actaulTableNames = new HashMap<>();
    private final Map<String, ShardingStrategy> databaseStrategys = new HashMap<>();
    private final Map<String, ShardingStrategy> tableStrategys = new HashMap<>();

    public StandardShardingEngine(ShardingProperties properties) {
        Map<String, Set<String>> databaseNames = new HashMap<>();
        Map<String, Set<String>> tableNames = new HashMap<>();
        properties.getTables().forEach((table, tableProperties) -> {
            databaseStrategys.put(table, ShardingStrategyFactory.getShardingStrategy(tableProperties.getDatabaseStrategy()));
            tableStrategys.put(table, ShardingStrategyFactory.getShardingStrategy(tableProperties.getTableStrategy()));

            if (!databaseNames.containsKey(table)) {
                databaseNames.put(table, new LinkedHashSet<>());
            }

            tableProperties.getActualDataNodes().forEach(actualDataNode -> {
                String[] split = actualDataNode.split("\\.");
                String databaseName = split[0], tableName = split[1];

                if (!tableNames.containsKey(databaseName)) {
                    tableNames.put(databaseName, new LinkedHashSet<>());
                }

                databaseNames.get(table).add(databaseName);
                tableNames.get(databaseName).add(tableName);
            });
        });
        databaseNames.forEach((table, names) -> actaulDatabaseNames.put(table, new ArrayList<>(names)));
        tableNames.forEach((database, names) -> actaulTableNames.put(database, new ArrayList<>(names)));
    }

    @Override
    public ShardingResult sharding(String sql, Object[] args) {
        SQLStatement statement = SQLUtils.parseSingleMysqlStatement(sql);

        MySqlSchemaStatVisitor visitor = new MySqlSchemaStatVisitor();
        visitor.setParameters(Arrays.asList(args));
        statement.accept(visitor);

        List<SQLName> names = new ArrayList<>(new LinkedHashSet<>(visitor.getOriginalTables()));
        if (names.size() != 1) {
            throw new RuntimeException("not support multi table sharding");
        }
        String tableName = names.get(0).getSimpleName();

        ShardingStrategy databaseStrategy = databaseStrategys.get(tableName);
        Map<String, Object> databaseShardingColumns = statement instanceof SQLInsertStatement
                ? findShardingColumns((SQLInsertStatement) statement, databaseStrategy.getShardingColumns(), args)
                : findShardingColumns(visitor, databaseStrategy.getShardingColumns());
        List<String> availableDatabaseNames = actaulDatabaseNames.get(tableName);
        String availableDatabaseName = databaseStrategy.doSharding(availableDatabaseNames, tableName, databaseShardingColumns);
        if (!availableDatabaseNames.contains(availableDatabaseName)) {
            throw new RuntimeException("not found available database name");
        }

        ShardingStrategy tableStrategy = tableStrategys.get(tableName);
        Map<String, Object> tableShardingColumns = statement instanceof SQLInsertStatement
                ? findShardingColumns((SQLInsertStatement) statement, tableStrategy.getShardingColumns(), args)
                : findShardingColumns(visitor, tableStrategy.getShardingColumns());
        List<String> actaulTableNames = this.actaulTableNames.get(availableDatabaseName);
        String availableTableName = tableStrategy.doSharding(actaulTableNames, tableName, tableShardingColumns);
        if (!actaulTableNames.contains(availableTableName)) {
            throw new RuntimeException("not found available table name");
        }

        ShardingResult result = new ShardingResult();
        result.setTargetDataSourceName(availableDatabaseName);
        result.setTargetSqlString(sql.replace(tableName, availableTableName));
        result.setParameters(args);
        return result;
    }

    private Map<String, Object> findShardingColumns(SQLInsertStatement statement, List<String> shardingColumns, Object[] args) {
        Map<String, Object> shardingColumnsMap = new HashMap<>();

        List<SQLExpr> columns = statement.getColumns();
        List<SQLExpr> values = statement.getValues().getValues();
        for (int i = 0; i < columns.size(); i++) {
            SQLIdentifierExpr columnExpr = (SQLIdentifierExpr) columns.get(i);
            SQLExpr valueExpr = values.get(i);
            if (shardingColumns.contains(columnExpr.getSimpleName())) {
                shardingColumnsMap.put(columnExpr.getSimpleName(), ValueVisitor.getValue(valueExpr, args));
            }
        }
        return shardingColumnsMap;
    }

    private Map<String, Object> findShardingColumns(MySqlSchemaStatVisitor visitor, List<String> shardingColumns) {
        Map<String, Object> shardingColumnsMap = new HashMap<>();
        for (TableStat.Condition condition : visitor.getConditions()) {
            if (shardingColumns.contains(condition.getColumn().getName())) {
                shardingColumnsMap.put(condition.getColumn().getName(), condition.getValues().toArray()[0]);
            }
        }
        return shardingColumnsMap;
    }

    private static final class ValueVisitor implements SQLASTVisitor {

        private final Object[] args;

        private Object value;

        public ValueVisitor(Object[] args) {
            this.args = args;
        }

        @Override
        public boolean visit(SQLVariantRefExpr x) {
            value = args[x.getIndex()];
            return false;
        }

        @Override
        public boolean visit(SQLNullExpr x) {
            value = null;
            return false;
        }

        public static Object getValue(SQLExpr expr, Object[] args) {
            if (expr instanceof SQLValuableExpr) {
                return ((SQLValuableExpr) expr).getValue();
            } else {
                ValueVisitor visitor = new ValueVisitor(args);
                expr.accept(visitor);
                return visitor.value;
            }
        }
    }

}
