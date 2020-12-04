package com.lzb.tester.entity;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.apache.http.cookie.Cookie;

import java.util.List;

@Data
public class HttpResult {
    private String headers;
    private Integer status;
    private String content;
    List<Cookie> cookies;

    public HttpResult(String headers, Integer status, String content) {
        this.headers = headers;
        this.status = status;
        this.content = content;
    }

    public HttpResult() {}

    public List<Cookie> getCookies() {
        return cookies;
    }

    public void setCookies(List<Cookie> cookies) {
        this.cookies = cookies;
    }
}
