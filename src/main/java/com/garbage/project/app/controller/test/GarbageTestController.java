package com.garbage.project.app.controller.test;

import com.garbage.project.model.GarbageBin;
import com.garbage.project.param.GarbageQueryParam;
import com.garbage.project.service.GarbageService;
import com.garbage.project.util.GARBAGE_TYPE;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/gbtest")
public class GarbageTestController {

    private static final Logger LOG = LoggerFactory.getLogger(GarbageTestController.class);

    @Autowired
    private GarbageService garbageService;

//    @GetMapping("/getAll")
//    public Page<GarbageBin> getAll(){
//        GarbageQueryParam param = new GarbageQueryParam();
//    }

    @GetMapping("/testadd")
    public String add(){
        GarbageBin garbageBin = new GarbageBin();
        garbageBin.setId("thisisaId");
        garbageBin.setContain(100);
        garbageBin.setCapacity(500);
        garbageBin.setType(GARBAGE_TYPE.OTHER);
        garbageBin.setLocation("njupt-1");
        LOG.warn(garbageBin.toString());
        GarbageBin bin = garbageService.add(garbageBin);
        return garbageBin.toString();
    }

    @GetMapping("/testdel")
    public String delete(){
        GarbageQueryParam gbq = new GarbageQueryParam();
        gbq.setCapacity(500);
        gbq.setId("thisisaId");
        Page<GarbageBin> list = garbageService.list(gbq);
        boolean b = garbageService.deleteBin(list.getContent().get(0).getId());
        if (b){
            return "success deleted";
        }else {
            return "delete failed";
        }
    }

    @GetMapping("/testmodify")
    public String modify(){
        GarbageBin garbageBin = new GarbageBin();
        garbageBin.setCapacity(400);
        garbageBin.setType(GARBAGE_TYPE.OTHER);
        garbageBin.setLocation("njupt-2");
        boolean b = garbageService.modifyBin(garbageBin);
        if (b){
            return "modify succeed";
        }else {
            return "modify failed";
        }

    }

    @GetMapping("/testgetall")
    public String getAll(){
        StringBuffer sb = new StringBuffer();

        List<GarbageBin> list = garbageService.getAll();
        for (GarbageBin item:list){
            String temp = item.toString();
            sb.append(temp);
        }
        return sb.toString();
    }



}
