package com.lzb.tester.entity;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Data
public class HttpResult {
    private String headers;
    private Integer status;
    private String content;

    public HttpResult(Integer status, String content) {
        this.status = status;
        this.content = content;
    }

    public HttpResult() {}
}
