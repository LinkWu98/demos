package cn.link.basic;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author Link50
 * @version 1.0
 * @date 2021/4/20 10:18
 */
public class SynchronizedTest {

    private static int num = 1;

    public static void testSync(Long key) {

        if (num == 1) {

            synchronized (key) {

                if (num == 1) {
                    num++;
                    System.out.println(num);
                }


            }

        } else {
            System.out.println(num);
        }

    }

    public static void main(String[] args) {

        Executor executor = Executors.newFixedThreadPool(10);
        for (int i = 0; i < 10; i++) {

            executor.execute(() -> testSync(1L));

        }

        testSync(2L);

        Thread.yield();

    }

}
