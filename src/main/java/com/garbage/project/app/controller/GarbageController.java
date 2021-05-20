package com.garbage.project.app.controller;

import com.garbage.project.model.GarbageBin;
import com.garbage.project.service.GarbageService;
import com.garbage.project.util.GARBAGE_TYPE;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/garbage")
public class GarbageController {

    private static final Logger LOG = LoggerFactory.getLogger(GarbageController.class);

    @Autowired
    private GarbageService garbageService;

//    @GetMapping("/getAll")
//    public Page<GarbageBin> getAll(){
//        GarbageQueryParam param = new GarbageQueryParam();
//    }

    @RequestMapping("/add")
    public String add(@RequestParam("location")String location, @RequestParam("type")String type
            , @RequestParam("capacity")int capacity, @RequestParam("contain")int contain){
        GarbageBin garbageBin = new GarbageBin();
        garbageBin.setLocation(location);//
        garbageBin.setType(GARBAGE_TYPE.valueOf(type));
        garbageBin.setCapacity(capacity);
        garbageBin.setContain(contain);
        LOG.warn(garbageBin.toString());
        garbageBin.setGmtCreated(LocalDateTime.now());
        garbageBin.setGmtModified(LocalDateTime.now());
        GarbageBin bin = garbageService.add(garbageBin);
        LOG.warn(bin.toString()+ " has been added");
        return "redirect:/garbage/gbs";
    }

    @RequestMapping("/gbs")
    public String list(Model model){
        List<GarbageBin> all = garbageService.getAll();
        model.addAttribute("gbs",all);
        return "tables";
    }

    @GetMapping("/gb")
    public String toAddPage(Model model){
        return "addGarbageBin";
    }

}
