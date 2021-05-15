package com.garbage.project.app.controller.test;

import com.alibaba.fastjson.JSON;
import com.garbage.project.model.GarbageBin;
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

import javax.annotation.PostConstruct;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/recordtest")
public class RecordTestController {

    private static final Logger LOGGER = LoggerFactory.getLogger(RecordTestController.class);

    @Autowired
    private RecordService recordService;

    private List<Record> init = new ArrayList<>();

    @PostConstruct
    private void initTest(){
        SecureRandom random = new SecureRandom();
        for (int i=0;i<10;i++){
            int r = random.nextInt(6)+1;
            Record record = new Record();
            //随便找一个账号ID
            record.setOwnerId("6095d0e0552f033a607f5860");
            record.setGarbageBinId("609c7acd47418c024984d577");
            switch (r){
                case 1:
                    record.setType(GARBAGE_TYPE.RECYCLABLE);
                    break;
                case 2:
                    record.setType(GARBAGE_TYPE.HARMFUL);
                    break;
                case 3:
                    record.setType(GARBAGE_TYPE.OTHER);
                    break;
                case 4:
                    record.setType(GARBAGE_TYPE.WASTE);
                    break;
                case 5:
                    record.setType(GARBAGE_TYPE.WET);
                    break;
                case 6:
                    record.setType(GARBAGE_TYPE.DRY);
                    break;
                default:
                    record.setType(GARBAGE_TYPE.OTHER);
                    break;
            }
            //这里minus起作用了，但是数据库里还是now，可能事库内置识别的问题
            LocalDateTime created = LocalDateTime.now().minusMonths(i);
            record.setGmtCreated(created);
            record.setGmtModified(LocalDateTime.now());
            init.add(record);
        }
    }

    @GetMapping("/testadd")
    public String add(){
//        Record record = new Record();
//        //随便找一个账号ID
//        record.setOwnerId("6095d0e0552f033a607f5860");
//        record.setGarbageBinId("njupt-2");
//        record.setType(GARBAGE_TYPE.OTHER);
//        record.setGmtCreated(LocalDateTime.now());
//        record.setGmtModified(LocalDateTime.now());
//        Record add = recordService.add(record);
        //暂时不考虑垃圾箱和记录类型的对应关系
        for (Record item:
             init) {
            Record add = recordService.add(item);
            LOGGER.warn(add.toString());
        }
        return init.toString();
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

    @RequestMapping("/getData")
    public String getData(){
        String userId = "6095d0e0552f033a607f5860";
        RecordQueryParam param = new RecordQueryParam();
        param.setOwnerId(userId);
        Map<String, Long> data = recordService.CountByTypeAndUser(param);
        String s = JSON.toJSONString(data);
        return s;
    }

    @RequestMapping("/getData2")
    public String getData2(){
        String userId = "6095d0e0552f033a607f5860";
        RecordQueryParam param = new RecordQueryParam();
        param.setOwnerId(userId);
        param.setGmtCreated(LocalDateTime.now());
        //键为月份数，从1开始，值为记录数
        Map<Integer, Long> map = recordService.CountByMonthAndUser(param);
        return map.toString();
    }
}
