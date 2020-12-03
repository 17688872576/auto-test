package com.lzb.tester.common;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class JdbcPool {

    private static final Map<Integer,JdbcUtil> map = new ConcurrentHashMap<>();

    public static JdbcUtil getPool(Integer cid){
        return map.get(cid);
    }

    public static void putPool(Integer cid,JdbcUtil jdbcUtil){
        map.put(cid,jdbcUtil);
    }

    public static void remove(Integer cid){
        map.remove(cid);
    }

    public static void remove(){
        map.clear();
    }
}
