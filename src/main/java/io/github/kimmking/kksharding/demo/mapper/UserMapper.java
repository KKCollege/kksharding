package io.github.kimmking.kksharding.demo.mapper;

import io.github.kimmking.kksharding.demo.model.User;
import org.apache.ibatis.annotations.*;

/**
 * user mapper.
 *
 * @Author : kimmking(kimmking@apache.org)
 * @create 2024/1/15 00:39
 */
@Mapper
public interface UserMapper {

    @Select("select * from t_user where id = #{id}")
    User findById(int id);

    @Select("select * from t_user where id = #{id} and name = #{name}")
    User findByIdAndName(int id, String name);

    @Insert("insert into t_user(id, name, age) values (#{id}, #{name}, #{age})")
    int insert(User user);

    @Update("update t_user set name = #{name}, age = #{age} where id = #{id}")
    int update(User user);

    @Delete("delete from t_user where id = #{id}")
    int delete(int id);

}
