package com.lzb.tester.common;

import com.alibaba.druid.pool.DruidDataSource;
import com.lzb.tester.entity.JdbcConnectInfo;
import com.mysql.cj.jdbc.Driver;
import java.sql.*;
import java.util.*;

public class JdbcUtil {

    private DruidDataSource dataSource = null;

    public void selectDataSource(JdbcConnectInfo connectInfo) {
        dataSource = new DruidDataSource();
        dataSource.setUrl(connectInfo.getUrl());
        try {
            dataSource.setDriver(new Driver());
            dataSource.setUsername(connectInfo.getUsername());
            dataSource.setPassword(connectInfo.getPassword());
            //设置最大连接数
            dataSource.setMaxActive(connectInfo.getMaxActive() == null ? 12 : connectInfo.getMaxActive());
            //设置最小的闲置连接数
            dataSource.setMinIdle(connectInfo.getMinIdle() == null ? 1 : connectInfo.getMinIdle());
            //设置初始的连接数
            dataSource.setInitialSize(connectInfo.getInitialSize() == null ? 2 : connectInfo.getInitialSize());
            //最长等待连接时间(MS)
            dataSource.setMaxWait(connectInfo.getMaxWait() == null ? 10000 : connectInfo.getMaxWait());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Connection getConn() {
        if (dataSource == null || dataSource.isClosed())
            throw new RuntimeException("数据库连接池未初始化!");
        try {
            System.out.println(dataSource.hashCode());
            return dataSource.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Map<String,Object>> executeSelect(String sql, Queue<Object> args){
        char[] chars = sql.toCharArray();
        int count = 0;
        for (int i = 0; i < chars.length; i++) {
            if (chars[i] == '?') ++count;
        }
        if (count == 0){
            return executeSelect(sql);
        }
        if (args.size() < count) throw new RuntimeException("sql语句里的变量和输入的变量不一致!");
        Connection connection = getConn();
        try {
            PreparedStatement prepareStatement = connection.prepareStatement(sql);
            for (int i = 1; i <= count; i++) {
                Object arg = args.poll();
                prepareStatement.setObject(i,arg);
            }
            ResultSet resultSet = prepareStatement.executeQuery();
            List<Map<String,Object>> dataList = new ArrayList<>();
            while (resultSet.next()){
                ResultSetMetaData metaData = resultSet.getMetaData();
                Map<String, Object> data = eachColumn(resultSet, metaData);
                dataList.add(data);
            }
            return dataList;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    private List<Map<String,Object>> executeSelect(String sql){
        Connection conn = getConn();
        try {
            PreparedStatement statement = conn.prepareStatement(sql);
            ResultSet resultSet = statement.executeQuery();
            List<Map<String,Object>> dataList = new ArrayList<>();
            while (resultSet.next()){
                ResultSetMetaData metaData = resultSet.getMetaData();
                Map<String, Object> data = eachColumn(resultSet, metaData);
                dataList.add(data);
            }
            return dataList;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    private boolean excuteUpdate(String sql){
        Connection connection = getConn();
        try {
            PreparedStatement statement = connection.prepareStatement(sql);
            return statement.execute();
        } catch (SQLException e) {
            return false;
        }
    }

    public boolean excuteUpdate(String sql,Queue<Object> args){
        char[] chars = sql.toCharArray();
        int count = 0;
        for (int i = 0; i < chars.length; i++) {
            if (chars[i] == '?') ++count;
        }
        if (count == 0) return excuteUpdate(sql);
        if (args.size() < count) throw new RuntimeException("sql语句里的变量和输入的变量不一致!");
        Connection connection = getConn();
        try {
            PreparedStatement statement = connection.prepareStatement(sql);
            for (int i = 1; i <= count; i++) {
                statement.setObject(i,args.poll());
            }
            return statement.execute();
        } catch (SQLException e) {
            return false;
        }
    }

    private static Map<String,Object> eachColumn(ResultSet resultSet, ResultSetMetaData metaData){
        Map<String,Object> data = new HashMap<>();
        try {
            int count = metaData.getColumnCount();
            for (int i = 1; i <= count; i++) {
                String name = metaData.getColumnName(i);
                Object value = resultSet.getObject(i);
                data.put(name,value);
            }
            return data;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void close(){
        dataSource.close();
    }

    public static final String SELECT = "SELECT";
    public static final String UPDATE = "UPDATE";



//    public static void main(String[] args) {
//        JdbcConnectInfo info = JdbcConnectInfo.builder().url("jdbc:mysql://rm-wz9ntplso9cl63qykwo.mysql.rds.aliyuncs.com:3306/abcpingjiae")
//                .username("lixuan")
//                .password("Ljw16Vl@")
//                .build();
//        selectDataSource(info);
//        String sql = "select * from fksdtb_user where username=? and usertype=?";
//        List<Map<String, Object>> maps = executeSelect(sql, "j_R2P2eVRdbg@test.new", 1);
//        System.out.println(maps);
//    }
}
