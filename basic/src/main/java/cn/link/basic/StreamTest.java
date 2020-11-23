package cn.link.basic;

import cn.link.common.bean.Order;
import cn.link.common.util.basic.StreamUtil;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Link50
 * @version 1.0
 * @date 2020/11/19 13:55
 */
public class StreamTest {

    @Test
    public void test(){

        Order order1 = new Order("1", "1");
        Order order2 = new Order("2", "1");
        Order order3 = new Order("3", "2");
        Order order4 = new Order("4", "2");
        Order order5 = new Order("5", "2");

        List<Order> orderList = new ArrayList<>();

        orderList.add(order1);
        orderList.add(order2);
        orderList.add(order3);


        System.out.println(orderList.stream().filter(StreamUtil.distinctByKey(Order::getProductId)).count());


    }


}
