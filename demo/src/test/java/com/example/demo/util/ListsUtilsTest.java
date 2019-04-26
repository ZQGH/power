package com.example.demo.util;

import com.google.common.collect.Lists;
import org.junit.Test;

import java.util.List;


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

}