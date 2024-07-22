package io.github.kimmking.kksharding;

import io.github.kimmking.kksharding.datasource.ShardingDataSource;
import io.github.kimmking.kksharding.engine.ShardingEngine;
import io.github.kimmking.kksharding.engine.ShardingProperties;
import io.github.kimmking.kksharding.engine.StandardShardingEngine;
import io.github.kimmking.kksharding.mybatis.SqlStatementInterceptor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Auto configuration.
 *
 * @Author : kimmking(kimmking@apache.org)
 * @create 2024/1/14 22:27
 */
@Configuration
@EnableConfigurationProperties({ShardingProperties.class})
public class ShardingAutoConfiguration {

    @Bean
    public ShardingDataSource shardingDataSource(ShardingProperties properties) {
        return new ShardingDataSource(properties);
    }

    @Bean
    public ShardingEngine shardingEngine(ShardingProperties shardingProperties) {
        return new StandardShardingEngine(shardingProperties);
    }

    @Bean
    public SqlStatementInterceptor sqlStatementInterceptor() {
        return new SqlStatementInterceptor();
    }

}
