package com.sleticalboy.dailywork;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {

    @Test
    public void subListTest() {
        List<String> dataList = new ArrayList<>();
        for (int i = 0; i < 30; i++) {
            dataList.add("test " + i);
        }
        System.out.println(dataList);
        List<String> subList = dataList.subList(0, 5);
        System.out.println(dataList);
        System.out.println(subList);
    }

    @Test
    public void ecbEcrypt() {
        System.out.println("test");
    }
}