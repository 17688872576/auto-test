package com.lzb.tester;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lzb.tester.common.ObjectID;
import com.lzb.tester.dto.UserScriptDTO;
import com.lzb.tester.entity.*;
import com.lzb.tester.service.UserScriptService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@SpringBootTest
public class WorkerTests {

    @Autowired
    private UserScriptService userScriptService;

    /**
     * 1、选择jdbc数据源
     * 2、添加变量
     * 3、添加任务，sql语句或者http请求中使用变量
     * 4、执行任务
     */
    @Test
    void suite(){

//        service.selectJdbcSource(123456,"ff280f98-bfa2-496d-bdac-922f1becac09");
//
//        UserVariables userVariables = new UserVariables();
//        userVariables.setCid(123456);
//        userVariables.setKey("$username");
//        userVariables.setValue("j_qs1cabbv5d@test.new");
//        service.addVariable(userVariables);
//
//        UserVariables var2 = new UserVariables();
//        var2.setCid(123456);
//        var2.setKey("$password");
//        var2.setValue("123456");
//        service.addVariable(var2);
//
//        String sql = "select id from fksdtb_user where username=?";
//
//        List<String> variableName = new ArrayList<>();
//        variableName.add("$username");
//
//        StatementInfo statementInfo = new StatementInfo();
//        statementInfo.setCid(123456);
//        statementInfo.setExecuteType(JdbcUtil.SELECT);
//        statementInfo.setId(UUID.randomUUID().toString());
//        statementInfo.setJdbcSource("abcpingjiae");
//        statementInfo.setStatement(sql);
//        statementInfo.setVariableKeys(variableName);
//        statementInfo.setResultKey("$userInfo");
//        service.addTask(123456,statementInfo);



//        Map<String,Object> params = new HashMap<>();
//        params.put("UserName","$username");
//        params.put("PassWord","$password");
//        params.put("Checked",1);
//        params.put("Recovery",false);
//
//        Map<String,String> headers = new HashMap<>();
//
//        HttpEntity httpEntity = new HttpEntity();
//        httpEntity.setUrl("http://t.rebatest.com/User/UserLogin");
//        httpEntity.setMethod("GET");
//        httpEntity.setIsLogin(true);
//        httpEntity.setCid(123456);
//        httpEntity.setParams(params);
//        httpEntity.setHeaders(headers);
//
//        service.addTask(123456,httpEntity);
//
//        HttpEntity isLogin = new HttpEntity();
//        isLogin.setUrl("http://t.rebatest.com/user/IsLogin");
//        isLogin.setMethod("GET");
//        isLogin.setIsLogin(false);
//        isLogin.setCid(123456);
//        isLogin.setHeaders(headers);
//
//        service.addTask(123456,isLogin);
        int cid = ObjectID.randomInt();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        UserScriptDTO dto = new UserScriptDTO();
        dto.setId(ObjectID.randomString());
        dto.setUsername("彭于晏江西分晏");
        dto.setCreated(formatter.format(LocalDateTime.now()));

        HttpEntity httpEntity = new HttpEntity();
        httpEntity.setId(ObjectID.randomString());
        httpEntity.setCid(cid);
        httpEntity.setType("http");
        httpEntity.setUrl("http://www.baidu.com");
        httpEntity.setMethod("GET");

        List<Map<String,Object>> eventList = new ArrayList<>();

        ObjectMapper mapper = new ObjectMapper();
        try {
            String json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(httpEntity);
            HashMap<String,Object> hashMap = mapper.readValue(json, HashMap.class);

            StatementInfo statementInfo = new StatementInfo();
            statementInfo.setId(ObjectID.randomString());
            statementInfo.setCid(cid);
            statementInfo.setType("jdbc");
            statementInfo.setStatement("select * from users");
            statementInfo.setJdbcSource("user");
            String statement = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(statementInfo);
            HashMap<String,Object> statementMap = mapper.readValue(statement, HashMap.class);

            eventList.add(hashMap);
            eventList.add(statementMap);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        dto.setEvents(eventList);

        UserScript insert = userScriptService.insert(dto);
        System.out.println(insert);

//        dto.setId(UUID.fromString());
//
//        userScriptService.insert()
    }
}
