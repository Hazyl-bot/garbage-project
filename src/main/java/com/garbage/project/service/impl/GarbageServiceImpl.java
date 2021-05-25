package com.garbage.project.service.impl;

import com.garbage.project.model.GarbageBin;
import com.garbage.project.param.GarbageQueryParam;
import com.garbage.project.service.GarbageService;
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
import java.util.List;
import java.util.function.LongSupplier;

@Service
public class GarbageServiceImpl implements GarbageService {

    Logger logger = LoggerFactory.getLogger(GarbageServiceImpl.class);

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public List<GarbageBin> getAll() {
        return mongoTemplate.findAll(GarbageBin.class);
    }

    @Override
    public Page<GarbageBin> list(GarbageQueryParam param) {
        // 作为服务，要对入参进行判断，不能假设被调用时，入参一定正确
        if (param == null) {
            logger.error("input User data is not correct.");
            return null;
        }

        // 总条件
        Criteria criteria = new Criteria();
        // 可能有多个子条件
        List<Criteria> subCris = new ArrayList();
        if (StringUtils.hasText(param.getId())) {
            subCris.add(Criteria.where("id").is(param.getId()));
        }

        if (StringUtils.hasText(param.getLocation())) {
            subCris.add(Criteria.where("location").is(param.getLocation()));
        }
        if (param.getCapacity()!=null) {
            subCris.add(Criteria.where("capacity").is(param.getCapacity()));
        }
        if (param.getContain()!=null) {
            subCris.add(Criteria.where("contain").is(param.getContain()));
        }
        if (param.getType()!=null) {
            subCris.add(Criteria.where("type").is(param.getType().getValue()));
        }
        if (param.getLastClean()!=null) {
            subCris.add(Criteria.where("lastClean").gte(param.getLastClean()));
        }


        // 必须至少有一个查询条件
        if (subCris.isEmpty()) {
            logger.error("input GarbageBin query param is not correct.");
            return null;
        }

        // 多个子条件以 and 关键词连接成总条件对象，相当于 id='' and location=''
        criteria.andOperator(subCris.toArray(new Criteria[] {}));

        // 条件对象构建查询对象
        Query query = new Query(criteria);
        // 总数
        long count = mongoTemplate.count(query, GarbageBin.class);
        // 构建分页对象。注意此对象页码号是从 0 开始计数的。
        Pageable pageable = PageRequest.of(param.getPageNum() - 1, param.getPageSize());
        query.with(pageable);

        // 查询结果
        List<GarbageBin> garbageBins = mongoTemplate.find(query, GarbageBin.class);
        // 构建分页器
        Page<GarbageBin> pageResult = PageableExecutionUtils.getPage(garbageBins, pageable, new LongSupplier() {
            @Override
            public long getAsLong() {
                return count;
            }
        });

        return pageResult;
    }

    @Override
    public long listCount(GarbageQueryParam param) {
        // 作为服务，要对入参进行判断，不能假设被调用时，入参一定正确
        if (param == null) {
            logger.error("input User data is not correct.");
            return -1;
        }

        // 总条件
        Criteria criteria = new Criteria();
        // 可能有多个子条件
        List<Criteria> subCris = new ArrayList();
        if (StringUtils.hasText(param.getId())) {
            subCris.add(Criteria.where("id").is(param.getId()));
        }

        if (StringUtils.hasText(param.getLocation())) {
            subCris.add(Criteria.where("location").is(param.getLocation()));
        }
        if (param.getCapacity()!=null) {
            subCris.add(Criteria.where("capacity").is(param.getCapacity()));
        }
        if (param.getContain()!=null) {
            subCris.add(Criteria.where("contain").is(param.getContain()));
        }
        if (StringUtils.hasText(param.getType().toString())) {
            subCris.add(Criteria.where("type").is(param.getType().toString()));
        }
        if (param.getLastClean()!=null) {
            subCris.add(Criteria.where("lastClean").gte(param.getLastClean()));
        }


        // 必须至少有一个查询条件
        if (subCris.isEmpty()) {
            logger.error("input GarbageBin query param is not correct.");
            return -1;
        }

        // 多个子条件以 and 关键词连接成总条件对象，相当于 id='' and location=''
        criteria.andOperator(subCris.toArray(new Criteria[] {}));

        // 条件对象构建查询对象
        Query query = new Query(criteria);
        long count = mongoTemplate.count(query, GarbageBin.class);
        return count;
    }

    @Override
    public GarbageBin add(GarbageBin garbageBin) {
        if (garbageBin == null){
            logger.error("garbage bin data is null");
            return null;
        }
        if (garbageBin.getGmtCreated()==null){
            garbageBin.setGmtCreated(LocalDateTime.now());
        }
        if (garbageBin.getGmtModified()==null){
            garbageBin.setGmtModified(LocalDateTime.now());
        }
        return mongoTemplate.insert(garbageBin);
    }

    @Override
    public boolean modifyBin(GarbageBin garbageBin) {
        if (garbageBin == null || !StringUtils.hasText(garbageBin.getId())){
            logger.error("input garbageBin data is not correct");
            return false;
        }
        Query query = new Query(Criteria.where("id").is(garbageBin.getId()));
        Update updateData = new Update();
        if (garbageBin.getLocation()!=null){
            updateData.set("location", garbageBin.getLocation());
        }
        if (garbageBin.getType()!=null){
            updateData.set("type", garbageBin.getType().toString());
        }
        if (garbageBin.getCapacity()!=null){
            updateData.set("capacity",garbageBin.getCapacity());
        }
        if (garbageBin.getContain()!=null){
            updateData.set("contain", garbageBin.getContain());
            if (garbageBin.getContain()==0){
                updateData.set("lastClean",LocalDateTime.now());
            }
        }
        updateData.set("gmtModified", LocalDateTime.now());
        UpdateResult result = mongoTemplate.updateFirst(query, updateData, GarbageBin.class);
        return result!=null && result.getModifiedCount()>0;
    }

    @Override
    public boolean deleteBin(String id) {
        if (!StringUtils.hasText(id)){
            logger.error("input garbage bin id is blank");
            return false;
        }
        GarbageBin garbageBin = new GarbageBin();
        garbageBin.setId(id);
        DeleteResult result = mongoTemplate.remove(garbageBin);
        return result!=null && result.getDeletedCount()>0;
    }

    @Override
    public GarbageBin getBinById(String id) {
        if (!StringUtils.hasText(id)){
            logger.error("id data is null");
            return null;
        }
        return mongoTemplate.findById(id, GarbageBin.class);
    }

    @Override
    public int cleanGarbages(List<GarbageBin> garbageBins) {
        int count=0;
        for (GarbageBin item:garbageBins){
            if (item == null || !StringUtils.hasText(item.getId())){
                logger.error("input garbageBin data is not correct");
                return 0;
            }
            // 主键不能修改，作为查询条件
            Query query = new Query(Criteria.where("id").is(item.getId()));

            Update updateData = new Update();
            // 值为 null 表示不修改。值为长度为 0 的字符串 "" 表示清空此字段
            updateData.set("contain",0);
            updateData.set("lastClean", LocalDateTime.now());
            updateData.set("lastModified", LocalDateTime.now());
            UpdateResult updateResult = mongoTemplate.updateFirst(query, updateData, GarbageBin.class);
            if (updateResult==null){
                logger.error("garbage clean error,please review");
            }
            count+=updateResult.getModifiedCount();
        }
        return count;
    }
}
