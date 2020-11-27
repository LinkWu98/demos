package cn.link.basic;

import org.junit.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;

/**
 * @author Link50
 * @version 1.0
 * @date 2020/11/19 9:39
 */
public class DecimalTest {

    @Test
    public void test(){

        Integer selectionNum = 31233;
        Integer broadcastNum = 22123;

        BigDecimal liveRate = new BigDecimal(broadcastNum).divide(new BigDecimal(selectionNum), 2, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(100));
        System.out.println(liveRate.toString().split("\\.")[0] + "%");
        System.out.println(liveRate);

    }

    @Test
    public void test2(){
        System.out.println(BigDecimal.ZERO);
    }

    @Test
    public void test4(){

        System.out.println(Integer.parseInt("100"));
        System.out.println(new BigDecimal("100.00").intValue());

    }

}
