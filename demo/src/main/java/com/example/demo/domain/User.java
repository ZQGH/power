package com.example.demo.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
* @Author:         zq
* @CreateDate:     2019/4/26 9:48
* @Description:    User model
*/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    private String userName;
    private Integer age;
    private String addr;
}
