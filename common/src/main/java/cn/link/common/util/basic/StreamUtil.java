package cn.link.common.util.basic;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * 针对流的工具类
 *
 * @author Link50
 * @version 1.0
 * @date 2020/11/19 13:51
 */
public class StreamUtil {

    /**
     * 根据对象的某个属性过滤
     *
     * @param keyExtractor
     * @param <T>
     * @return
     */
    public static <T> Predicate<T> distinctByKey(Function<? super T, Object> keyExtractor) {
        Map<Object, Boolean> seen = new ConcurrentHashMap<>();
        return object -> seen.putIfAbsent(keyExtractor.apply(object), Boolean.TRUE) == null;
    }

    public static void main(String[] args) {

        int total = 12;
        int rate = 77;
        int randomRateRange = 7;

        System.out.println(getNumByRandomRange(total, rate, randomRateRange));
    }

    public static int getNumByRandomRange(long source, int rate, int floatRandomRange) {

        double finalRate = ((rate - floatRandomRange) + (new Random().nextInt(floatRandomRange * 2) + 1)) * 0.01;

        System.out.println(finalRate);
        BigDecimal result = BigDecimal.valueOf(source).multiply(new BigDecimal(finalRate));

        return result.setScale(0, RoundingMode.HALF_UP).intValue();

    }

}
