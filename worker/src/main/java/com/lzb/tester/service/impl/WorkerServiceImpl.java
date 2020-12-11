package com.lzb.tester.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lzb.tester.common.*;
import com.lzb.tester.dto.RegexHandleInfo;
import com.lzb.tester.dto.UserVariables;
import com.lzb.tester.entity.*;
import com.lzb.tester.service.WorkerService;
import org.apache.http.cookie.Cookie;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class WorkerServiceImpl implements WorkerService {

    private Logger log = LoggerFactory.getLogger(WorkerServiceImpl.class);

    @Autowired
    private MongoTemplate template;

    private Map<Integer, Queue> taskQueue = JdbcPool.taskQueue;
    private Map<Integer, Map<String, Object>> variableMap = JdbcPool.variableMap;

    @Async
    public List<Object> execute(ClientInfo clientInfo) {
        Integer cid = clientInfo.getId();
        Queue queue = taskQueue.get(cid);
        List<Object> resultList = new ArrayList<>();
        Map<String, String> headers = new HashMap<>();
        Optional.ofNullable(queue).ifPresent(q -> {
            int size = q.size();
            for (int i = 0; i < size; i++) {
                Object poll = q.poll();
                if (poll instanceof HttpEntity) {
                    HttpEntity entity = (HttpEntity) poll;
                    /* 请求头处理(把使用了变量的参数值替换成对应的值) */
                    httpHeadersHandle(cid, entity);
                    /* 请求参数处理(把使用了变量的参数值替换成对应的值) */
                    httpParametersHandle(cid, entity);
                    /* 执行http请求，并返回结果，如果是登录请求，保存cookie到其他的请求 */
                    HttpResult result = executeHttpResult(headers, entity);
                    /* 如果是登录请求，判断返回的结果或者headers存不存在token，有的话存到用户变量*/
                    if (entity.getIsLogin() && !entity.getTokenKey().equals(""))
                        saveToken(cid, result, entity.getTokenKey());
                    resultList.add(result);
                } else if (poll instanceof StatementInfo) {
                    Object result = executeJdbcRequest(cid, (StatementInfo) poll);
                    resultList.add(result);
                } else if (poll instanceof RegexHandleInfo) {
                    String result = regexHandle((RegexHandleInfo) poll);
                    resultList.add(result);
                }
            }
            taskQueue.remove(cid);
        });
        return resultList;
    }

    public void addTask(Integer cid, Object task) {
        if (taskQueue.get(cid) == null) {
            taskQueue.put(cid, new ConcurrentLinkedQueue());
        }
        Queue queue = taskQueue.get(cid);
        queue.add(task);
        taskQueue.put(cid, queue);
        log.info("任务 {} 已添加", task);
    }

    public void selectJdbcSource(Integer cid, String id) {
        if (JdbcPool.getPool(cid) == null) {
            Query query = new Query(Criteria.where("_id").is(id));
            JdbcConnectInfo info = template.findOne(query, JdbcConnectInfo.class);
            JdbcUtil jdbcUtil = new JdbcUtil();
            Optional.ofNullable(info).ifPresent(f -> {
                jdbcUtil.selectDataSource(f);
                JdbcPool.putPool(cid, jdbcUtil);
                log.info("数据源已切换：{}", f.getJdbcSource());
            });
        }
    }

    public String addVariable(UserVariables variable) {
        try {
            if (variableMap.get(variable.getCid()) == null) {
                variableMap.put(variable.getCid(), new HashMap<>());
            }
            Map<String, Object> map = variableMap.get(variable.getCid());
            map.put(variable.getKey(), variable.getValue());
            variableMap.put(variable.getCid(), map);
            log.info("变量：{}={} 已添加", variable.getKey(), variable.getValue());
            return "success";
        } catch (Exception e) {
            log.error("添加变量时出现了错误：{}", e.getMessage());
            return e.getMessage();
        }
    }

    private String regexHandle(RegexHandleInfo regexInfo) {
        Integer cid = regexInfo.getCid();
        Map<String, Object> objectMap = variableMap.get(cid);
        StringBuilder builder = new StringBuilder();
        Optional.ofNullable(objectMap.get(regexInfo.getSourceKey())).ifPresent(obj -> {
            if (obj instanceof String) {
                String value = (String) obj;
                Matcher matcher = Pattern.compile(regexInfo.getRegex()).matcher(value);
                if (matcher.find()) {
                    String group = matcher.group();
                    String resultKey = regexInfo.getResultKey();
                    UserVariables variables = new UserVariables(regexInfo.getCid(), resultKey, group);
                    String s = addVariable(variables);
                    builder.append("[regexHandle] ").append(s);
                    log.info("正则表达式处理器：{}",s);
                }
            }
        });
        return builder.toString();
    }

    private Object executeJdbcRequest(Integer cid, StatementInfo statementInfo) {
        JdbcUtil jdbcUtil = Optional.ofNullable(JdbcPool.getPool(cid)).orElseThrow(() -> new RuntimeException("未选择数据库源!"));
        String statement = statementInfo.getStatement();
        String type = statementInfo.getExecuteType();
        Map<String, Object> map = variableMap.get(cid);
        List<String> variableKeys = statementInfo.getVariableKeys();
        Queue<Object> args = new LinkedBlockingQueue<>();
        variableKeys.stream().forEach(k -> args.add(map.get(k)));
        if (type.equals(JdbcUtil.SELECT)) {
            List<Map<String, Object>> maps = jdbcUtil.executeSelect(statement, args);
            Optional.ofNullable(maps).ifPresent(res -> {
                ObjectMapper mapper = new ObjectMapper();
                try {
                    String str = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(res);
                    UserVariables var = new UserVariables(cid, statementInfo.getResultKey(), str);
                    addVariable(var);
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                    log.error("JdbcRequest错误：{}", e.getMessage());
                }
            });
            return maps;
        } else if (type.equals(JdbcUtil.UPDATE)) {
            return jdbcUtil.excuteUpdate(statement, args);
        } else return null;
    }

    private HttpResult executeHttpResult(Map<String, String> headers, HttpEntity httpEntity) {
        if (headers.size() > 0) {
            Map<String, String> map = Optional.ofNullable(httpEntity.getHeaders()).orElse(new HashMap<>());
            map.putAll(headers);
            httpEntity.setHeaders(map);
        }
        HttpResult result = null;
        try {
            result = HttpMethodUtil.maps.get(httpEntity.getMethod()).apply(httpEntity);
        } catch (NullPointerException e) {
            result = new HttpResult(null, 500, e.getMessage());
            log.error("未选择正确的请求方式");
        }
        if (httpEntity.getIsLogin()) {
            List<Cookie> cookies = result.getCookies();
            saveHeaders(headers, cookies);
        }
        return result;
    }

    private void httpParametersHandle(Integer cid, HttpEntity httpEntity) {
        Map<String, Object> map = variableMap.get(cid);
        Map<String, Object> params = httpEntity.getParams();
        Map<String, Object> newParameters = new HashMap<>();
        Optional.ofNullable(params).ifPresent(p ->
                p.entrySet().stream().map(entry -> {
                    Object value = entry.getValue();
                    Object o = map.get(value);
                    Optional.ofNullable(o).ifPresent(v -> entry.setValue(v));
                    return entry;
                }).forEach(entry -> newParameters.put(entry.getKey(), entry.getValue()))
        );
        httpEntity.setParams(newParameters);
    }

    private void httpHeadersHandle(Integer cid, HttpEntity httpEntity) {
        Map<String, Object> map = variableMap.get(cid);
        Map<String, String> headers = httpEntity.getHeaders();
        Map<String, String> newHeaders = new HashMap<>();
        Optional.ofNullable(headers).ifPresent(h ->
                h.entrySet().stream().map(entry -> {
                    String value = entry.getValue();
                    Object o = map.get(value);
                    Optional.ofNullable(o).ifPresent(v -> entry.setValue((String) v));
                    return entry;
                }).forEach(entry -> newHeaders.put(entry.getKey(), entry.getValue())));
        httpEntity.setHeaders(newHeaders);
    }

    private void saveHeaders(Map<String, String> headers, List<Cookie> cookies) {
        StringBuilder builder = new StringBuilder();
        cookies.forEach(c -> {
            String name = c.getName();
            String value = c.getValue();
            builder.append(name).append("=").append(value).append(";");
        });
        headers.put("Cookie", builder.toString());
        log.info("cookie已保存");
    }

    /**
     * 存到用户的变量中
     *
     * @param cid      客户端id
     * @param result   http返回结果
     * @param tokenKey token的变量名
     */
    private void saveToken(Integer cid, HttpResult result, String tokenKey) {
        Optional.ofNullable(result.getHeaders()).ifPresent(h -> {
            if (h.contains("token")){
                addVariable(new UserVariables(cid, tokenKey, h));
                log.info("token已存入变量:{}",tokenKey);
            }
        });
        Optional.ofNullable(result.getContent()).ifPresent(c -> {
            if (c.contains("token")){
                addVariable(new UserVariables(cid, tokenKey, c));
                log.info("token已存入变量:{}",tokenKey);
            }
        });
    }
}
