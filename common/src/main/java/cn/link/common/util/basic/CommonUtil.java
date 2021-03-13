package cn.link.common.util.basic;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Random;

public class CommonUtil {

    /**
     * 传入源数据，根据指定的概率和概率浮动范围，返回一个上下浮动结果
     *
     * @param source
     * @param rate
     * @param floatRandomRangeMin
     * @param floatRandomRangeMax
     * @return
     */
    public static int getNumByRandomRange(long source, int rate, int floatRandomRangeMin, int floatRandomRangeMax) {

        int operatorFlag = new Random().nextInt(2);
        double finalRate;

        if (operatorFlag == 0) {
            finalRate = (rate + ((new Random().nextInt(floatRandomRangeMax - floatRandomRangeMin + 1) + floatRandomRangeMin))) * 0.01;
        } else {
            finalRate = (rate - ((new Random().nextInt(floatRandomRangeMax - floatRandomRangeMin + 1) + floatRandomRangeMin))) * 0.01;
        }
        System.out.println("rate = " + finalRate);
        BigDecimal result = BigDecimal.valueOf(source).multiply(new BigDecimal(finalRate));

        return result.setScale(0, RoundingMode.HALF_UP).intValue();

    }

}
