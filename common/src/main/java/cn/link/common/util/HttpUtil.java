package cn.link.common.util;


import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLContext;
import java.io.InputStream;
import java.net.URI;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

/**
 * @author Link
 * @version 1.0
 * @date 2020/11/10 15:50
 */
public class HttpUtil {

    private static final Logger log = LoggerFactory.getLogger(HttpUtil.class);
    private static final HttpGet GET = new HttpGet();
    private static HttpClient httpClient;

    static {

        try {

            SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(null, new TrustStrategy() {
                // 信任所有证书
                @Override
                public boolean isTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                    return true;
                }
            }).build();

            SSLConnectionSocketFactory sslFactory = new SSLConnectionSocketFactory(sslContext);

            httpClient = HttpClients.custom().setSSLSocketFactory(sslFactory).build();

        } catch (Exception e) {

            log.error("处理Https证书异常", e);
            httpClient = HttpClients.createDefault();

        }

    }

    /**
     * get 请求文件流
     *
     * @param url 地址
     * @return 文件流
     * @throws Exception
     */
    public static InputStream getForFile(String url) throws Exception {

        SSLUtil.trustAllHosts();
        GET.setURI(new URI(url));
        HttpResponse response = httpClient.execute(GET);
        HttpEntity entity = response.getEntity();
        return entity.getContent();

    }

}
