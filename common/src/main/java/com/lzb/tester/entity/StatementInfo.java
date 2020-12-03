package com.lzb.tester.entity;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.io.Serializable;
import java.util.List;

@Data
@Document
@Builder
public class StatementInfo implements Serializable {

    @Id
    private String id;

    /**
     * 客户端id
     */
    private Integer cid;

    private String statement;

    private List<String> variableKeys;

    private String jdbcSource;

    private String resultKey;
}
