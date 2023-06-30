package com.example.utils;

import com.example.vo.HttpResult;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.*;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.util.*;

@Component
public class HttpAPIUtil {
    @Autowired
    private CloseableHttpClient httpClient;

    @Autowired
    private RequestConfig config;

    // 编码格式。发送编码格式统一用UTF-8
    private static final String ENCODING = "UTF-8";


    /**
     * 不带参数的get请求，如果状态码为200，则返回body，如果不为200，则返回null
     *
     * @param url
     * @return
     * @throws Exception
     */
    public HttpResult doGet(String url) throws Exception {
        // 声明 http get 请求
        HttpGet httpGet = new HttpGet(url);

        // 装载配置信息
        httpGet.setConfig(config);

        // 发起请求
        CloseableHttpResponse response = this.httpClient.execute(httpGet);

        return new HttpResult(response.getStatusLine().getStatusCode(),
                EntityUtils.toString(response.getEntity(), "UTF-8"));

        // 判断状态码是否为200
//        if (response.getStatusLine().getStatusCode() == 200) {
//            // 返回响应体的内容
//            return EntityUtils.toString(response.getEntity(), "UTF-8");
//        }
//        return null;
    }

    /**
     * 带参数的get请求，如果状态码为200，则返回body，如果不为200，则返回null
     *
     * @param url
     * @return
     * @throws Exception
     */
    public HttpResult doGet(String url, Map<String, Object> map) throws Exception {
        URIBuilder uriBuilder = new URIBuilder(url);

        if (map != null) {
            // 遍历map,拼接请求参数
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                uriBuilder.setParameter(entry.getKey(), entry.getValue().toString());
            }
        }

        // 调用不带参数的get请求
        return this.doGet(uriBuilder.build().toString());

    }

    /**
     * Post请求
     * @param url 地址
     * @param headers  请求头
     * @param params  请求参数
     * @param params  url请求参数
     * @return
     */
    public HttpResult doPost(String url, Map<String, String> headers, Map<String, String> params,Map<String, String> UrlParams){
        // 获得Http客户端(可以理解为:你得先有一个浏览器;注意:实际上HttpClient与浏览器是不一样的)
        // 创建访问的地址
        HttpPost httpPost = postUrlParam(url,UrlParams);
        httpPost.setConfig(config);

        packageHeader(headers, httpPost);
        packageParam(headers, httpPost);

        // 响应模型
        CloseableHttpResponse response = null;
        try {
            // 由客户端执行(发送)Get请求
            response = httpClient.execute(httpPost);
            // 从响应模型中获取响应实体
            HttpEntity responseEntity = response.getEntity();
            return new HttpResult(response.getStatusLine().getStatusCode(),
                    EntityUtils.toString(responseEntity));
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            //5.回收链接到连接池
            if (null != response) {
                try {
                    EntityUtils.consume(response.getEntity());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    /**
     * Post请求
     * @param url 地址
     * @param headers 请求头
     * @param urlParams url请求参数
     * @return
     */
    public HttpResult doPost(String url, Map<String,String> headers,Map<String, String> urlParams){
        return doPost(url,headers,null,urlParams);
    }

    /**
     * Post请求
     * @param url 地址
     * @param urlParams url请求参数
     * @return
     */
    public HttpResult doPost(String url, Map<String, String> urlParams){
        return doPost(url,null,null,urlParams);
    }

    /**
     * Post请求
     * @param url 地址
     * @return
     */
    public HttpResult doPost(String url){
        return doPost(url,new HashMap<>());
    }

    /**
     * Post请求
     * @param url 地址
     * @param headers  请求头
     * @param params  url请求参数
     * @param json  json请求体
     * @return
     */
    public HttpResult doPost(String url, Map<String, String> headers, Map<String, String> params,String json){
        // 获得Http客户端(可以理解为:你得先有一个浏览器;注意:实际上HttpClient与浏览器是不一样的)
        // 创建访问的地址
        HttpPost httpPost = postUrlParam(url,params);
        httpPost.setConfig(config);
        if(headers==null){
            headers=new HashMap<>();
        }



        headers.put("Content-Type", "application/json;charset=utf8");

        packageHeader(headers, httpPost);
        // post请求是将参数放在请求体里面传过去的;这里将entity放入post请求体中
        try {
            StringEntity s = new StringEntity(json,"UTF-8");
            s.setContentEncoding("UTF-8");
            s.setContentType("application/json");
            httpPost.setEntity(s);
//            httpPost.setEntity(new StringEntity(json));
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 响应模型
        CloseableHttpResponse response = null;
        try {
            // 由客户端执行(发送)Get请求
            response = httpClient.execute(httpPost);
            // 从响应模型中获取响应实体
            HttpEntity responseEntity = response.getEntity();
            return new HttpResult(response.getStatusLine().getStatusCode(),
                    EntityUtils.toString(responseEntity));
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            //5.回收链接到连接池
            if (null != response) {
                try {
                    EntityUtils.consume(response.getEntity());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    /**
     * 简单json请求
     * @param url 地址
     * @param json  json请求体
     * @return
     */
    public HttpResult doPost(String url, String json){
        Map<String, String> headers=new HashMap<>();
        headers.put("Content-Type", "application/json;charset=utf8");
        return doPost(url,headers,null,json);
    }

    private HttpGet getUrlParam(String url, Map<String, String> params) {
        try{
            URIBuilder uriBuilder = new URIBuilder(url);
            if (params != null) {
                Set<Map.Entry<String, String>> entrySet = params.entrySet();
                for (Map.Entry<String, String> entry : entrySet) {
                    uriBuilder.setParameter(entry.getKey(), entry.getValue());
                }
            }
            // 创建Get请求
            return new HttpGet(uriBuilder.build());
        }catch (URISyntaxException e){
            throw new RuntimeException("url语法错误！");
        }
    }

    private HttpPost postUrlParam(String url, Map<String, String> params) {
        try{
            URIBuilder uriBuilder = new URIBuilder(url);
            if (params != null) {
                Set<Map.Entry<String, String>> entrySet = params.entrySet();
                for (Map.Entry<String, String> entry : entrySet) {
                    uriBuilder.setParameter(entry.getKey(), entry.getValue());
                }
            }
            // 创建Get请求
            return new HttpPost(uriBuilder.build());
        }catch (URISyntaxException e){
            throw new RuntimeException("url语法错误！");
        }
    }

    /**
     * Description: 封装请求头
     * @param params
     * @param httpMethod
     */
    private void packageHeader(Map<String, String> params, HttpRequestBase httpMethod) {
        // 封装请求头
        if (params != null) {
            Set<Map.Entry<String, String>> entrySet = params.entrySet();
            for (Map.Entry<String, String> entry : entrySet) {
                // 设置到请求头到HttpRequestBase对象中
                httpMethod.setHeader(entry.getKey(), entry.getValue());
            }
        }
    }

    /**
     * Description: 封装请求参数
     *
     * @param params
     * @param httpMethod
     * @throws UnsupportedEncodingException
     */
    private void packageParam(Map<String, String> params, HttpEntityEnclosingRequestBase httpMethod) {
        // 封装请求参数
        if (params != null) {
            List<NameValuePair> nvps = new ArrayList<NameValuePair>();
            Set<Map.Entry<String, String>> entrySet = params.entrySet();
            for (Map.Entry<String, String> entry : entrySet) {
                nvps.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
            }

            // 设置到请求的http对象中
            try {
                httpMethod.setEntity(new UrlEncodedFormEntity(nvps, ENCODING));
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException("不支持的地址格式！");
            }
        }
    }
}
