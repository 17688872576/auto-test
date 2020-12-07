package com.lzb.tester.service;

import com.lzb.tester.dto.UserVariables;
import com.lzb.tester.entity.ClientInfo;

import java.util.List;

public interface WorkerService {

    /**
     * 执行任务
     * @param clientInfo 客户端详情
     * @return 返回结果集
     */
    List<Object> execute(ClientInfo clientInfo);

    /**
     * 添加任务
     * @param cid 客户端hashcode
     * @param task 任务
     */
    void addTask(Integer cid, Object task);

    /**
     * 选择数据库源
     * @param cid 客户端hashcode
     * @param id 数据源id
     */
    void selectJdbcSource(Integer cid, String id);

    /**
     * 添加变量
     * @param variable 变量实体
     * @return 返回结果字符串
     */
    String addVariable(UserVariables variable);
}
