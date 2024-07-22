package io.github.kimmking.kksharding.engine;

import lombok.Data;

/**
 * Description for this class.
 *
 * @Author : kimmking(kimmking@apache.org)
 * @create 2024/1/14 22:04
 */
@Data
public class ShardingResult {

    private String targetDataSourceName;
    private String targetSqlString;
    private Object[] parameters;

}
