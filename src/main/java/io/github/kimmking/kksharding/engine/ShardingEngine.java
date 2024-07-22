package io.github.kimmking.kksharding.engine;

/**
 * Description for this class.
 *
 * @Author : kimmking(kimmking@apache.org)
 * @create 2024/1/14 22:03
 */
public interface ShardingEngine {

    ShardingResult sharding(String sql, Object[] args);

}
