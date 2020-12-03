package com.lzb.tester.service;

import com.lzb.tester.common.*;
import com.lzb.tester.dto.EventsHelper;
import com.lzb.tester.entity.ClientInfo;
import com.lzb.tester.entity.HttpEntity;
import com.lzb.tester.entity.JdbcConnectInfo;
import com.lzb.tester.entity.StatementInfo;
import com.sun.org.apache.xerces.internal.impl.dv.dtd.ENTITYDatatypeValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;


@Service
public class WorkerService {

    @Autowired
    private MongoTemplate template;

    public void execute(ClientInfo clientInfo){
        Integer cid = clientInfo.getId();
        List<EventsHelper> events = clientInfo.getEvents();

        events.stream().sorted(Comparator.comparing(EventsHelper::getSort)).forEach(e->{
            switch (e.getEvent()){
                case Events.HTTP_REQUEST:
                    executeHttpRequest(cid,e);
                    break;
                case Events.JDBC_REQUEST:

            }
        });

    }

    private void executeHttpRequest(Integer cid,EventsHelper helper){
        String hid = helper.getHid();
        Query query = new Query(Criteria.where("cid").is(cid).and("_id").is(hid));
        HttpEntity entity = template.findOne(query, HttpEntity.class);
        String method = entity.getMethod();
        HttpMethodUtil.maps.get(method).accept(entity);
    }

    public void selectJdbcSource(Integer cid,String id){
        if (JdbcPool.getPool(cid) == null){
            Query query = new Query(Criteria.where("_id").is(id));
            JdbcConnectInfo info = template.findOne(query, JdbcConnectInfo.class);
            JdbcUtil jdbcUtil = new JdbcUtil();
            jdbcUtil.selectDataSource(info);
            JdbcPool.putPool(cid,jdbcUtil);
        }


    }

    private void executeJdbcRequest(Integer cid,EventsHelper helper,ClientInfo info){

    }

}
