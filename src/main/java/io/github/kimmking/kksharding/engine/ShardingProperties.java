package io.github.kimmking.kksharding.engine;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.*;

/**
 * Description for this class.
 *
 * @Author : kimmking(kimmking@apache.org)
 * @create 2024/1/14 22:29
 */
@Data
@ConfigurationProperties(prefix = "spring.sharding")
public class ShardingProperties {

    private Map<String, Properties> datasources = new LinkedHashMap<>();
    private Map<String, TableProperties> tables = Collections.emptyMap();

    @Data
    public static class TableProperties {
        private List<String> actualDataNodes;
        private Properties databaseStrategy;
        private Properties tableStrategy;
    }

}