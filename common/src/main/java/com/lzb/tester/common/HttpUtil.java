package com.lzb.tester.common;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lzb.tester.entity.HttpResult;
import org.apache.commons.io.FileUtils;
import org.apache.http.*;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.*;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import javax.net.ssl.SSLException;
import javax.net.ssl.SSLHandshakeException;
import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class HttpUtil {

    /* 连接池manager */
    private static final PoolingHttpClientConnectionManager connManager = new PoolingHttpClientConnectionManager();

    /* 创建一个ThreadLocal，用来存放 CloseableHttpClient */
    private static ThreadLocal<CloseableHttpClient> threadLocal = new ThreadLocal<>();

    /* 默认超时时间为6秒 */
    private static final int timeOut = 6000;

    private static BasicCookieStore cookieStore = new BasicCookieStore();

    static {
        // 设置最大连接数
        connManager.setMaxTotal(200);
        // 设置每个连接的路由数
        connManager.setDefaultMaxPerRoute(20);
    }

    /**
     * 获取一个CloseableHttpClient 实例
     *
     * @return
     */
    private static CloseableHttpClient getRquest() {
        setThreadLocal();
        return threadLocal.get();
    }

    private static void setThreadLocal() {
        // 创建Http请求配置参数
        RequestConfig requestConfig = RequestConfig.custom()
                // 获取连接超时时间
                .setConnectionRequestTimeout(timeOut)
                // 请求超时时间
                .setConnectTimeout(timeOut)
                // 响应超时时间
                .setSocketTimeout(timeOut)
                .build();

        /**
         * 测出超时重试机制为了防止超时不生效而设置
         *  如果直接放回false,不重试
         *  这里会根据情况进行判断是否重试
         */
        HttpRequestRetryHandler retry = ((exception, executionCount, context) -> {
            if (executionCount >= 3) {// 如果已经重试了3次，就放弃
                return false;
            }
            if (exception instanceof NoHttpResponseException) {// 如果服务器丢掉了连接，那么就重试
                return true;
            }
            if (exception instanceof SSLHandshakeException) {// 不要重试SSL握手异常
                return false;
            }
            if (exception instanceof InterruptedIOException) {// 超时
                return true;
            }
            if (exception instanceof UnknownHostException) {// 目标服务器不可达
                return false;
            }
            if (exception instanceof ConnectTimeoutException) {// 连接被拒绝
                return false;
            }
            if (exception instanceof SSLException) {// ssl握手异常
                return false;
            }
            HttpClientContext clientContext = HttpClientContext.adapt(context);
            HttpRequest request = clientContext.getRequest();
            // 如果请求是幂等的，就再次尝试
            if (!(request instanceof HttpEntityEnclosingRequest)) {
                return true;
            }
            return false;
        });
        // 创建httpClient

        CloseableHttpClient httpClient = HttpClients.custom()
                // 把请求相关的超时信息设置到连接客户端
                .setDefaultRequestConfig(requestConfig)
                // 把请求重试设置到连接客户端
                .setRetryHandler(retry)
                // 配置连接池管理对象
                .setConnectionManager(connManager)
                .setDefaultCookieStore(cookieStore)
                .build();
        threadLocal.set(httpClient);
    }

    /**
     * get请求
     */
    public static HttpResult get(String url, Map<String, Object> params, Map<String, String> headers) {
        CloseableHttpClient httpClient = getRquest();
        HttpGet httpGet = new HttpGet();
        // 请求头设置
        Optional.ofNullable(headers).ifPresent(h -> h.forEach((k, v) -> httpGet.setHeader(k, v)));
        try {
            URIBuilder uriBuilder = new URIBuilder(url);
            Optional.ofNullable(params).ifPresent(p -> p.forEach((k, v) -> uriBuilder.setParameter(k, String.valueOf(v))));
            URI uri = uriBuilder.build();
            httpGet.setURI(uri);
            CloseableHttpResponse response = httpClient.execute(httpGet);
            return closeRequest(response);
        } catch (Exception e) {
            return new HttpResult("",500,e.getMessage());
        }
    }

    /**
     * post请求
     */
    public static HttpResult post(String url, Map<String, Object> params, Map<String, String> headers) {
        CloseableHttpClient httpClient = getRquest();
        HttpPost httpPost = new HttpPost(url);
        Optional.ofNullable(headers).ifPresent(h -> h.forEach((k, v) -> httpPost.setHeader(k, v)));
        StringBuilder builder = new StringBuilder();
        Optional.ofNullable(params).ifPresent(p -> {
            ObjectMapper mapper = new ObjectMapper();
            try {
                String s = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(p);
                httpPost.setEntity(new ByteArrayEntity(s.getBytes("UTF-8")));
            } catch (Exception e) {
                builder.append(e.getMessage()).append("\t");
            }
        });
        try {
            CloseableHttpResponse response = httpClient.execute(httpPost);
            return closeRequest(response);
        } catch (IOException e) {
            builder.append(e.getMessage());
            return new HttpResult("",500,builder.toString());
        }
    }

    /**
     * 参数拼接在url后面的post请求
     */
    public static HttpResult postOfUrl(String url, Map<String,Object> params, Map<String, String> headers) {
        CloseableHttpClient httpClient = getRquest();
        HttpPost httpPost = new HttpPost(url);
        List<BasicNameValuePair> nameValuePairs = params.entrySet().stream()
                .map(e -> new BasicNameValuePair(e.getKey(), String.valueOf(e.getValue())))
                .collect(Collectors.toList());
        try {
            Optional.ofNullable(nameValuePairs).ifPresent(p-> {
                try {
                    httpPost.setEntity(new UrlEncodedFormEntity(p, "UTF-8"));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            });
            Optional.ofNullable(headers).ifPresent(h->h.forEach((k, v) -> httpPost.setHeader(k, v)));
            CloseableHttpResponse response = httpClient.execute(httpPost);
            return closeRequest(response);
        } catch (Exception e) {
            return new HttpResult("",500,e.getMessage());
        }
    }

    public static HttpResult put(String url, Map<String, Object> params, Map<String, String> headers) {
        CloseableHttpClient httpClient = getRquest();
        HttpPut httpPut = new HttpPut(url);
        Optional.ofNullable(params).ifPresent(arg -> {
            ObjectMapper mapper = new ObjectMapper();
            try {
                String s = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(arg);
                httpPut.setEntity(new ByteArrayEntity(s.getBytes()));
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        });
        Optional.ofNullable(headers).ifPresent(arg -> arg.forEach((k, v) -> httpPut.setHeader(k, v)));
        try {
            CloseableHttpResponse response = httpClient.execute(httpPut);
            return closeRequest(response);
        } catch (IOException e) {
            return new HttpResult("",500,e.getMessage());
        }
    }

    public static HttpResult delete(String url, Map<String, Object> params, Map<String, String> headers) {
        CloseableHttpClient httpClient = getRquest();
        HttpDelete httpDelete = new HttpDelete();
        try {
            URIBuilder uriBuilder = new URIBuilder(url);
            Optional.ofNullable(params).ifPresent(p->p.forEach((k,v)->uriBuilder.setParameter(k,String.valueOf(v))));
            URI uri = uriBuilder.build();
            httpDelete.setURI(uri);
            Optional.ofNullable(headers).ifPresent(h->h.forEach((k,v)->httpDelete.setHeader(k,v)));
            CloseableHttpResponse response = httpClient.execute(httpDelete);
            return closeRequest(response);
        } catch (Exception e) {
            return new HttpResult("",500,e.getMessage());
        }
    }

    /**
     * 上传文件post请求
     */
    public static HttpResult postMultipart(String url, Map<String, String> headers, MultipartEntityBuilder builder) {
        CloseableHttpClient httpClient = getRquest();
        HttpPost httpPost = new HttpPost(url);
        Optional.ofNullable(headers).orElse(new HashMap<>()).forEach((k, v) -> httpPost.setHeader(k, v));
        MultipartEntityBuilder opBuilder = Optional.ofNullable(builder).orElse(MultipartEntityBuilder.create());
        httpPost.setEntity(opBuilder.build());
        try {
            CloseableHttpResponse response = httpClient.execute(httpPost);
            return closeRequest(response);
        } catch (Exception e) {
            return new HttpResult("",500,e.getMessage());
        }
    }

    /**
     * 向一个图片地址发起请求，存到服务器
     */
    public static void getImgRequest(String url, String filePathName) {
        CloseableHttpClient httpClient = getRquest();
        HttpGet httpGet = new HttpGet(url);
        CloseableHttpResponse response = null;
        try {
            response = httpClient.execute(httpGet);
            HttpEntity entity = response.getEntity();
            InputStream inputStream = entity.getContent();
            FileUtils.copyToFile(inputStream, new File(filePathName));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                response.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 关闭连接
     */
    private static HttpResult closeRequest(CloseableHttpResponse response) {
        try {
            HttpResult result = new HttpResult();
            HttpEntity entity = response.getEntity();
            String content = EntityUtils.toString(entity, "UTF-8");
            int statusCode = response.getStatusLine().getStatusCode();
            Header[] allHeaders = response.getAllHeaders();
            result.setStatus(statusCode);
            result.setContent(content);
            result.setHeaders(Arrays.toString(allHeaders));
            result.setCookies(cookieStore.getCookies());
            return result;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            try {
                response.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
