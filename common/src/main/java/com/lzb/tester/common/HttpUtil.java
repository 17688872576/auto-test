package com.lzb.tester.common;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lzb.tester.entity.HttpResult;
import org.apache.commons.io.FileUtils;
import org.apache.http.*;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.util.EntityUtils;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLHandshakeException;
import java.io.*;
import java.net.URI;
import java.net.UnknownHostException;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.Function;

public class HttpUtil {

    /* 连接池manager */
    private static final PoolingHttpClientConnectionManager connManager = new PoolingHttpClientConnectionManager();

    /* 创建一个ThreadLocal，用来存放 CloseableHttpClient */
    private static ThreadLocal<CloseableHttpClient> threadLocal = new ThreadLocal<>();

    private static final ThreadPoolExecutor threadPool = new ThreadPoolExecutor(1, 8, 2, TimeUnit.SECONDS, new LinkedBlockingQueue<>());

    private static List<Future<CloseableHttpResponse>> futureList = new CopyOnWriteArrayList<>();

    /* 默认超时时间为6秒 */
    private static final int timeOut = 6000;

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
                .build();
        threadLocal.set(httpClient);
    }

    /**
     * get请求
     */
    public static void get(String url, Map<String, Object> params, Map<String, String> headers) {
        CloseableHttpClient httpClient = getRquest();
        HttpGet httpGet = new HttpGet();
        // 请求头设置
        Map<String, String> opHeaders = Optional.ofNullable(headers).orElse(new HashMap<>());
        opHeaders.forEach((k, v) -> httpGet.setHeader(k, v));
        try {
            URIBuilder uriBuilder = new URIBuilder(url);
            URI uri = uriBuilder.build();
            httpGet.setURI(uri);
            Map<String, Object> opParams = Optional.ofNullable(params).orElse(new HashMap<>());
            opParams.forEach((k, v) -> uriBuilder.setParameter(k, String.valueOf(v)));
            Future<CloseableHttpResponse> future = threadPool.submit(() -> httpClient.execute(httpGet));
            futureList.add(future);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * post请求
     */
    public static void post(String url, Map<String, Object> params, Map<String, String> headers) {
        CloseableHttpClient httpClient = getRquest();
        HttpPost httpPost = new HttpPost(url);
        Optional.ofNullable(headers).orElse(new HashMap<>()).forEach((k, v) -> httpPost.setHeader(k, v));
        try {
            Map<String, Object> opParams = Optional.ofNullable(params).orElse(new HashMap<>());
            ObjectMapper mapper = new ObjectMapper();
            String s = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(opParams);
            httpPost.setEntity(new ByteArrayEntity(s.getBytes("UTF-8")));
            Future<CloseableHttpResponse> future = threadPool.submit(() -> httpClient.execute(httpPost));
            futureList.add(future);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 参数拼接在url后面的post请求
     */
    public static void postOfUrl(String url, List<NameValuePair> params, Map<String, String> headers) {
        CloseableHttpClient httpClient = getRquest();
        HttpPost httpPost = new HttpPost(url);
        try {
            List<NameValuePair> opParams = Optional.ofNullable(params).orElse(new ArrayList<>());
            httpPost.setEntity(new UrlEncodedFormEntity(opParams, "UTF-8"));
            Map<String, String> opHeaders = Optional.ofNullable(headers).orElse(new HashMap<>());
            opHeaders.forEach((k, v) -> httpPost.setHeader(k, v));
            Future<CloseableHttpResponse> future = threadPool.submit(() -> httpClient.execute(httpPost));
            futureList.add(future);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 上传文件post请求
     */
    public static void postMultipart(String url, Map<String, String> headers, MultipartEntityBuilder builder) {
        CloseableHttpClient httpClient = getRquest();
        HttpPost httpPost = new HttpPost(url);
        Optional.ofNullable(headers).orElse(new HashMap<>()).forEach((k, v) -> httpPost.setHeader(k, v));
        MultipartEntityBuilder opBuilder = Optional.ofNullable(builder).orElse(MultipartEntityBuilder.create());
        httpPost.setEntity(opBuilder.build());
        try {
            Future<CloseableHttpResponse> future = threadPool.submit(() -> httpClient.execute(httpPost));
            futureList.add(future);
        } catch (Exception e) {
            e.printStackTrace();
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

    public static List<HttpResult> getHttpResult() {
        List<HttpResult> resultList = new ArrayList<>();
        futureList.forEach(f -> {
            try {
                CloseableHttpResponse response = f.get();
                HttpResult httpResult = closeRequest(response);
                resultList.add(httpResult);
            } catch (InterruptedException e) {
                throw new RuntimeException(e.getMessage());
            } catch (ExecutionException e) {
                throw new RuntimeException(e.getMessage());
            }
        });
        futureList.clear();
        return resultList;
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

    private static Map<String,Function<Object,Object>> maps = new HashMap<>();
}
