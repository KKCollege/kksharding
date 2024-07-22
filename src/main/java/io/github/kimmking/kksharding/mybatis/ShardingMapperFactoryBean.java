package io.github.kimmking.kksharding.mybatis;

import io.github.kimmking.kksharding.engine.ShardingContext;
import io.github.kimmking.kksharding.engine.ShardingResult;
import io.github.kimmking.kksharding.engine.ShardingEngine;
import lombok.Data;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSession;
import org.mybatis.spring.mapper.MapperFactoryBean;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * Factory Bean.
 *
 * @Author : kimmking(kimmking@apache.org)
 * @create 2024/1/15 00:28
 */
@Data
public final class ShardingMapperFactoryBean<T> extends MapperFactoryBean<T> {

    private ShardingEngine shardingEngine;

    public ShardingMapperFactoryBean() {
    }

    public ShardingMapperFactoryBean(Class<T> mapperInterface) {
        super(mapperInterface);
    }

    @SuppressWarnings("unchecked")
    @Override
    public T getObject() throws Exception {
        Object proxy = super.getObject();

        SqlSession session = getSqlSession();
        Configuration configuration = session.getConfiguration();
        Class<?> clazz = getMapperInterface();

        return (T) Proxy.newProxyInstance(clazz.getClassLoader(), new Class[]{clazz}, (o, method, args) -> {
            if (method.getDeclaringClass() != Object.class) {
                String mapperId = getMapperId(clazz, method);
                MappedStatement statement = configuration.getMappedStatement(mapperId);
                BoundSql boundSql = statement.getBoundSql(args);
                String sql = boundSql.getSql();
                ShardingResult result = shardingEngine.sharding(sql, args);
                ShardingContext.setShardingResult(result);
            }
            return method.invoke(proxy, args);
        });
    }

    private String getMapperId(Class<?> clazz, Method method) {
        return clazz.getName() + "." + method.getName();
    }

}
