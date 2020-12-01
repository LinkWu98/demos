package tool.link.util.cos;

import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.auth.BasicCOSCredentials;
import com.qcloud.cos.auth.COSCredentials;
import com.qcloud.cos.exception.CosClientException;
import com.qcloud.cos.model.*;
import com.qcloud.cos.region.Region;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * 腾讯云对象存储工具类
 * <p>
 * init 和 close 请一起使用 ~
 *
 * @author Link50
 * @version 1.0
 * @date 2020/12/1 15:09
 */
public class CosUtil {

    private static COSClient cosClient = null;

    /**
     * 初始化 cos 客户端
     * <p>
     * COSClient是线程安全的类,
     * 内部维持了一个连接池,
     * 创建多个实例可能导致程序资源耗尽,
     * 请确保程序生命周期内实例只有一个
     */
    public static void initCOS(String cosSecretId, String cosSecretKey) {

        COSCredentials cred = new BasicCOSCredentials(cosSecretId, cosSecretKey);
        //1.1 设置 bucket 的区域, COS 地域的简称请参照 https://cloud.tencent.com/document/product/436/6224
        Region region = new Region("ap-shanghai");
        //1.2 clientConfig 中包含了设置 region, https(默认 http), 超时, 代理等 set 方法, 使用可参见源码或者常见问题 Java SDK 部分。
        ClientConfig clientConfig = new ClientConfig(region);
        //1.3 生成 cos 客户端
        cosClient = new COSClient(cred, clientConfig);

    }

    /**
     * 本地文件上传
     *
     * @param bucketName    桶名称 - 格式：BucketName-APPID
     * @param key           对象键（Key）是对象在存储桶中的唯一标识。
     *                      例如，在对象的访问域名 examplebucket-1250000000.cos.ap-guangzhou.myqcloud.com/doc/picture.jpg 中，对象键为 doc/picture.jpg
     * @param localFilePath 本地文件路径
     * @return bucket
     */
    public static void upload(String bucketName, String key, String localFilePath) {

        File file = new File(localFilePath);
        if (!file.exists()) {
            return;
        }

        PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, key, file);
        PutObjectResult putObjectResult = cosClient.putObject(putObjectRequest);
        //简单上传接口返回对象的 MD5 值
        System.out.println(putObjectResult.getETag());

    }

    /**
     * 上传对象
     * <p>
     * 本地文件上传
     *
     * @param bucketName    桶名称 - 格式：BucketName-APPID
     * @param key           对象键（Key）是对象在存储桶中的唯一标识。
     *                      例如，在对象的访问域名 examplebucket-1250000000.cos.ap-guangzhou.myqcloud.com/doc/picture.jpg 中，对象键为 doc/picture.jpg
     * @param in            资源流
     * @param contentLength 提前告知输入流的长度
     * @return bucket
     */
    public static void upload(String bucketName, String key, InputStream in, Integer contentLength) {

        if (in == null) {
            return;
        }

        PutObjectRequest putObjectRequest;
        if (contentLength != null && contentLength > 0) {
            ObjectMetadata metadata = new ObjectMetadata();
            //提前告知输入流的长度, 否则可能导致 oom
            metadata.setContentLength(contentLength);
            putObjectRequest = new PutObjectRequest(bucketName, key, in, metadata);
        } else {
            putObjectRequest = new PutObjectRequest(bucketName, key, in, null);
        }

        PutObjectResult putObjectResult = cosClient.putObject(putObjectRequest);
        //简单上传接口返回对象的 MD5 值
        System.out.println(putObjectResult.getETag());

    }

    /**
     * 下载文件，返回对应的流
     *
     * @param bucketName 桶名称 - 格式：BucketName-APPID
     * @param key        对象键（Key）是对象在存储桶中的唯一标识。
     *                   例如，在对象的访问域名 examplebucket-1250000000.cos.ap-guangzhou.myqcloud.com/doc/picture.jpg 中，对象键为 doc/picture.jpg
     * @return
     */
    public static InputStream download4Stream(String bucketName, String key) {

        if (cosClient == null) {
            return null;
        }

        // Bucket的命名格式为 BucketName-APPID ，此处填写的存储桶名称必须为此格式
        // 方法1 获取下载输入流
        GetObjectRequest getObjectRequest = new GetObjectRequest(bucketName, key);
        // 限流使用的单位是 bit/s, 这里设置下载带宽限制为 10MB /s
        getObjectRequest.setTrafficLimit(80 * 1024 * 1024);
        COSObject cosObject = cosClient.getObject(getObjectRequest);
        COSObjectInputStream cosObjectInput = cosObject.getObjectContent();

        // 服务端根据分块内容计算出来的 CRC64
        String crc64Ecma = cosObject.getObjectMetadata().getCrc64Ecma();
        System.out.println(crc64Ecma);

        // 关闭输入流
        //cosObjectInput.close();

        return cosObjectInput;

    }

    /**
     * 下载文件，将文件存于本地
     *
     * @param bucketName 桶名称 - 格式：BucketName-APPID
     * @param key        对象键（Key）是对象在存储桶中的唯一标识。
     *                   例如，在对象的访问域名 examplebucket-1250000000.cos.ap-guangzhou.myqcloud.com/doc/picture.jpg 中，对象键为 doc/picture.jpg
     * @return 是否下载成功
     */
    public static boolean download4File(String bucketName, String key, String outputFilePath) throws IOException {

        if (cosClient == null) {
            return false;
        }

        boolean flag;

        File downFile = new File(outputFilePath);
        if (!downFile.exists()) {
            if (downFile.getParentFile() != null && !downFile.getParentFile().exists()) {
                flag = downFile.getParentFile().mkdirs();
                if (!flag) {
                    return false;
                }
            }

            flag = downFile.createNewFile();
            if (!flag) {
                return false;
            }
        }

        // Bucket的命名格式为 BucketName-APPID ，此处填写的存储桶名称必须为此格式
        // 方法1 获取下载输入流
        GetObjectRequest getObjectRequest = new GetObjectRequest(bucketName, key);
        // 限流使用的单位是 bit/s, 这里设置下载带宽限制为 10MB /s
        getObjectRequest.setTrafficLimit(80 * 1024 * 1024);
        ObjectMetadata downObjectMeta = cosClient.getObject(getObjectRequest, downFile);

        return true;

    }

    /**
     * 关闭资源
     */
    public static void closeResource() {

        if (cosClient != null) {
            cosClient.shutdown();
        }

    }

    /**
     * 创建桶
     *
     * @param bucketName 桶名称 - 格式：BucketName-APPID
     * @return bucket
     */
    public Bucket createBucket(String bucketName) {

        if (cosClient == null) {
            return null;
        }
        CreateBucketRequest createBucketRequest = new CreateBucketRequest(bucketName);
        // 设置 bucket 的权限为 Private(私有读写), 其他可选有公有读私有写, 公有读写
        createBucketRequest.setCannedAcl(CannedAccessControlList.Private);
        try {
            return cosClient.createBucket(createBucketRequest);
        } catch (CosClientException serverException) {
            serverException.printStackTrace();
        } finally {
            cosClient.shutdown();
        }

        return null;

    }

}
