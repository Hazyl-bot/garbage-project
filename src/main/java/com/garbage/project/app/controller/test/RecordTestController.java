package com.garbage.project.app.controller.test;

import com.garbage.project.model.Record;
import com.garbage.project.param.RecordQueryParam;
import com.garbage.project.service.RecordService;
import com.garbage.project.util.GARBAGE_TYPE;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/recordtest")
public class RecordTestController {

    private static final Logger LOGGER = LoggerFactory.getLogger(RecordTestController.class);

    @Autowired
    private RecordService recordService;

    @GetMapping("/testadd")
    public String add(){
        Record record = new Record();
        record.setOwnerId("testforaid");
        record.setGarbageBinId("njupt-2");
        record.setType(GARBAGE_TYPE.OTHER);
        record.setGmtCreated(LocalDateTime.now());
        record.setGmtModified(LocalDateTime.now());
        Record add = recordService.add(record);
        return add.toString();
    }

    @GetMapping("/testdel")
    public String delete(){
        RecordQueryParam param = new RecordQueryParam();
        param.setOwnerId("testforaid");
        Page<Record> list = recordService.list(param);
        Record record = list.getContent().get(0);
        boolean b = recordService.deleteRecord(record.getId());
        if (b){
            return "successfully deleted";
        }else {
            return "fail to delete";
        }
    }

    @GetMapping("/testmodify")
    public String modify(){
        RecordQueryParam param = new RecordQueryParam();
        param.setOwnerId("testforaid");
        Record record = new Record();
        record.setType(GARBAGE_TYPE.HARMFUL);
        Page<Record> records = recordService.list(param);
        record.setId(records.getContent().get(0).getId());
        boolean b = recordService.modifyRecord(record);
        if (b){
            return "modify succeed";
        }else {
            return "modify failed";
        }

    }

    @GetMapping("/getall")
    public String getAll(){
        List<Record> records = recordService.getAll();
        if (records.isEmpty()){
            return null;
        }
        StringBuffer sb = new StringBuffer();
        for (Record item: records){
            sb.append(item.toString());
        }

        return sb.toString();
    }

    @GetMapping("/list")
    public String getList(){
        RecordQueryParam param = new RecordQueryParam();
        param.setGmtCreated(LocalDateTime.now());
        Page<Record> list = recordService.list(param);
        return list.getContent().toString();
    }
}
