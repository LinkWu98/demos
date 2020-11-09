package cn.link.excel.easyexcel;

import cn.link.common.exception.MyException;
import cn.link.common.util.image.ImageUtil;
import cn.link.excel.easyexcel.bean.ImageData;
import com.alibaba.excel.EasyExcel;
import org.junit.Test;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.*;
import java.net.URL;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * 写测试
 *
 * @author Link
 * @version 1.0
 * @date 2020/11/3 10:31
 */
public class WriteTest {

    private final String imageUrl = "https://img.alicdn.com/i3/2206490389990/O1CN01BjIipa2NfTGzlv1YZ_!!2206490389990.jpg";

    private final String path = "K:\\test\\img\\";

    public static void testException() throws MyException {

        throw new MyException("haha");

    }

    public static void trustAllHosts() {

        // Create a trust manager that does not validate certificate chains
        TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {

            @Override
            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                return new java.security.cert.X509Certificate[]{};
            }

            @Override
            public void checkClientTrusted(X509Certificate[] chain, String authType) {

            }

            @Override
            public void checkServerTrusted(X509Certificate[] chain, String authType) {

            }
        }};

        // Install the all-trusting trust manager
        try {
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Test
    public void localWriteTest() throws Exception {

        //1. 字节流读取远程 url 的图片
        trustAllHosts();
        URL url = new URL(imageUrl);
        InputStream imageStream = url.openConnection().getInputStream();

        //2. 压缩图片，输出到本地
        ImageUtil.compress(imageStream, new File("K:\\test\\test.jpg"), 150, 150);

    }

    /**
     * 通过流读取图片，转为字节数组，写入表格
     */
    @Test
    public void writeTest() {

        //忽略网页SSL认证
        trustAllHosts();
        List<ImageData> images = new ArrayList<>();

        try {
            //1. 字节流读取远程 url 的图片
            URL url = new URL(imageUrl);
            InputStream imageInput = url.openConnection().getInputStream();

            //2. 压缩图片，将返回的字节数组，设置给 excelData对象
            byte[] imageByte = ImageUtil.compress(imageInput, imageUrl.substring(imageUrl.lastIndexOf(".") + 1), 200, 200);

            ImageData imageData = new ImageData();
            imageData.setImageByteArray(imageByte);
            images.add(imageData);

            //3. 写 excel
            EasyExcel.write("K:\\test\\test.xlsx", ImageData.class).sheet().doWrite(images);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void regexTest() {

        String regex = "(https?|ftp|file)://[-A-Za-z0-9+&@#/%?=~_|!:,.;]+[-A-Za-z0-9+&@#/%=~_|]";

        System.out.println(Pattern.matches(regex, "https://img.alicdn.com/imgextra/i4/1818112330/O1CN01vnqQVh1T5BRZuEoCG_!!1818112330-0-lubanu-s.jpg"));

    }

    @Test
    public void thumbTest() {

        ImageUtil.commpressPicForScale("K:\\test\\origin.jpg", "K:\\test\\Thumbnails.jpg", 2, 0.9, 1000, 1000);

    }

    /**
     * 获取图片url，本地有就读本地的，没有就读url的
     */
    @Test
    public void localImgTest() throws Exception {

        trustAllHosts();

        long start = System.currentTimeMillis();

        List<ImageData> dataList = new ArrayList<>();
        String fileName = imageUrl.substring(imageUrl.lastIndexOf("/") + 1);
        for (int i = 0; i < 30000; i++) {

            ImageData data = new ImageData();
            File img = new File("K:\\test\\img\\" + fileName);

            //有就读本地的
            if (img.exists()) {

                data.setInputStream(new FileInputStream(img));

            } else {

                //没有就读url的
                InputStream in = new URL(imageUrl).openConnection().getInputStream();
                if (in != null) {
                    byte[] compress = ImageUtil.compress(in, "K:\\test\\img\\tetstse.jpg", 150, 150);
                    if (compress != null) {
                        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(compress);
                        ByteArrayOutputStream out = new ByteArrayOutputStream();
                        out.write(compress);
                        out.close();
                        data.setInputStream(byteArrayInputStream);
                    }
                }

            }

            dataList.add(data);

        }

        long cost = (System.currentTimeMillis() - start) / 1000 / 60;

        //3. 写 excel
        EasyExcel.write("K:\\test\\big.xlsx", ImageData.class).sheet().doWrite(dataList);

        System.out.println("花费时间:" + cost);

    }


    @Test
    public void testWrite() throws Exception {

        trustAllHosts();

        String filename = imageUrl.substring((imageUrl.lastIndexOf(".") + 1));
        List<ImageData> dataList = new ArrayList<>();

        //1.有就直接给
        File file = new File(path + filename);

        if (!file.exists()) {
            //2.没有就读url的,压缩图片，输出到本地
            InputStream in = new URL(imageUrl).openConnection().getInputStream();
            ImageUtil.compress(in, new File("K:\\test\\test80.jpg"), 80, 80);
            //ImageUtil.compress(in, file, 150, 150);
        }

        for (int i = 0; i < 10; i++) {
            ImageData imageData = new ImageData();
            imageData.setInputStream(new FileInputStream(new File("K:\\test\\test80.jpg")));
            dataList.add(imageData);
        }

        //3. 写 excel
        EasyExcel.write("K:\\test\\big.xlsx", ImageData.class).sheet().doWrite(dataList);

    }


}
