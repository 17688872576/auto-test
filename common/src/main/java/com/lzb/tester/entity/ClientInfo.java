package com.lzb.tester.entity;

import com.lzb.tester.dto.EventsHelper;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

@Document
@Data
@Builder
public class ClientInfo implements Serializable {

    @Id
    private Integer id;

    private Map<String,Object> variables;

    private String jdbcSource;

    private List<EventsHelper> events;
}
