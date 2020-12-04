package com.lzb.tester;

import com.lzb.tester.entity.HttpEntity;
import com.lzb.tester.service.WorkerService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;
import java.util.Map;

@SpringBootTest
public class WorkerTests {

    @Autowired
    private WorkerService service;

    @Test
    void suite(){
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

        service.addTask(123456,httpEntity);



    }
}
