package com.lzb.tester.entity;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.Map;

@Document
@Data
public class HttpEntity implements Serializable {

    @Id
    private String id;

    /**
     * 客户端id
     */
    private Integer cid;

    private String title;

    private String description;

    private String url;

    private String method;

    private Map<String,Object> params;

    private Map<String,String> headers;

    private Boolean isLogin;
}
