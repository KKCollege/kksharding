package io.github.kimmking.kksharding.demo;

import io.github.kimmking.kksharding.ShardingAutoConfiguration;
import io.github.kimmking.kksharding.demo.mapper.UserMapper;
import io.github.kimmking.kksharding.demo.model.User;
import io.github.kimmking.kksharding.mybatis.ShardingMapperFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import({ShardingAutoConfiguration.class})
@MapperScan(value = "io.github.kimmking.kksharding.demo.mapper",
        factoryBean = ShardingMapperFactoryBean.class)
public class KKShardingApplication {

    public static void main(String[] args) {
        SpringApplication.run(KKShardingApplication.class, args);
    }

    @Autowired
    private UserMapper mapper;

    @Bean
    ApplicationRunner runner() {
        return x -> {
            System.out.println("=================================================");
            System.out.println("===== Run kk sharding test for mybatis CRUD =====");
            System.out.println("=================================================");

//            int kk = mapper.insert(new User(101, "KK101", 20));
//            System.out.println(" =》 insert id=101, and result = "+ kk);

//            User user = mapper.findById(101);
//            System.out.println(" =》 findById id=101, and result = "+user);

            test(1, "KK0" + 1);

//            for (int i = 1; i <= 10; i++) {
//                test(i, "KK0" + i);
//            }

            System.out.println("=================================================");
            System.out.println("=====        Run all test completely.       =====");
            System.out.println("=================================================");

        };
    }

    private void test(int id, String name) {
        System.out.println("\n\n");
        System.out.println(" [[[===>> Running kk sharding test for id/name=" + id + "/" + name + "]]]");
        int no = 1;
        int deleted = mapper.delete(id);
        System.out.println(no++ + ". delete id = "+id+", result: " + deleted);

        int kk = mapper.insert(new User(id, name, 20));
        System.out.println(no++ + ". insert id = "+id+", name="+name+", result: " + kk);

        User user = mapper.findById(id);
        System.out.println(no++ + ". findById id="+id+", and name: "+user.getName());

        user = mapper.findByIdAndName(id, name);
        System.out.println(no++ + ". findByIdAndName id="+id+", and name: "+user.getName());

        user.setName(user.getName() + "02");
        kk = mapper.update(user);
        System.out.println(no++ + ". update id="+id+", name="+name+"02, result: "+kk);

        user = mapper.findById(id);
        System.out.println(no++ + ". findById id="+id+", and name: "+user.getName());

        int delete = mapper.delete(id);
        System.out.println(no++ + ". delete id = "+id+", result: " + delete);
        System.out.println(" [[[ ===>> Finish kk sharding test for id/name=" + id + "/" + name + "]]]");
        System.out.println("\n\n");
    }


//    // this is testing engine
//    @Autowired
//    ShardingProperties properties;
//
//    @Bean
//    ApplicationRunner runner() {
//        return x -> {
//            System.out.println("run...");
//            System.out.println(properties.toString());
//
//            String sql = "select * from t_user where id = ?";
//
//            ShardingEngine engine = new StandardShardingEngine(properties);
//            ShardingResult result = engine.sharding(sql, new Object[]{1});
//            System.out.println(result.toString());
//
//            result = engine.sharding(sql, new Object[]{2});
//            System.out.println(result.toString());
//
//        };
//    }


}
