package com.lzb.tester.service.impl;

import com.lzb.tester.dto.MongoPageInfo;
import com.lzb.tester.entity.WebElementInfo;
import com.lzb.tester.service.WebElementService;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class WebElementServiceImpl implements WebElementService<WebElementInfo> {
    @Override
    public WebElementInfo save(WebElementInfo entity) {
        return null;
    }

    @Override
    public Long remove(List<String> idList) {
        return null;
    }

    @Override
    public Long remove(String id) {
        return null;
    }

    @Override
    public WebElementInfo findOneById(String id) {
        return null;
    }

    @Override
    public MongoPageInfo<WebElementInfo> findAllBy(Integer index, Integer size, String key, Object value) {
        return null;
    }

    @Override
    public MongoPageInfo<WebElementInfo> selectAll(Integer index, Integer size) {
        return null;
    }

    @Override
    public Long update(WebElementInfo entity) {
        return null;
    }
}
