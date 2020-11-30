package com.lzb.tester.service;

import com.lzb.tester.entity.HttpEntity;

import java.util.List;
import java.util.Map;

public interface IHttpDetailsService {

    void save(HttpEntity entity);

    void save(List<HttpEntity> details);

    void remove(Map<String,Object> relations);

    void remove(String col);

    HttpEntity findBy(String by);

    List<HttpEntity> findBys(String by);

    void update(HttpEntity entity);

    void updateBy(UpdateHttpEntity update);

    class UpdateHttpEntity{
        Map<String,Object> relations;
        HttpEntity entity;
    }
}
