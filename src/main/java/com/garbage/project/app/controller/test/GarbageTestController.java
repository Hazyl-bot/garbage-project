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

import javax.annotation.PostConstruct;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@RestController
@RequestMapping("/test/gb")
public class GarbageTestController {

    private static final Logger LOG = LoggerFactory.getLogger(GarbageTestController.class);

    @Autowired
    private GarbageService garbageService;

    private List<GarbageBin> init = new ArrayList<>();

    @GetMapping("/testadd")
    public String add(){
        for (int i=0;i<12;i++){
            GarbageBin garbageBin = new GarbageBin();
            garbageBin.setCapacity(100*i);
            garbageBin.setContain(0);
            SecureRandom random = new SecureRandom();
            int r = random.nextInt(6)+1;
            switch (r){
                case 1:
                    garbageBin.setType(GARBAGE_TYPE.RECYCLABLE);
                    break;
                case 2:
                    garbageBin.setType(GARBAGE_TYPE.HARMFUL);
                    break;
                case 3:
                    garbageBin.setType(GARBAGE_TYPE.OTHER);
                    break;
                case 4:
                    garbageBin.setType(GARBAGE_TYPE.WASTE);
                    break;
                case 5:
                    garbageBin.setType(GARBAGE_TYPE.WET);
                    break;
                case 6:
                    garbageBin.setType(GARBAGE_TYPE.DRY);
                    break;
                default:
                    garbageBin.setType(GARBAGE_TYPE.OTHER);
                    break;
            }
            garbageBin.setLocation("njupt-"+i);
            init.add(garbageBin);
        }
        for (GarbageBin item : init){
            GarbageBin add = garbageService.add(item);
            LOG.warn(add.toString());
        }
        return init.toString();
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
        StringBuilder sb = new StringBuilder();

        List<GarbageBin> list = garbageService.getAll();
        for (GarbageBin item:list){
            String temp = item.toString();
            sb.append(temp);
        }
        return sb.toString();
    }



}
