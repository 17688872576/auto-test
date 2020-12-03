package com.lzb.tester.service.impl;

import com.lzb.tester.common.MongoUtils;
import com.lzb.tester.dto.MongoPageInfo;
import com.lzb.tester.entity.JdbcConnectInfo;
import com.lzb.tester.service.JdbcConnectInfoService;
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
public class JdbcConnectInfoServiceImpl implements JdbcConnectInfoService<JdbcConnectInfo> {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public JdbcConnectInfo save(JdbcConnectInfo entity) {
        return mongoTemplate.insert(entity);
    }

    @Override
    public Long remove(List<String> idList) {
        AtomicLong count = new AtomicLong();
        idList.forEach(id->{
            Long remove = remove(id);
            count.addAndGet(remove);
        });
        return Optional.ofNullable(count.get()).orElse(-1L);
    }

    @Override
    public Long remove(String id) {
        DeleteResult remove = mongoTemplate.remove(new Query(Criteria.where("_id").is(id)));
        return Optional.ofNullable(remove.getDeletedCount()).orElse(-1L);
    }

    @Override
    public JdbcConnectInfo findOneById(String id) {
        return mongoTemplate.findOne(new Query(Criteria.where("_id").is(id)), JdbcConnectInfo.class);
    }

    @Override
    public MongoPageInfo<JdbcConnectInfo> findAllBy(Integer index, Integer size, String key, Object value) {
        if (index < 1) throw new RuntimeException("分页异常");
        Query query = new Query(Criteria.where(key).is(value));
        long count = mongoTemplate.count(query, JdbcConnectInfo.class);
        PageRequest pageRequest = PageRequest.of(index-1, size);
        query.with(pageRequest).with(Sort.by(Sort.Direction.ASC,"sort"));
        List<JdbcConnectInfo> entities = mongoTemplate.find(query, JdbcConnectInfo.class);
        Page<JdbcConnectInfo> page = PageableExecutionUtils.getPage(entities, pageRequest, () -> count);
        return MongoUtils.pageInfoCopy(page);
    }

    @Override
    public MongoPageInfo<JdbcConnectInfo> selectAll(Integer index, Integer size) {
        if (index < 1) throw new RuntimeException("分页异常");
        Query query = new Query();
        long count = mongoTemplate.count(query, JdbcConnectInfo.class);
        PageRequest pageRequest = PageRequest.of(index-1, size);
        query.with(pageRequest).with(Sort.by(Sort.Direction.ASC,"sort"));
        List<JdbcConnectInfo> entities = mongoTemplate.find(query, JdbcConnectInfo.class);
        Page<JdbcConnectInfo> page = PageableExecutionUtils.getPage(entities, pageRequest, () -> count);
        return MongoUtils.pageInfoCopy(page);
    }

    @Override
    public Long update(JdbcConnectInfo entity) {
        Update update = new Update();
        MongoUtils.filedsCopy(entity,update);
        UpdateResult result = mongoTemplate.upsert(new Query(Criteria.where("_id").is(entity.getId())), update, JdbcConnectInfo.class);
        return Optional.ofNullable(result.getModifiedCount()).orElse(-1L);
    }
}
