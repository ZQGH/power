package com.example.demo.util;

import com.example.demo.domain.User;
import com.google.common.collect.Lists;
import org.junit.Test;
import java.util.List;
import java.util.stream.Collectors;

public class ListsUtilsTest {
    /**
     * 将大集合按指定大小拆分成多个集合
     * 场景: sqlserver遍历操作时,参数超过2000报错参数过多
     */
    @Test
    public void partitionTest() {
        List<String> list = Lists.newArrayList("a", "b", "c", "d", "e");
        List<List<String>> partition = Lists.partition(list, 2);
        for (List<String> strings :partition) {
            System.out.println(strings);
        }
    }
    /**
     * 筛选集合中指定两个属性到指定值
     */
    @Test
    public void propertySelectorTest() {
        String cona = "a";
//        String conb = "b";
        String conb = "";
        User a = new User("a",18,"a");
        User b = new User("b",18,"b");
        User c = new User("c",18,"c");
        User d = new User("d",18,"d");
        List<User> list = Lists.newArrayList(a,b,c,d);
        List<User> collect = list.stream().filter(x -> cona.equals(x.getUserName()) || conb.equals(x.getAddr())).collect(Collectors.toList());
        System.out.println(collect);
    }

}