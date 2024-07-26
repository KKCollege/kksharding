package io.github.kimmking.kksharding.mybatis;

import io.github.kimmking.kksharding.engine.ShardingContext;
import io.github.kimmking.kksharding.engine.ShardingResult;
import io.github.kimmking.kksharding.engine.ShardingEngine;
import lombok.Data;
import lombok.Setter;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSession;
import org.mybatis.spring.mapper.MapperFactoryBean;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.List;

/**
 * Factory Bean.
 *
 * @Author : kimmking(kimmking@apache.org)
 * @create 2024/1/15 00:28
 */
public final class ShardingMapperFactoryBean<T> extends MapperFactoryBean<T> {

    @Setter
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
                Object[] params = getParams(args, boundSql);

                ShardingResult result = shardingEngine.sharding(sql, params);
                ShardingContext.setShardingResult(result);
            }
            return method.invoke(proxy, args);
        });
    }

    private static Object[] getParams(Object[] args, BoundSql boundSql) throws IllegalAccessException {
        Object[] params = args;
        if(args.length == 1 && !ClassUtils.isPrimitiveOrWrapper(args[0].getClass())) {
            Object arg = args[0];
            List<String> cols = boundSql.getParameterMappings()
                    .stream().map(ParameterMapping::getProperty).toList();
            Object[] values = new Object[cols.size()];
            for (int i = 0; i < cols.size(); i++) {
                Field field = ReflectionUtils.findField(arg.getClass(), cols.get(i));
                if(field == null) throw new IllegalArgumentException("can not find field " + cols.get(i));
                field.setAccessible(true);
                values[i] = field.get(arg);
            }
            params = values;
        }
        return params;
    }

    private String getMapperId(Class<?> clazz, Method method) {
        return clazz.getName() + "." + method.getName();
    }

}
