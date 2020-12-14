package com.lzb.tester.dto;

import lombok.Data;
import java.util.List;
import java.util.Map;

@Data
public class UserScriptDTO {

    private String id;

    private String username;

    private String created;

    private List<Map<String,Object>> events;
}
