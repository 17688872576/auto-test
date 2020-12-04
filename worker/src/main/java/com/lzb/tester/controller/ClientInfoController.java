package com.lzb.tester.controller;

import com.lzb.tester.dto.BaseResult;
import com.lzb.tester.entity.ClientInfo;
import com.lzb.tester.service.ClientInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/client-info")
public class ClientInfoController extends ManageController<ClientInfo>{

//    @Autowired
//    private Map<Integer,ClientInfo> clientContainer;

    @Autowired
    public ClientInfoController(ClientInfoService<ClientInfo> service) {
        super.service = service;
    }

    @PostMapping("/register")
    @ResponseBody
    public BaseResult register(HttpServletRequest request){
        ClientInfo info = new ClientInfo();
        info.setId(request.hashCode());
        return super.save(info);
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
    public BaseResult<ClientInfo> findOneById(@RequestParam("id") String id){
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
    public BaseResult update(@RequestBody ClientInfo entity){
        return super.update(entity);
    }

    @GetMapping("/selectAll")
    @ResponseBody
    public BaseResult selectAll(@RequestParam("index")Integer index,@RequestParam("size") Integer size){
        return super.selectAll(index, size);
    }

}
