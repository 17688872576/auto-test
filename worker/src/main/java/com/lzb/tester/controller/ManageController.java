package com.lzb.tester.controller;

import com.lzb.tester.dto.BaseResult;
import com.lzb.tester.dto.MongoPageInfo;
import com.lzb.tester.dto.ResultCode;
import com.lzb.tester.service.ManageService;
import java.util.List;

public class ManageController<T> {

    protected ManageService<T> service;

    public BaseResult save(T entity){
        T jdbcConnectInfo = service.save(entity);
        return jdbcConnectInfo != null ? BaseResult.success("保存成功") : BaseResult.error("保存失败");
    }

    public BaseResult remove(List<String> idList){
        Long remove = service.remove(idList);
        return remove != -1L ? BaseResult.success("删除成功") : BaseResult.error("删除失败");
    }

    public BaseResult remove(String id){
        Long remove = service.remove(id);
        return remove != -1L ? BaseResult.success("删除成功") : BaseResult.error("删除失败");
    }

    public BaseResult<T> findOneById(String id){
        T entity = service.findOneById(id);
        return BaseResult.success(ResultCode.SUCCESS.getMsg(),entity);
    }

    public BaseResult findAllBy(Integer index,Integer size, String key,Object value){
        MongoPageInfo<T> pageInfo = service.findAllBy(index, size, key, value);
        return BaseResult.success(ResultCode.SUCCESS.getMsg(),pageInfo);
    }

    public BaseResult update(T entity){
        Long update = service.update(entity);
        return update != -1L ? BaseResult.success("更新成功") : BaseResult.error("更新失败");
    }

    public BaseResult selectAll(Integer index,Integer size){
        MongoPageInfo<T> pageInfo = service.selectAll(index, size);
        return BaseResult.success(ResultCode.SUCCESS.getMsg(),pageInfo);
    }
}
