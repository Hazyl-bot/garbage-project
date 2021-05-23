package com.garbage.project.app.controller;

import com.garbage.project.model.GarbageBin;
import com.garbage.project.model.Record;
import com.garbage.project.param.RecordQueryParam;
import com.garbage.project.param.UserLoginInfo;
import com.garbage.project.service.GarbageService;
import com.garbage.project.service.RecordService;
import com.garbage.project.util.GARBAGE_TYPE;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/garbage")
public class GarbageController {

    private static final Logger LOG = LoggerFactory.getLogger(GarbageController.class);


    private GarbageService garbageService;
    private RecordService recordService;

    @Autowired
    public GarbageController(GarbageService garbageService, RecordService recordService) {
        this.garbageService = garbageService;
        this.recordService = recordService;
    }

    @RequestMapping("/add")
    public String add(@RequestParam("location")String location, @RequestParam("type")String type
            , @RequestParam("capacity")int capacity, @RequestParam("contain")int contain){
        GarbageBin garbageBin = new GarbageBin();
        garbageBin.setLocation(location);
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

    @RequestMapping("/update")
    public String update(@RequestParam String id,@RequestParam("location")String location, @RequestParam("type")String type
            , @RequestParam("capacity")int capacity, @RequestParam("contain")int contain){
        GarbageBin param = new GarbageBin();
        param.setId(id);
        param.setLocation(location);
        param.setType(GARBAGE_TYPE.valueOf(type));
        param.setCapacity(capacity);
        param.setContain(contain);
        LOG.warn(param.toString());
        param.setGmtModified(LocalDateTime.now());
        garbageService.modifyBin(param);
        LOG.warn(param+ " has been updated");
        return "redirect:/garbage/gbs";
    }

    @GetMapping("/edit")
    public String edit(@RequestParam String id,@RequestParam("location")String location, @RequestParam("type")String type
            , @RequestParam("capacity")int capacity, @RequestParam("contain")int contain, Model model){
        model.addAttribute("id", id);
        model.addAttribute("defaultLocation", location);
        model.addAttribute("defaultType", type);
        model.addAttribute("defaultCapacity", capacity);
        model.addAttribute("defaultContain", contain);
        return "editGarbageBin";
    }

    @ResponseBody
    @RequestMapping("/remove")
    public String remove(@RequestParam String id){
        boolean b = garbageService.deleteBin(id);
        RecordQueryParam param = new RecordQueryParam();
        param.setGarbageBinId(id);
        List<Record> content = recordService.list(param).getContent();
        for (Record r:content){
            boolean delResult = recordService.deleteRecord(r.getId());
            if (!delResult){
                return "500";
            }
        }
        if (b){
            return "200";
        }else {
            LOG.error("delete failed");
            return "500";
        }
    }

    @RequestMapping("/gbs")
    public String list(HttpServletRequest request, Model model){
        UserLoginInfo userLoginInfo = (UserLoginInfo) request.getSession().getAttribute("userLoginInfo");
        if (userLoginInfo==null){
            return "redirect:/user/login";
        }
        String userId = userLoginInfo.getUserId();
        String userName = userLoginInfo.getUserName();
        model.addAttribute("username", userName);

        List<GarbageBin> all = garbageService.getAll();
        model.addAttribute("gbs",all);
        return "garbageBin";
    }

    @GetMapping("/gb")
    public String toAddPage(Model model){
        return "addGarbageBin";
    }

}
