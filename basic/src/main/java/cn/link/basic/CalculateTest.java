package cn.link.basic;

import org.junit.Test;

/**
 * @author Link
 * @version 1.0
 * @date 2020/11/15 0:33
 * @description 计算
 */
public class CalculateTest {

    @Test
    public void calculateTest(){

        int num = 99;
        System.out.println((num++ % 100));
        System.out.println(num);

    }

}
