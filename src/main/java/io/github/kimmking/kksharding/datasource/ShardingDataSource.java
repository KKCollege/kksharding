package io.github.kimmking.kksharding.datasource;

import com.alibaba.druid.pool.DruidDataSourceFactory;
import io.github.kimmking.kksharding.engine.ShardingProperties;
import io.github.kimmking.kksharding.engine.ShardingResult;
import io.github.kimmking.kksharding.engine.ShardingContext;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

/**
 * Description for this class.
 *
 * @Author : kimmking(kimmking@apache.org)
 * @create 2024/1/15 01:21
 */
public class ShardingDataSource extends AbstractRoutingDataSource {

    public ShardingDataSource(ShardingProperties properties) {
        Map<Object, Object> dataSourceMap = new HashMap<>();

        DataSource[] defaultDataSourceName = {null};
        properties.getDatasources().forEach((k, v) -> {
            try {
                DataSource ds = DruidDataSourceFactory.createDataSource(v);
                if (defaultDataSourceName[0] == null) {
                    defaultDataSourceName[0] = ds;
                }
                dataSourceMap.put(k, ds);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        setTargetDataSources(dataSourceMap);
        setDefaultTargetDataSource(defaultDataSourceName[0]);
    }

    @Override
    protected Object determineCurrentLookupKey() {
        ShardingResult result = ShardingContext.getShardingResult();
        return result == null ? null : result.getTargetDataSourceName();
    }

}
