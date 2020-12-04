package com.lzb.tester.common;

import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;

public class JdbcPool {

    private static final Map<Integer,JdbcUtil> pool = new ConcurrentHashMap<>();

    public static Map<Integer, Queue> taskQueue = new ConcurrentHashMap<>();

    public static Map<Integer,Map<String,Object>> variableMap = new HashMap<>();

    public static JdbcUtil getPool(Integer cid){
        return pool.get(cid);
    }

    public static void putPool(Integer cid,JdbcUtil jdbcUtil){
        pool.put(cid,jdbcUtil);
    }

    public static void remove(Integer cid){
        pool.remove(cid);
    }

    public static void remove(){
        pool.clear();
    }

    public static Map<Integer,JdbcUtil> pool(){
        return pool;
    }
}
