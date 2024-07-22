package io.github.kimmking.kksharding.strategy;

import java.util.List;
import java.util.Map;

/**
 * Description for this class.
 *
 * @Author : kimmking(kimmking@apache.org)
 * @create 2024/1/14 22:10
 */
public interface ShardingStrategy {

    List<String> getShardingColumns();

    String doSharding(List<String> availableTargetNames, String logicTableName, Map<String, Object> shardingParams);

}
