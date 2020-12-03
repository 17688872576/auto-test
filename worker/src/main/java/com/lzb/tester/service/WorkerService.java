package com.lzb.tester.service;

import com.lzb.tester.common.Events;
import com.lzb.tester.common.HttpUtil;
import com.lzb.tester.dto.EventsHelper;
import com.lzb.tester.entity.ClientInfo;
import com.lzb.tester.entity.HttpEntity;
import com.sun.org.apache.xerces.internal.impl.dv.dtd.ENTITYDatatypeValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;


@Service
public class WorkerService {

    @Autowired
    private MongoTemplate template;

    public void execute(ClientInfo clientInfo){
        Integer cid = clientInfo.getId();
        List<EventsHelper> events = clientInfo.getEvents();

        events.stream().sorted((i,j) ->i.getSort().compareTo(j.getSort())).forEach(e->{
            switch (e.getEvent()){
                case Events.HTTP_REQUEST:
                    executeHttpRequest(cid,e);
                    break;
            }
        });

    }

    private void executeHttpRequest(Integer cid,EventsHelper helper){
        String hid = helper.getHid();
        Query query = new Query(Criteria.where("cid").is(cid).and("checked").is(true).and("_id").is(hid));
        HttpEntity entity = template.findOne(query, HttpEntity.class);
        String method = entity.getMethod();
    }


}
