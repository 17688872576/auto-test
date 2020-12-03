package com.lzb.tester;

import com.lzb.tester.common.HttpUtil;
import com.lzb.tester.common.JdbcUtil;
import com.lzb.tester.entity.HttpResult;
import com.lzb.tester.entity.JdbcConnectInfo;
import org.junit.jupiter.api.Test;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ApiTests {

    static {
        JdbcConnectInfo info = JdbcConnectInfo.builder().url("jdbc:mysql://rm-wz9ntplso9cl63qykwo.mysql.rds.aliyuncs.com:3306/abcpingjiae")
                .username("lixuan")
                .password("Ljw16Vl@").build();
        JdbcUtil.selectDataSource(info);
    }

    private static final String url = "http://47.107.83.33:1010";

    /**
     * 获取用户信息
     */
    @Test
    public void testGetUserInfo(){
        String username = "j_R2P2eVRdbg@test.new";
        Integer userId = getUserId(username);
        String api = url + "/api/UserInfo/getuserinfo";
        Map<String,Object> params = new HashMap<>();
        params.put("UserId",userId);
        params.put("Version",null);
        HttpUtil.get(api,params,null);

        List<HttpResult> httpResult = HttpUtil.getHttpResult();
        System.out.println("===========>"+httpResult.get(0).getContent());
    }

    /**
     * 获取用户留评率
     */
    @Test
    public void testGetReview(){
        String username = "j_R2P2eVRdbg@test.new";
        Integer userId = getUserId(username);
        String api = url + "/api/UserInfo/getuserinfos";
        Map<String,Object> params = new HashMap<>();
        params.put("UserId",userId);
        params.put("isHight",1);
        HttpUtil.get(api,params,null);

        List<HttpResult> httpResult = HttpUtil.getHttpResult();
        System.out.println("===========>"+httpResult.get(0).getContent());
    }


    public Integer getUserId(String username){
        String sql = "SELECT id FROM fksdtb_user WHERE UserName=? and usertype=1";
        List<Map<String, Object>> maps = JdbcUtil.executeSelect(sql, username);
        return (Integer)maps.get(0).get("Id");
    }
}
