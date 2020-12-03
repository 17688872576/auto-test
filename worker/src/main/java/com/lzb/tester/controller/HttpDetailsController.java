package com.lzb.tester.controller;

import com.lzb.tester.dto.BaseResult;

import com.lzb.tester.entity.HttpEntity;
import com.lzb.tester.service.HttpDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/http")
public class HttpDetailsController extends ManageController<HttpEntity>{

    @Autowired
    public HttpDetailsController(HttpDetailsService<HttpEntity> service) {
        super.service = service;
    }

    @PostMapping("/save")
    @ResponseBody
    public BaseResult save(@RequestBody HttpEntity entity){
        return super.save(entity);
    }

    @DeleteMapping("/remove-batch")
    @ResponseBody
    public BaseResult remove(@RequestParam("idList") List<String> idList){
        return super.remove(idList);
    }

    @DeleteMapping("/remove")
    @ResponseBody
    public BaseResult remove(@RequestParam("id") String id){
        return super.remove(id);
    }

    @GetMapping("/findOne")
    @ResponseBody
    public BaseResult<HttpEntity> findOneById(@RequestParam("id") String id){
        return super.findOneById(id);
    }

    @GetMapping("/findAllBy")
    @ResponseBody
    public BaseResult findAllBy(@RequestParam("index")Integer index, @RequestParam("size") Integer size,
                                @RequestParam("key") String key, @RequestParam("value") Object value){
        return super.findAllBy(index, size, key, value);
    }

    @PutMapping("/update")
    @ResponseBody
    public BaseResult update(@RequestBody HttpEntity entity){
        return super.update(entity);
    }

    @GetMapping("/selectAll")
    @ResponseBody
    public BaseResult selectAll(@RequestParam("index")Integer index,@RequestParam("size") Integer size){
        return super.selectAll(index, size);
    }
}
