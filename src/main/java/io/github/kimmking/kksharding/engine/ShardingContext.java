package io.github.kimmking.kksharding.engine;

import io.github.kimmking.kksharding.engine.ShardingResult;

/**
 * Description for this class.
 *
 * @Author : kimmking(kimmking@apache.org)
 * @create 2024/1/15 00:51
 */
public class ShardingContext {
    private static final ThreadLocal<ShardingResult> LOCAL = new ThreadLocal<>();

    public static void setShardingResult(ShardingResult shardingResult) {
        LOCAL.set(shardingResult);
    }

    public static ShardingResult getShardingResult() {
        return LOCAL.get();
    }

    public static void clear() {
        LOCAL.remove();
    }

}
