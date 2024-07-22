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

    @Insert("insert into t_user(id, name) values (#{id}, #{name})")
    int insert(int id, String name);

    @Update("update t_user set name = #{name} where id = #{id}")
    int update(String name, int id);

    @Delete("delete from t_user where id = #{id}")
    int delete(int id);

}
