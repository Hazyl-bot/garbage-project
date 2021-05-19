package com.garbage.project.app.controller;

import com.alibaba.fastjson.JSON;
import com.garbage.project.model.GarbageBin;
import com.garbage.project.model.Record;
import com.garbage.project.model.User;
import com.garbage.project.param.GarbageQueryParam;
import com.garbage.project.param.RecordQueryParam;
import com.garbage.project.param.UserLoginInfo;
import com.garbage.project.param.UserQueryParam;
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
import java.util.ArrayList;
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
     * 给出垃圾箱列表，所有可用类型列表
     * */
    @RequestMapping("/takeout")
    public String takeout(Model model){
        List<GarbageBin> garbageBins = garbageService.getAll();
        List<String> locations = new ArrayList<>();
        List<String> gbIds = new ArrayList<>();
        List<String> types = new ArrayList<>();
        for (GarbageBin item: garbageBins){
            locations.add(item.getLocation());
            gbIds.add(item.getId());
            if (!types.contains(item.getType().getName())){
                types.add(item.getType().getName());
            }
        }
        model.addAttribute("locations",locations);
        model.addAttribute("gbIds",gbIds);
        model.addAttribute("types",types);
        return "addRecord";
    }

    /**
     * 丢垃圾：点击按钮转到addrecord界面，两个下拉列表一个类型，一个是location
     * 根据这两个参数查询垃圾箱，找不到则返回，找到则检查类型和容量，不符合要求则回到表格，符合则添加成功
     * 转到历史列表
     * */
    @PostMapping("/record/add")
    public String addRecord(@RequestParam String location,GARBAGE_TYPE type,Model model,
                            HttpServletRequest request,HttpServletResponse response){
        GarbageQueryParam gbParam = new GarbageQueryParam();
        gbParam.setLocation(location);
        gbParam.setType(type);
        //这里可能找到多个不同类型的垃圾箱
        List<GarbageBin> content = garbageService.list(gbParam).getContent();

        if (content == null || content.isEmpty()){
            model.addAttribute("msg","找不到所选垃圾箱或类型不匹配！");
            return "redirect:/takeout";
        }
        GarbageBin garbageBin = content.get(0);
        if (garbageBin.getContain()>=garbageBin.getCapacity()){
            model.addAttribute("msg","垃圾箱已满，请选择其他可用垃圾箱！");
            return "redirect:/takeout";
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
        return JSON.toJSONString(data);
    }

}
