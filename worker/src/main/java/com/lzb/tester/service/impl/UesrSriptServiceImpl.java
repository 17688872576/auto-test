package com.lzb.tester.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lzb.tester.common.MongoUtils;
import com.lzb.tester.common.ObjectID;
import com.lzb.tester.dto.MongoPageInfo;
import com.lzb.tester.dto.UserScriptDTO;
import com.lzb.tester.entity.JdbcConnectInfo;
import com.lzb.tester.entity.UserScript;
import com.lzb.tester.service.UserScriptService;
import com.mongodb.client.result.DeleteResult;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class UesrSriptServiceImpl implements UserScriptService {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public UserScript insert(UserScriptDTO entity) {
        ObjectMapper mapper = new ObjectMapper();
        UserScript userScript = new UserScript();
        List<String> eventList = new ArrayList<>();
        List<Map<String, Object>> events = Optional.ofNullable(entity).map(e -> e.getEvents()).orElseGet(() -> new ArrayList<>());
        events.forEach(e->{
            try {
                String event = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(e);
                eventList.add(event);
            } catch (JsonProcessingException e1) {
                e1.printStackTrace();
            }
        });
        userScript.setId(ObjectID.randomString());
        userScript.setUsername(entity.getUsername());
        userScript.setCreated(entity.getCreated());
        userScript.setEvents(eventList);
        return mongoTemplate.save(userScript);
    }

    @Override
    public UserScriptDTO save(UserScriptDTO entity) {
        return null;
    }

    @Override
    public Long remove(List<String> idList) {
        return null;
    }

    @Override
    public Long remove(String id) {
        DeleteResult remove = mongoTemplate.remove(new Query(Criteria.where("_id").is(id)));
        return Optional.ofNullable(remove.getDeletedCount()).orElse(-1L);
    }

    @Override
    public UserScriptDTO findOneById(String id) {
        UserScript userScript = mongoTemplate.findOne(new Query(Criteria.where("_id").is(id)), UserScript.class);
        ObjectMapper mapper = new ObjectMapper();
        UserScriptDTO dto = new UserScriptDTO();
        List<Map<String,Object>> events = new ArrayList<>();
        userScript.getEvents().forEach(e->{
            try {
                HashMap<String,Object> hashMap = mapper.readValue(e, HashMap.class);
                events.add(hashMap);
            } catch (JsonProcessingException e1) {
                e1.printStackTrace();
            }
        });
        dto.setEvents(events);
        return dto;
    }

    @Override
    public MongoPageInfo<UserScriptDTO> findAllBy(Integer index, Integer size, String key, Object value) {
        return null;
    }

    @Override
    public MongoPageInfo<UserScriptDTO> selectAll(Integer index, Integer size) {
        if (index < 1) throw new RuntimeException("分页异常");
        Query query = new Query();
        long count = mongoTemplate.count(query, UserScript.class);
        PageRequest pageRequest = PageRequest.of(index-1, size);
        query.with(pageRequest).with(Sort.by(Sort.Direction.ASC,"sort"));
        List<UserScript> entities = mongoTemplate.find(query, UserScript.class);
        List<UserScriptDTO> resultList = new ArrayList<>();
        // 名称、创建人、创建时间
        entities.forEach(u->{
            UserScriptDTO userScriptDTO = new UserScriptDTO();
            BeanUtils.copyProperties(u,userScriptDTO);
            resultList.add(userScriptDTO);
        });
        Page<UserScriptDTO> page = PageableExecutionUtils.getPage(resultList, pageRequest, () -> count);
        return MongoUtils.pageInfoCopy(page);
    }

    @Override
    public Long update(UserScriptDTO entity) {
        return null;
    }


    public static void main(String[] args) {
        LocalDateTime now = LocalDateTime.now();
    }
}
