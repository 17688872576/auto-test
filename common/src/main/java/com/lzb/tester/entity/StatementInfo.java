package com.lzb.tester.entity;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.io.Serializable;
import java.util.List;

@Data
@Document
public class StatementInfo implements Serializable {

    @Id
    private String id;

    /**
     * 客户端id
     */
    private Integer cid;

    private String statement;

    private String executeType;

    private List<String> variableKeys;

    private String jdbcSource;

    private String resultKey;

    private String type;
}
