package com.lzb.tester.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.io.Serializable;

@Document
@Data
public class ClientInfo implements Serializable {

    @Id
    private Integer id;

    private String jdbcSource;

    private String username;
}
