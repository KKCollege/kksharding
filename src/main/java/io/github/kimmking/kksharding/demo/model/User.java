package io.github.kimmking.kksharding.demo.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * entity model.
 *
 * @Author : kimmking(kimmking@apache.org)
 * @create 2024/7/23 上午12:02
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    private int id;
    private String name;
    private int age;
}
