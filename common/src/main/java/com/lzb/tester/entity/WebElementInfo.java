package com.lzb.tester.entity;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document
@Builder
public class WebElementInfo {

    @Id
    private String id;

    private String title;

    private String description;



    private Integer sort;
}
