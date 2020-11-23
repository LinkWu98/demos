package cn.link.basic;

import cn.link.common.util.basic.StringUtil;
import org.junit.Test;

/**
 * @author Link50
 * @version 1.0
 * @date 2020/11/13 9:54
 */
public class StringTest {

    @Test
    public void picStrTest() {

        String path = StringUtil.ProductPicStr.path;

        String o1cn01Img = StringUtil.ProductPicStr.COMMON_O1CN01_IMG;
        String substring = o1cn01Img.substring(o1cn01Img.lastIndexOf("/") + 1);
        if (substring.contains("O1CN01")) {
            String o1CN01 = substring.substring(substring.lastIndexOf("O1CN01"));
            System.out.println(o1CN01);
        }
        System.out.println(substring);


    }

    @Test
    public void testPer() {


        String wrongImg = StringUtil.ProductPicStr.WRONG_IMG2;
        String correct = wrongImg.substring(wrongImg.indexOf("%") + 1);
        System.out.println(correct);


    }

    @Test
    public void mkdir() {

        String o1cn01Img = StringUtil.ProductPicStr.COMMON_O1CN01_IMG.substring(StringUtil.ProductPicStr.COMMON_O1CN01_IMG.lastIndexOf("/") + 1);
        if (o1cn01Img.startsWith("O1CN01")) {
            String dirName = o1cn01Img.substring("O1CN01".length(), "O1CN01".length() + 1);
            System.out.println(dirName);
        }


    }


}
