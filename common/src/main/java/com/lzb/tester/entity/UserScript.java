package com.lzb.tester.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import java.io.Serializable;
import java.util.List;

@Data
public class UserScript implements Serializable {

    @Id
    private String id;

    private String username;

    private String created;

    private List<String> events;
}
