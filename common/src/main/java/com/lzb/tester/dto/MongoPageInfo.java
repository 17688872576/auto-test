package com.lzb.tester.dto;

import lombok.Builder;
import lombok.ToString;

import java.util.List;

@Builder
@ToString
public class MongoPageInfo<T> {
    private Long totalSize;
    private Integer totalPage;
    private List<T> content;
    private Integer size;
}
