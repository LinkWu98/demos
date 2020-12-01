package cn.link.demo.thirdparty.common;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * @author Link50
 * @version 1.0
 * @date 2020/12/1 14:25
 */
@Data
@PropertySource("classpath:application.yml")
@Configuration
public class Properties {

    @Value("${cos.secretId}")
    private String cosSecretId;

    @Value("${cos.secretKey}")
    private String cosSecretKey;

    @Value("${cos.bucketName}")
    private String cosBucketName;

}
