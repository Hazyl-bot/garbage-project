package com.garbage.project.app.controller;

import com.alibaba.fastjson.JSON;
import com.garbage.project.model.GarbageBin;
import com.garbage.project.model.Record;
import com.garbage.project.model.User;
import com.garbage.project.param.GarbageQueryParam;
import com.garbage.project.param.RecordQueryParam;
import com.garbage.project.service.GarbageService;
import com.garbage.project.service.RecordService;
import com.garbage.project.service.UserService;
import com.garbage.project.util.GARBAGE_TYPE;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Controller
public class MainController {

    private static final Logger LOGGER = LoggerFactory.getLogger(MainController.class);

    private GarbageService garbageService;

    private RecordService recordService;

    private UserService userService;

    @Autowired
    public MainController(GarbageService garbageService, RecordService recordService, UserService userService) {
        this.garbageService = garbageService;
        this.recordService = recordService;
        this.userService = userService;
    }




    /**
     * 处理数据显示
     * 分别展示四个类型垃圾丢弃总数
     * 加入数据集，按月统计丢垃圾总数 折线图 gmtCreated
     * 按类型统计丢垃圾总数，制作环形图
     * */
    @RequestMapping("/")
    public String index(Model model, HttpServletRequest request,HttpServletResponse response){
        if (request.getSession()==null){
            model.addAttribute("username","User");
            return "index";
        }
        String userId = request.getSession().getAttribute("userId").toString();
        String userName = request.getSession().getAttribute("userName").toString();
        User user = userService.getUserById(userId);

        return "index";
    }


    /**
     * 用户选择
     * */
    @RequestMapping("/takeout")
    public String takeout(Model model){
        return "addRecord";
    }

    @PostMapping("/record/add")
    public String addRecord(@RequestParam String location,GARBAGE_TYPE type,Model model,
                            HttpServletRequest request,HttpServletResponse response){
        GarbageQueryParam gbParam = new GarbageQueryParam();
        gbParam.setLocation(location);
        //这里可能找到多个不同类型的垃圾箱
        List<GarbageBin> content = garbageService.list(gbParam).getContent();
        GarbageBin garbageBin = null;
        for (GarbageBin item : content){
            if (item.getType().getValue().equals(type.getValue())){
                garbageBin=item;
                break;
            }
        }
        if (garbageBin == null){
            model.addAttribute("msg","找不到所选垃圾箱或类型不匹配！");
            return "addRecord";
        }
        String userId = request.getSession().getAttribute("userId").toString();
        Record record = new Record();
        record.setOwnerId(userId);
        record.setGarbageBinId(garbageBin.getId());
        record.setGmtCreated(LocalDateTime.now());
        record.setGmtModified(LocalDateTime.now());
        Record add = recordService.add(record);
        if (add!=null){
            LOGGER.warn("Record "+add+" has been added");
        }else {
            LOGGER.warn("add failed");
        }
        return "addRecord";
    }

    @RequestMapping("/getMonthlyData")
    @ResponseBody
    public String getMonthlyData(HttpServletRequest request,Model model){
        String userId = request.getSession().getAttribute("userId").toString();
        RecordQueryParam param = new RecordQueryParam();
        param.setOwnerId(userId);
        param.setGmtCreated(LocalDateTime.now());
        //键为月份数，从1开始，值为记录数
        Map<Integer, Long> map = recordService.CountByMonthAndUser(param);
        return map.toString();
    }

    @RequestMapping("/getTypeData")
    @ResponseBody
    public String getTypeData(HttpServletRequest request,Model model){
        String userId = request.getSession().getAttribute("userId").toString();
        RecordQueryParam param = new RecordQueryParam();
        param.setOwnerId(userId);
        Map<String, Long> data = recordService.CountByTypeAndUser(param);
        String s = JSON.toJSONString(data);
        return s;
    }

}
