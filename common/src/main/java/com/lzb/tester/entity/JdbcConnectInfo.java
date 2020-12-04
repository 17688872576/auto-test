package com.lzb.tester.entity;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.io.Serializable;

@Document
@Data
public class JdbcConnectInfo implements Serializable {

    @Id
    private String id;

    private String url;

    private String username;

    private String password;

    private String jdbcSource;

    private Integer maxActive;

    private Integer minIdle;

    private Integer initialSize;

    private Long maxWait;
}
