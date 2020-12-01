package cn.link.demo.thirdparty.cos.service;

import cn.link.demo.thirdparty.common.Properties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tool.link.util.cos.CosUtil;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * @author Link50
 * @version 1.0
 * @date 2020/12/1 14:56
 */
@Service
public class CosService {

    @Autowired
    Properties properties;

    /**
     * 上传
     */
    public void uploadFile() {

        CosUtil.initCOS(properties.getCosSecretId(), properties.getCosSecretKey());

        CosUtil.upload(properties.getCosBucketName(), "Pic/test.jpg", "D:\\Pic\\u=1078861629,3747050294&fm=26&gp=0.jpg");

        CosUtil.closeResource();

    }

    /**
     * 下载文件
     */
    public void downloadFile() throws IOException {

        CosUtil.initCOS(properties.getCosSecretId(), properties.getCosSecretKey());

        CosUtil.download4File(properties.getCosBucketName(), "Pic/test.jpg", "D:\\Pic\\test.jpg");

        CosUtil.closeResource();

    }
}
