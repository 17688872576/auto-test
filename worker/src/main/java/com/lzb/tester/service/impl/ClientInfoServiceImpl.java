package com.lzb.tester.service.impl;

import com.lzb.tester.common.MongoUtils;
import com.lzb.tester.dto.MongoPageInfo;
import com.lzb.tester.entity.ClientInfo;
import com.lzb.tester.service.ClientInfoService;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class ClientInfoServiceImpl implements ClientInfoService<ClientInfo> {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public ClientInfo save(ClientInfo entity) {
        ClientInfo info = mongoTemplate.findOne(new Query(Criteria.where("username").is(entity.getUsername())), ClientInfo.class);
        if (info == null){
            return mongoTemplate.insert(entity);
        }
        return null;
    }

    @Override
    public Long remove(List<String> idList) {
        AtomicLong count = new AtomicLong();
        idList.forEach(id -> {
            Long remove = remove(id);
            count.addAndGet(remove);
        });
        return Optional.ofNullable(count.get()).orElse(-1L);
    }

    @Override
    public Long remove(String id) {
        Query query = new Query(Criteria.where("_id").is(id));
        DeleteResult result = mongoTemplate.remove(query, ClientInfo.class);
        return Optional.ofNullable(result.getDeletedCount()).orElse(-1L);
    }

    @Override
    public ClientInfo findOneById(String id) {
        return mongoTemplate.findOne(new Query(Criteria.where("_id").is(id)), ClientInfo.class);
    }

    @Override
    public MongoPageInfo<ClientInfo> findAllBy(Integer index,Integer size,String key,Object value) {
        if (index < 1) throw new RuntimeException("分页异常");
        Query query = new Query(Criteria.where(key).is(value));
        long count = mongoTemplate.count(query, ClientInfo.class);
        PageRequest pageRequest = PageRequest.of(index-1, size);
        query.with(pageRequest).with(Sort.by(Sort.Direction.ASC,"sort"));
        List<ClientInfo> entities = mongoTemplate.find(query, ClientInfo.class);
        Page<ClientInfo> page = PageableExecutionUtils.getPage(entities, pageRequest, () -> count);
        return MongoUtils.pageInfoCopy(page);
    }

    @Override
    public Long update(ClientInfo entity) {
        Update update = new Update();
        MongoUtils.filedsCopy(entity,update);
        UpdateResult result = mongoTemplate.upsert(new Query(Criteria.where("_id").is(entity.getId())), update, ClientInfo.class);
        return Optional.ofNullable(result.getModifiedCount()).orElse(-1L);
    }


    @Override
    public MongoPageInfo<ClientInfo> selectAll(Integer index, Integer size) {
        if (index < 1) throw new RuntimeException("分页异常");
        Query query = new Query();
        long count = mongoTemplate.count(query, ClientInfo.class);
        PageRequest pageRequest = PageRequest.of(index-1, size);
        query.with(pageRequest).with(Sort.by(Sort.Direction.ASC,"sort"));
        List<ClientInfo> entities = mongoTemplate.find(query, ClientInfo.class);
        Page<ClientInfo> page = PageableExecutionUtils.getPage(entities, pageRequest, () -> count);
        return MongoUtils.pageInfoCopy(page);
    }

    @Override
    public ClientInfo findOneByname(String username) {
        return null;
    }
}
