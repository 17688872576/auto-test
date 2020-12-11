package com.lzb.tester.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserVariables {
    @NotNull(message = "客户端id不能为空")
    private Integer cid;
    @NotBlank(message = "变量名不能为空")
    private String key;
    @NotBlank(message = "变量值不能为空")
    private Object value;
}
