package com.lzb.tester.common;

import com.lzb.tester.entity.HttpEntity;
import com.lzb.tester.entity.HttpResult;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

public class HttpMethodUtil {

    interface Method{
        String GET = "GET";
        String POST = "POST";
        String POST_OF_URL = "POST_OF_URL";
        String PUT = "PUT";
        String DELETE = "DELETE";
    }

    public static Map<String,Function<HttpEntity,HttpResult>> maps = new HashMap<>();

    static {
        maps.put(Method.GET,e->HttpUtil.get(e.getUrl(),e.getParams(),e.getHeaders()));
        maps.put(Method.POST,e->HttpUtil.post(e.getUrl(),e.getParams(),e.getHeaders()));
        maps.put(Method.POST_OF_URL,e->HttpUtil.postOfUrl(e.getUrl(),e.getParams(),e.getHeaders()));
        maps.put(Method.PUT,e->HttpUtil.put(e.getUrl(),e.getParams(),e.getHeaders()));
        maps.put(Method.DELETE,e->HttpUtil.delete(e.getUrl(),e.getParams(),e.getHeaders()));
    }
}
