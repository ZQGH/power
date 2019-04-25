package com.example.demo.test;

import lombok.Data;

import java.io.Serializable;
import java.util.Timer;

/**
 * @author ：chendaolong
 * @Description ：
 * @date ：2019/4/23
 */
@Data
public class User implements Serializable {
    private String name;
    private Integer age;
}
