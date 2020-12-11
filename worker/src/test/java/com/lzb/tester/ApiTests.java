package com.lzb.tester;

import com.lzb.tester.common.HttpUtil;
import com.lzb.tester.common.JdbcUtil;
import com.lzb.tester.entity.HttpResult;
import com.lzb.tester.entity.JdbcConnectInfo;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.stream.Stream;

public class ApiTests {

    static {
        JdbcConnectInfo info = new JdbcConnectInfo();
//        JdbcConnectInfo info = JdbcConnectInfo.builder().url("jdbc:mysql://rm-wz9ntplso9cl63qykwo.mysql.rds.aliyuncs.com:3306/abcpingjiae")
//                .username("lixuan")
//                .password("Ljw16Vl@").build();
//        JdbcUtil.selectDataSource(info);
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

    }


    public Integer getUserId(String username){
        String sql = "SELECT id FROM fksdtb_user WHERE UserName=? and usertype=1";
//        List<Map<String, Object>> maps = JdbcUtil.executeSelect(sql, username);
//        return (Integer)maps.get(0).get("Id");
        return null;
    }


    public static void main(String[] args) {
        int arr[] = new int[100000];
        Random random = new Random();
        for (int i = 0; i < 100000; i++) {
            int index = random.nextInt(100000);
            arr[i] = index;
        }

        long start = System.currentTimeMillis();
        Arrays.sort(arr);
        int[] ints = Stream.of(arr).distinct().findAny().get();
        System.out.println("size:"+ints.length);
        int i = byBinarySearch(ints);
        long end = System.currentTimeMillis();
        System.out.println("花费"+(end-start)+"毫秒");
        System.out.println(i);
    }

    public Integer getNumFromData(List<Integer> nums,int target){
        int left = 0;
        int right = nums.size();
        while (left < right){
            int mid = left + (right-left) / 2;
            int val = nums.get(mid);
            if (target > val) left = mid +1;
            else if (target < val) right = mid -1;
            else if (target == val) return mid;
        }
        return -1;
    }

    /**
     * 条件：1、有序数组(必备条件，不满足这个条件会陷入死循环！)
     *      2、去重（不能有重复数字，不然达不到预计效果）
     * 目标：寻找一个数组中不相邻的两位数
     * 二分法
     * 时间复杂度：O(log n)
     */
    public static int byBinarySearch(int[] arr){
        if (arr.length <= 1) return -1;
        int left = 0;
        int right = arr.length - 1;
        while (left < right - 1){
            int mid = left + (right-left) / 2;
            int leftDiff = mid - left;
            int rightDiff = right - mid;
            if (arr[mid] - arr[left] > leftDiff) right = mid;
            else if (arr[right] - arr[mid] > rightDiff) left = mid;
            else if (arr[right] - arr[mid] == rightDiff && arr[mid] - arr[left] == leftDiff) return -1;
        }
        // 不相邻的两位数左索引和右索引分别是left和right
        System.out.println(left+"=="+right);
        // 返回两数之差
        return arr[right] - arr[left];
    }

    /**
     * 条件：有序数组
     * 目标：寻找一个数组中不相邻的两位数
     * 快速排序思路
     * 时间复杂度：O(n log n)
     */
//    public static int byQuickSort(int[] arr){
//        if (arr.length <= 1) return -1;
//        int left = 0,right = arr.length - 1;
//        while (left < right){
//            int leftDiff = arr[left + 1] - arr[left];
//            int rightDiff = arr[right] - arr[right - 1];
//            while (left < right && leftDiff == 1) left ++;
//            while (left < right && rightDiff == 1) right --;
//            if ()
//        }
//    }

}
