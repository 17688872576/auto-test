package com.lzb.tester.dto;

import lombok.Builder;
import lombok.Data;
import java.io.Serializable;

@Data
@Builder
public class EventsHelper implements Serializable {

    private String event;

    /**
     * http请求id
     */
    private String hid;

    /**
     * jdbc请求id
     */
    private String jid;

    /**
     * 排序值
     */
    private Integer sort;
}
