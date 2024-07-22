package io.github.kimmking.kksharding.demo.mapper;

import org.apache.ibatis.annotations.*;

import java.util.Map;

/**
 * Description for this class.
 *
 * @Author : kimmking(kimmking@apache.org)
 * @create 2024/1/15 00:39
 */
@Mapper
public interface UserMapper {

    @Select("select * from t_user where id = #{id}")
    Map<String, String> findById(int id);

    @Insert("insert into t_user(id, name) values (#{id}, #{name})")
    int insert(int id, String name);

    @Update("update t_user set name = #{name} where id = #{id}")
    int update(String name, int id);

    @Delete("delete from t_user where id = #{id}")
    int delete(int id);

}
