package cn.link.basic;

import cn.link.common.util.CollectionUtil;
import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author Link50
 * @version 1.0
 * @date 2020/11/25 13:39
 */
public class DataTest {

    @Test
    public void testWeekDate() {

        //设置日期格式
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar calendar = Calendar.getInstance(Locale.CHINA);

        //以周一为首日
        calendar.setFirstDayOfWeek(Calendar.MONDAY);

        //当前时间
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);

        //周一
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        System.out.println("周一" + simpleDateFormat.format(calendar.getTime()));

        //周日
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
        System.out.println("周日" + simpleDateFormat.format(calendar.getTime()));

    }

    @Test
    public void testMonthDate() {
        Calendar calendar = Calendar.getInstance(Locale.CHINA);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        System.out.println("本月第最后天" + calendar.getTime());

        calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH));
        System.out.println(calendar.getTime());
    }

    @Test
    public void test(){

        Calendar instance = Calendar.getInstance();
        instance.setTime(new Date());
        instance.set(Calendar.HOUR_OF_DAY, 0);
        instance.set(Calendar.MINUTE, 0);
        instance.set(Calendar.SECOND, 0);

        System.out.println(instance.getTime());

    }

}
