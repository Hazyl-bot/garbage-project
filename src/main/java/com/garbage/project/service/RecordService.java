package com.garbage.project.service;

import com.garbage.project.model.Record;
import com.garbage.project.param.RecordQueryParam;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;

public interface RecordService {
    /**
     * 按照用户ID或垃圾箱ID分页查询记录
     * */
    Page<Record> list(RecordQueryParam param);

    long listCount(RecordQueryParam param);

    Map<String,Long> CountByTypeAndUser(Record param);

    Map<Integer,Long> CountByMonthAndUser(RecordQueryParam param);

    Record add(Record record);

    boolean deleteRecord(String id);

    boolean modifyRecord(Record record);

    List<Record> getAll();



}
