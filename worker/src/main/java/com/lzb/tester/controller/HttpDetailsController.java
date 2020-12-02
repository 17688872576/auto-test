package com.lzb.tester.controller;

import com.lzb.tester.dto.BaseResult;
import com.lzb.tester.dto.MongoPageInfo;
import com.lzb.tester.dto.ResultCode;
import com.lzb.tester.entity.HttpEntity;
import com.lzb.tester.service.HttpDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class HttpDetailsController {

    @Autowired
    private HttpDetailsService<HttpEntity> service;

    @PostMapping("/save")
    @ResponseBody
    public BaseResult save(@RequestBody HttpEntity entity){
        HttpEntity httpEntity = service.save(entity);
        return httpEntity != null ? BaseResult.success("保存成功") : BaseResult.error("保存失败");
    }

    @DeleteMapping("/remove-batch")
    @ResponseBody
    public BaseResult remove(@RequestParam("idList") List<String> idList){
        Long remove = service.remove(idList);
        return remove != -1L ? BaseResult.success("删除成功") : BaseResult.error("删除失败");
    }

    @DeleteMapping("/remove/{id}")
    @ResponseBody
    public BaseResult remove(@PathVariable("id") String id){
        Long remove = service.remove(id);
        return remove != -1L ? BaseResult.success("删除成功") : BaseResult.error("删除失败");
    }

    @GetMapping("/findOne/{id}")
    @ResponseBody
    public BaseResult<HttpEntity> findOneById(@PathVariable("id") String id){
        HttpEntity entity = service.findOneById(id);
        return BaseResult.success(ResultCode.SUCCESS.getMsg(),entity);
    }

    @GetMapping("/findAllBy")
    @ResponseBody
    public BaseResult findAllBy(@RequestParam("index")Integer index,@RequestParam("size") Integer size,
                                @RequestParam("key") String key,@RequestParam("value") Object value){
        MongoPageInfo<HttpEntity> pageInfo = service.findAllBy(index, size, key, value);
        return BaseResult.success(ResultCode.SUCCESS.getMsg(),pageInfo);
    }

    @PutMapping("/update")
    @ResponseBody
    public BaseResult update(@RequestBody HttpEntity entity){
        Long update = service.update(entity);
        return update != -1L ? BaseResult.success("更新成功") : BaseResult.error("更新失败");
    }

    @GetMapping("/selectAll")
    @ResponseBody
    public BaseResult selectAll(@RequestParam("index")Integer index,@RequestParam("size") Integer size){
        MongoPageInfo<HttpEntity> pageInfo = service.selectAll(index, size);
        return BaseResult.success(ResultCode.SUCCESS.getMsg(),pageInfo);
    }
}
