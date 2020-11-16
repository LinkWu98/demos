package cn.link.common.util.image;

import org.apache.commons.lang3.StringUtils;

import java.io.File;

/**
 * 无可奈何创建的图片导出功能相关工具类
 *
 * @author Link50
 * @version 1.0
 * @date 2020/11/12 10:13
 */
public class ProductPicUtil {

    /**
     * 压缩图片的长和宽
     */
    public static final Integer PIC_SIZE = 500;

    /**
     * 超过该数量展示链接而非图片
     */
    public static final Integer MAX_DISPLAY_COUNT = 100;

    /**
     * 一下为分文件夹的字符串
     */
    public static final String O1CN01_STR = "O1CN01";
    public static final String TB_STR = "TB";

    /**
     * 服务器文件夹路径
     */
    //public static final String PIC_DIR_PATH = "/usr/local/pic/";

    private static final String PIC_DIR_PATH = "D:\\Test\\testPic\\";

    /**
     * 获取图片对象
     *
     * @param mainPicId 图片地址
     * @return
     */
    public static ProductPic getProductPic(String mainPicId) {

        if (StringUtils.isBlank(mainPicId)) {
            return null;
        }

        /*
            图片地址有两种情况，统一返回.jpg后缀
            1、 .SS2、download.action=xxx、没有后缀，该情况使用文件流请求
            2、 .jpg、.png 直接 URL 请求

         */

        String filepath = getFilepath(mainPicId);

        ProductPic productPic = new ProductPic();

        //1. 没有后缀 添加后缀
        if (filepath.lastIndexOf(".") == -1) {
            productPic.setFile(new File(PIC_DIR_PATH + "noSuffix/" + filepath.concat(ImageUtil.JPG)));
            productPic.setRequestFlag(true);
            return productPic;
        }

        //2. .SS2 改后缀
        if (filepath.endsWith(".SS2")) {
            productPic.setFile(new File(PIC_DIR_PATH + "ss2/" + filepath.substring(0, filepath.lastIndexOf(".")).concat(ImageUtil.JPG)));
            productPic.setRequestFlag(true);
            return productPic;
        }

        //3. download.action=xxx 取id=后的字符串，添加后缀
        if (filepath.contains("action")) {
            productPic.setFile(new File(PIC_DIR_PATH + "action/" + filepath.split("=")[1].concat(ImageUtil.JPG)));
            productPic.setRequestFlag(true);
            return productPic;
        }

        //4. .png 改为 .jpg
        if (filepath.endsWith(".png")) {
            productPic.setRequestFlag(false);
            productPic.setFile(new File(PIC_DIR_PATH + "png/" + filepath.substring(0, filepath.lastIndexOf(".")).concat(ImageUtil.JPG)));
            return productPic;
        }

        //5. .jpg结尾，直接返回
        productPic.setRequestFlag(false);
        productPic.setFile(new File(PIC_DIR_PATH + filepath));
        return productPic;

    }


    /**
     * 只给O1CN01前缀的路径拼接特别文件夹，其他都直接在上一层添加
     *
     * @param mainPicId
     * @return
     */
    private static String getFilepath(String mainPicId) {

        if (StringUtils.isBlank(mainPicId)) {
            return null;
        }

        String filepath = mainPicId.substring(mainPicId.lastIndexOf("/") + 1);

        if (filepath.startsWith(O1CN01_STR)) {

            //o1cn01后的字符作为文件夹名
            String dirName = filepath.substring(O1CN01_STR.length(), O1CN01_STR.length() + 1);
            filepath = dirName + "/" + filepath;

        } else if (filepath.startsWith(TB_STR)) {

            //TB后的字符作为文件夹名
            String dirName = filepath.substring(TB_STR.length(), TB_STR.length() + 1);
            filepath = dirName + "/" + filepath;

        }

        return filepath;

    }

}
