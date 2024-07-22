package io.github.kimmking.kksharding.demo;

import io.github.kimmking.kksharding.ShardingAutoConfiguration;
import io.github.kimmking.kksharding.demo.mapper.UserMapper;
import io.github.kimmking.kksharding.mybatis.ShardingMapperFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

import java.util.Map;

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
            System.out.println("Run kk sharding test for mybatis CRUD...");

            int delete = mapper.delete(1);
            System.out.println("1. delete id = 1, result: " + delete);

            int th = mapper.insert(1, "th");
            System.out.println("2. insert id = 1, name=th, result: " + th);

            Map<String, String> map = mapper.findById(1);
            System.out.println("3. select/findById id=1, and name: "+map.get("name"));

            int kk = mapper.update("kk", 1);
            System.out.println("4. update id=1, name=kk, result: "+kk);

            map = mapper.findById(1);
            System.out.println("5. select/findById id=1, and name: "+map.get("name"));

            System.out.println("Run all test completely.");

        };
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
