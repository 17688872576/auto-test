package com.lzb.tester.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class RegexHandleInfo {
    @NotNull
    private Integer cid;
    @NotBlank(message = "正则表达式不能为空")
    private String regex;
    @NotBlank(message = "sourceKey不能为空")
    private String sourceKey;
    @NotBlank(message = "resultKey不能为空")
    private String resultKey;
}
