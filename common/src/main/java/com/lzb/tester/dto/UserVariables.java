package com.lzb.tester.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserVariables {
    private Integer cid;
    private String key;
    private Object value;
}
