package io.github.kimmking.kksharding.mybatis;

import io.github.kimmking.kksharding.engine.ShardingContext;
import io.github.kimmking.kksharding.engine.ShardingResult;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.springframework.objenesis.instantiator.util.UnsafeUtils;
import sun.misc.Unsafe;

import java.lang.reflect.Field;

/**
 * Core interceptor.
 *
 * @Author : kimmking(kimmking@apache.org)
 * @create 2024/1/15 00:46
 */
@Intercepts({
    @org.apache.ibatis.plugin.Signature(
            type = StatementHandler.class,
            method = "prepare",
            args = {java.sql.Connection.class, Integer.class}
    ),
})
public class SqlStatementInterceptor implements Interceptor {

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        ShardingResult result = ShardingContext.getShardingResult();
        if (result == null) {
            return invocation.proceed();
        }

        StatementHandler handler = (StatementHandler) invocation.getTarget();

        BoundSql boundSql = handler.getBoundSql();
        if (boundSql.getSql().equals(result.getTargetSqlString())) {
            return invocation.proceed();
        }

        Field field = boundSql.getClass().getDeclaredField("sql");
//        field.setAccessible(true);
//        makeModifiered(field);
//        field.set(boundSql, result.getTargetSqlString());

        Unsafe unsafe = UnsafeUtils.getUnsafe();
        long fieldOffset = unsafe.objectFieldOffset(field);
        unsafe.putObject(boundSql, fieldOffset, result.getTargetSqlString());

        return invocation.proceed();
    }
//
//    private void makeModifiered(Field field) throws NoSuchFieldException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
//        Class<?> clazz = field.getClass();
//        Field fModifiers = getDeclaredField(clazz, "modifiers");
//        fModifiers.setAccessible(true);
//        fModifiers.setInt(field, field.getModifiers() & ~java.lang.reflect.Modifier.FINAL);
//    }
//
//    private Field getDeclaredField(Class<?> clazz, String name) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
//        Method getDeclaredFields0 = Class.class.getDeclaredMethod("getDeclaredFields0", boolean.class);
//        getDeclaredFields0.setAccessible(true);
//        Field[] fields = (Field[]) getDeclaredFields0.invoke(Field.class, false);
//
//        Field modifiers = null;
//        for (Field each : fields) {
//            if (name.equals(each.getName())) {
//                modifiers = each;
//            }
//        }
//
//        return modifiers;
//    }

}
