package io.github.kimmking.kksharding.strategy;

import groovy.lang.Closure;
import lombok.Data;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Description for this class.
 *
 * @Author : kimmking(kimmking@apache.org)
 * @create 2024/1/14 22:59
 */
@Data
public class HashShardingStrategy implements ShardingStrategy {

    private String shardingColumn;
    private String algorithmExpression;

    @Override
    public List<String> getShardingColumns() {
        return Collections.singletonList(shardingColumn);
    }

    @Override
    public String doSharding(List<String> availableTargetNames, String logicTableName, Map<String, Object> shardingParams) {
        InlineExpressionParser parser = new InlineExpressionParser(InlineExpressionParser.handlePlaceHolder(algorithmExpression));
        Closure closure = parser.evaluateClosure();
        closure.setProperty(shardingColumn, shardingParams.get(shardingColumn));
        return closure.call().toString();
    }

}

