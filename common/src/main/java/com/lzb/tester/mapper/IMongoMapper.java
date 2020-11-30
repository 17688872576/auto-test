package com.lzb.tester.mapper;

import java.util.List;
import java.util.Map;

public interface IMongoMapper<T> {

    T findByCol(String col, Object value);

    void update(String col,Object value, T t);

    void update(List<String> columns, T t);

    void remove(String key, Object value);

    void remove(Map<String, Object> conditions);

    void insertOne(T t);

    void insertMany(List<T> documents);

}
