package com.garbage.project.service.impl;

import com.garbage.project.model.Record;
import com.garbage.project.param.RecordQueryParam;
import com.garbage.project.service.RecordService;
import com.garbage.project.util.GARBAGE_TYPE;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.repository.support.PageableExecutionUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.LongSupplier;

@Service
public class RecordServiceImpl implements RecordService {

    private static final Logger LOG = LoggerFactory.getLogger(RecordServiceImpl.class);

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public Page<Record> list(RecordQueryParam param) {
        if (param == null){
            LOG.error("input record data is not correct");
            return null;
        }

        Criteria criteria = new Criteria();
        List<Criteria> subCris = new ArrayList<>();

        if (StringUtils.hasText(param.getId())) {
            subCris.add(Criteria.where("id").is(param.getId()));
        }
        if (StringUtils.hasText(param.getOwnerId())) {
            subCris.add(Criteria.where("ownerId").is(param.getOwnerId()));
        }
        if (StringUtils.hasText(param.getGarbageBinId())) {
            subCris.add(Criteria.where("garbageBinId").is(param.getGarbageBinId()));
        }
        if (param.getType()!=null) {
            subCris.add(Criteria.where("type").is(param.getType().toString()));
        }
        if (param.getGmtCreated()!=null){
            subCris.add(Criteria.where("gmtCreated").gte(param.getGmtCreated()));
        }
        if (subCris.isEmpty()){
            LOG.error("input record query param is not correct");
            return null;
        }
        criteria.andOperator(subCris.toArray(new Criteria[] {}));

        Query query = new Query(criteria);

        long count = mongoTemplate.count(query,Record.class);

        Pageable pageable = PageRequest.of(param.getPageNum() - 1, param.getPageSize());
        query.with(pageable);

        List<Record> list = mongoTemplate.find(query, Record.class);

        Page<Record> pageResult = PageableExecutionUtils.getPage(list, pageable, new LongSupplier() {
            @Override
            public long getAsLong() {
                return count;
            }
        });
        return pageResult;

    }

    @Override
    public long listCount(RecordQueryParam param) {
        if (param == null){
            LOG.error("input record data is not correct");
            return -1;
        }

        Criteria criteria = new Criteria();
        List<Criteria> subCris = new ArrayList<>();

        if (StringUtils.hasText(param.getId())) {
            subCris.add(Criteria.where("id").is(param.getId()));
        }
        if (StringUtils.hasText(param.getOwnerId())) {
            subCris.add(Criteria.where("ownerId").is(param.getOwnerId()));
        }
        if (StringUtils.hasText(param.getGarbageBinId())) {
            subCris.add(Criteria.where("garbageBinId").is(param.getGarbageBinId()));
        }
        if (param.getType()!=null) {
            subCris.add(Criteria.where("type").is(param.getType().toString()));
        }
        if (param.getGmtCreated()!=null){
            subCris.add(Criteria.where("gmtCreated").gte(param.getGmtCreated()));
        }
        if (subCris.isEmpty()){
            LOG.error("input record query param is not correct");
            return -1;
        }
        criteria.andOperator(subCris.toArray(new Criteria[] {}));

        Query query = new Query(criteria);

        long count = mongoTemplate.count(query,Record.class);
        return count;
    }

    @Override
    public Map<String, Long> CountByTypeAndUser(Record param) {
        if (param == null){
            LOG.error("input record data is not correct");
            return null;
        }
        if (!StringUtils.hasText(param.getOwnerId())) {
            LOG.error("user id not provided!");
            return null;
        }
        Map<String,Long> counts = new HashMap<>();
        for (GARBAGE_TYPE type: GARBAGE_TYPE.values()){
            Criteria criteria = Criteria.where("ownerId").is(param.getOwnerId())
                    .andOperator(Criteria.where("type").is(type.getValue()));
            Query query = new Query(criteria);
            long count = mongoTemplate.count(query, Record.class);
            counts.put(type.getValue(),count);
        }
        return counts;
    }

    @Override
    public Map<Integer, Long> CountByMonthAndUser(RecordQueryParam param) {
        if (param==null){
            LOG.error("input record data is not correct");
            return null;
        }
        if (param.getGmtCreated()==null){
            LOG.error("no gmtCreated Date data provided");
            return null;
        }
        if (!StringUtils.hasText(param.getOwnerId())) {
            LOG.error("user id not provided!");
            return null;
        }

        int year = param.getGmtCreated().getYear();
        LocalDateTime start = LocalDateTime
                .of(year, 1, 1, 0, 0);
        LocalDateTime end = start.plusMonths(1);

        Map<Integer,Long> counts = new HashMap<>();
        int m=0;
        do{
            Criteria criteria = Criteria.where("ownerId").is(param.getOwnerId())
                    .andOperator(
                    Criteria.where("gmtCreated").gte(start),
                    Criteria.where("gmtCreated").lt(end)
            );
            Query query = new Query(criteria);
            long count = mongoTemplate.count(query, Record.class);
            counts.put(m,count);
            m++;
            start=start.plusMonths(1);
            end = end.plusMonths(1);
            LOG.warn(start.toString());
        }while (start.getYear()==year);

        return counts;
    }

    @Override
    public Record add(Record record) {
        if (record == null){
            LOG.error("record data is null");
            return null;
        }
        record.setGmtCreated(LocalDateTime.now());
        record.setGmtModified(LocalDateTime.now());
        return mongoTemplate.insert(record);
    }

    @Override
    public boolean deleteRecord(String id) {
        if (!StringUtils.hasText(id)){
            LOG.error("record id is null");
            return false;
        }
        Record record = new Record();
        record.setId(id);
        DeleteResult result = mongoTemplate.remove(record);
        return result != null && result.getDeletedCount() > 0;

    }

    @Override
    public boolean modifyRecord(Record record) {
        if (record == null || !StringUtils.hasText(record.getId())) {
            LOG.error("input User data is not correct.");
            return false;
        }
        // 主键不能修改，作为查询条件
        Query query = new Query(Criteria.where("id").is(record.getId()));

        Update updateData = new Update();
        // 值为 null 表示不修改。值为长度为 0 的字符串 "" 表示清空此字段
        if (record.getType() != null) {
            updateData.set("type", record.getType());
        }
        if (StringUtils.hasText(record.getOwnerId())){
            updateData.set("ownerId",record.getOwnerId());
        }
        if (StringUtils.hasText(record.getGarbageBinId())){
            updateData.set("garbageBinId",record.getGarbageBinId());
        }
        updateData.set("gmtModified", LocalDateTime.now());
        UpdateResult result = mongoTemplate.updateFirst(query, updateData, Record.class);
        return result!=null && result.getModifiedCount()>0;
    }

    @Override
    public List<Record> getAll() {
        return mongoTemplate.findAll(Record.class);
    }
}
