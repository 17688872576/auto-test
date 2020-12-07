package com.lzb.tester;

import com.lzb.tester.common.JdbcUtil;
import com.lzb.tester.dto.UserVariables;
import com.lzb.tester.entity.ClientInfo;
import com.lzb.tester.entity.HttpEntity;
import com.lzb.tester.entity.JdbcConnectInfo;
import com.lzb.tester.entity.StatementInfo;
import com.lzb.tester.service.JdbcConnectInfoService;
import com.lzb.tester.service.WorkerService;
import com.lzb.tester.service.impl.WorkerServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SpringBootTest
public class WorkerTests {

    @Autowired
    private WorkerService service;
    @Autowired
    private JdbcConnectInfoService<JdbcConnectInfo> jdbcService;

    /**
     * 1、选择jdbc数据源
     * 2、添加变量
     * 3、添加任务，sql语句或者http请求中使用变量
     * 4、执行任务
     */
    @Test
    void suite(){

        service.selectJdbcSource(123456,"ff280f98-bfa2-496d-bdac-922f1becac09");

        UserVariables userVariables = new UserVariables();
        userVariables.setCid(123456);
        userVariables.setKey("$username");
        userVariables.setValue("j_qs1cabbv5d@test.new");
        service.addVariable(userVariables);

        UserVariables var2 = new UserVariables();
        var2.setCid(123456);
        var2.setKey("$password");
        var2.setValue("123456");
        service.addVariable(var2);

        String sql = "select id from fksdtb_user where username=?";

        List<String> variableName = new ArrayList<>();
        variableName.add("$username");

        StatementInfo statementInfo = new StatementInfo();
        statementInfo.setCid(123456);
        statementInfo.setExecuteType(JdbcUtil.SELECT);
        statementInfo.setId(UUID.randomUUID().toString());
        statementInfo.setJdbcSource("abcpingjiae");
        statementInfo.setStatement(sql);
        statementInfo.setVariableKeys(variableName);
        statementInfo.setResultKey("$userInfo");
        service.addTask(123456,statementInfo);



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

        ClientInfo info = new ClientInfo();
        info.setId(123456);
        info.setJdbcSource("abcpingjiae");
        service.execute(info);


        while (true){Thread.yield();}
    }

    public static void main(String[] args) {

    }
}
