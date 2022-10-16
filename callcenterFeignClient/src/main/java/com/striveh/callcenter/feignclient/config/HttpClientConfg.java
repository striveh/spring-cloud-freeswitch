package com.striveh.callcenter.feignclient.config;

import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "common.httpclient.config")
public class HttpClientConfg {

    /** 连接数 */
    private Integer connectionManagerMaxTotal;
    /** 连接数 */
    private Integer connectionManagerMaxPerRoute;
    /** 超时时间 */
    private Integer connectionTimeOut;
    /** 超时时间 */
    private Integer socketTimeOut;
    /** 超时时间 */
    private Integer requestTimeout;

    @Bean
    public HttpClient httpClient(HttpClientBuilder httpClientBuilder){
        return httpClientBuilder.build();
    }

    @Bean
    public PoolingHttpClientConnectionManager poolingHttpClientConnectionManager(){
        PoolingHttpClientConnectionManager poolingHttpClientConnectionManager = new PoolingHttpClientConnectionManager();
        poolingHttpClientConnectionManager.setMaxTotal(connectionManagerMaxTotal);
        poolingHttpClientConnectionManager.setDefaultMaxPerRoute(connectionManagerMaxPerRoute);
        return poolingHttpClientConnectionManager;
    }

    @Bean
    public HttpClientBuilder httpClientBuilder(HttpClientConnectionManager connectionManager){
        RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(connectionTimeOut).setSocketTimeout(socketTimeOut)
                .setConnectionRequestTimeout(requestTimeout).build();
        HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();
        httpClientBuilder.setConnectionManager(connectionManager);
        httpClientBuilder.setDefaultRequestConfig(requestConfig);
        httpClientBuilder.setRetryHandler(new DefaultHttpRequestRetryHandler(0,false));
        return httpClientBuilder;
    }

    public Integer getConnectionManagerMaxTotal() {
        return connectionManagerMaxTotal;
    }

    public void setConnectionManagerMaxTotal(Integer connectionManagerMaxTotal) {
        this.connectionManagerMaxTotal = connectionManagerMaxTotal;
    }

    public Integer getConnectionManagerMaxPerRoute() {
        return connectionManagerMaxPerRoute;
    }

    public void setConnectionManagerMaxPerRoute(Integer connectionManagerMaxPerRoute) {
        this.connectionManagerMaxPerRoute = connectionManagerMaxPerRoute;
    }

    public Integer getConnectionTimeOut() {
        return connectionTimeOut;
    }

    public void setConnectionTimeOut(Integer connectionTimeOut) {
        this.connectionTimeOut = connectionTimeOut;
    }

    public Integer getSocketTimeOut() {
        return socketTimeOut;
    }

    public void setSocketTimeOut(Integer socketTimeOut) {
        this.socketTimeOut = socketTimeOut;
    }

    public Integer getRequestTimeout() {
        return requestTimeout;
    }

    public void setRequestTimeout(Integer requestTimeout) {
        this.requestTimeout = requestTimeout;
    }
}
