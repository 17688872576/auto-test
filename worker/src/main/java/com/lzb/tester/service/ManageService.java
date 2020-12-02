package com.lzb.tester.service;

import com.lzb.tester.dto.MongoPageInfo;
import java.util.List;

public interface ManageService<T> {

    T save(T entity);

    Long remove(List<String> idList);

    Long remove(String id);

    T findOneById(String id);

    MongoPageInfo<T> findAllBy(Integer index, Integer size, String key, Object value);

    MongoPageInfo<T> selectAll(Integer index, Integer size);

    Long update(T entity);
}
