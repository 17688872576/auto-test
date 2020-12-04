package com.lzb.tester.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lzb.tester.common.*;
import com.lzb.tester.dto.UserVariables;
import com.lzb.tester.entity.*;
import org.apache.http.cookie.Cookie;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;


@Service
public class WorkerService {

    @Autowired
    private MongoTemplate template;

    private Map<Integer, Queue> taskQueue = JdbcPool.taskQueue;
    private Map<Integer,Map<String,Object>> variableMap = JdbcPool.variableMap;

    @Async
    public List<Object> execute(ClientInfo clientInfo) {
        Integer cid = clientInfo.getId();
        Queue queue = taskQueue.get(cid);
        List<Object> resultList = new ArrayList<>();
        Map<String,String> headers = new HashMap<>();
        Optional.ofNullable(queue).ifPresent(q -> {
            int size = q.size();
            for (int i = 0; i < size; i++) {
                Object poll = q.poll();
                if (poll instanceof HttpEntity) {
                    HttpEntity httpEntity = (HttpEntity) poll;
                    httpEntity.getHeaders().putAll(headers);
                    HttpResult result = HttpMethodUtil.maps.get(httpEntity.getMethod()).apply(httpEntity);
                    if (httpEntity.getIsLogin()){
                        List<Cookie> cookies = result.getCookies();
                        saveHeaders(headers,cookies);
                    }
                    resultList.add(result);
                } else if (poll instanceof StatementInfo) {
                    StatementInfo statementInfo = (StatementInfo) poll;
                    Object result = executeJdbcRequest(cid, statementInfo);
                    resultList.add(result);
                }
            }
        });

        taskQueue.remove(cid);
        return resultList;
    }

    public void addTask(Integer cid, Object task) {
        if (taskQueue.get(cid) == null) {
            taskQueue.put(cid, new ConcurrentLinkedQueue());
        }
        Queue queue = taskQueue.get(cid);
        queue.add(task);
        taskQueue.put(cid,queue);
    }

    public void selectJdbcSource(Integer cid, String id) {
        if (JdbcPool.getPool(cid) == null) {
            Query query = new Query(Criteria.where("_id").is(id));
            JdbcConnectInfo info = template.findOne(query, JdbcConnectInfo.class);
            JdbcUtil jdbcUtil = new JdbcUtil();
            jdbcUtil.selectDataSource(info);
            JdbcPool.putPool(cid, jdbcUtil);
        }
    }

    public String addVariable(UserVariables variable){
        try {
            if (variableMap.get(variable.getCid()) == null){
                variableMap.put(variable.getCid(),new HashMap<>());
            }
            Map<String, Object> map = variableMap.get(variable.getCid());
            map.put(variable.getKey(),variable.getValue());
            variableMap.put(variable.getCid(),map);
            return "success";
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    private Object executeJdbcRequest(Integer cid,StatementInfo statementInfo){
        JdbcUtil jdbcUtil = Optional.ofNullable(JdbcPool.getPool(cid)).orElseThrow(() -> new RuntimeException("未选择数据库源!"));
        String statement = statementInfo.getStatement();
        String type = statementInfo.getExecuteType();
        Map<String, Object> map = variableMap.get(cid);
        List<String> variableKeys = statementInfo.getVariableKeys();
        Queue<Object> args = new LinkedBlockingQueue<>();
        variableKeys.stream().forEach(k->args.add(map.get(k)));
        if (type.equals(JdbcUtil.SELECT)){
            return jdbcUtil.executeSelect(statement,args);
        } else if (type.equals(JdbcUtil.UPDATE)){
            return jdbcUtil.excuteUpdate(statement,args);
        }else return null;
    }

    private void saveHeaders(Map<String,String> headers,List<Cookie> cookies){
        StringBuilder builder = new StringBuilder();
        cookies.forEach(c->{
            String name = c.getName();
            String value = c.getValue();
            builder.append(name).append("=").append(value).append(";");
        });
        headers.put("Cookie",builder.toString());
    }

    public static void main(String[] args) {
        Map<String,Object> params = new HashMap<>();
        params.put("UserName","j_qs1cabbv5d%40test.new");
        params.put("PassWord","123456");
        params.put("Checked",1);
        params.put("Recovery",false);
//        HttpResult httpResult = HttpUtil.get("http://t.rebatest.com/User/UserLogin", params, null);
//        System.out.println(httpResult.getHeaders());

        HttpEntity httpEntity = new HttpEntity();
        httpEntity.setUrl("http://t.rebatest.com/User/UserLogin");
        httpEntity.setMethod("GET");
        httpEntity.setIsLogin(true);
        httpEntity.setCid(123456);
        httpEntity.setParams(params);




    }
}
