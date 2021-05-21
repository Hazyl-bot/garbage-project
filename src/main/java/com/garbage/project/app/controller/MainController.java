package com.garbage.project.app.controller;

import com.garbage.project.model.GarbageBin;
import com.garbage.project.model.Record;
import com.garbage.project.param.GarbageQueryParam;
import com.garbage.project.param.RecordInfo;
import com.garbage.project.param.RecordQueryParam;
import com.garbage.project.param.UserLoginInfo;
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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

@Controller
public class MainController {

    //TODO:设置新密码页面和后端方法（UserController),丢垃圾完成后回到个人资料页
    // BUG：丢垃圾表单提交无效

    private static final Logger LOGGER = LoggerFactory.getLogger(MainController.class);

    private GarbageService garbageService;

    private RecordService recordService;

    private UserService userService;

    final static String[] monthName = {"一月", "二月", "三月", "四月", "五月", "六月", "七月", "八月", "九月", "十月", "十一月", "十二月"};

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
        UserLoginInfo userLoginInfo = (UserLoginInfo) request.getSession().getAttribute("userLoginInfo");
        if (userLoginInfo==null){
            return "redirect:/user/login";
        }
        String userId = userLoginInfo.getUserId();
        String userName = userLoginInfo.getUserName();
        model.addAttribute("username", userName);

        Map<Integer, Long> monthlyMap = getMonthlyData(userId);
        List<String> monthlyLabel = new ArrayList<>();
        List<Long> monthlyData = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        int month = calendar.get(Calendar.MONTH);
        for (int i = 0; i < 12; i++){
            monthlyLabel.add(monthName[(month + i + 1)%12]);
            monthlyData.add(monthlyMap.get((month + i + 1)%12));
        }
        model.addAttribute("monthlyLabel", monthlyLabel);
        model.addAttribute("monthlyData", monthlyData);

        Map<String, Long> typeMap = getTypeData(userId);
        List<Long> typeData = new ArrayList<>();
        for (GARBAGE_TYPE type: GARBAGE_TYPE.values()){
            typeData.add(typeMap.get(type.getValue()));
            model.addAttribute(type.getValue(), typeMap.get(type.getValue()));
        }
        model.addAttribute("typeData", typeData);

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
    * 用户历史记录数据
    * */
    @RequestMapping("/records")
    public String list(HttpServletRequest request,Model model){
        UserLoginInfo info = (UserLoginInfo) request.getSession().getAttribute("userLoginInfo");
        String userId = info.getUserId();
        List<Record> records = getRecordByUser(userId).getContent();
        List<RecordInfo> infoList = new ArrayList<>();
        for (Record r:records){
            GarbageBin bin = garbageService.getBinById(r.getGarbageBinId());
            String location = bin.getLocation();
            RecordInfo recordInfo = new RecordInfo(location,userId,r.getType(),r.getGmtCreated());
            infoList.add(recordInfo);
        }
        model.addAttribute("records",infoList);
        return "records";
    }

    /**
     * 丢垃圾：点击按钮转到addrecord界面，两个下拉列表一个类型，一个是location
     * 根据这两个参数查询垃圾箱，找不到则返回，找到则检查类型和容量，不符合要求则回到表格，符合则添加成功
     * 转到历史列表
     * */
    @RequestMapping("/record/add")
    public String addRecord(@RequestParam String location,@RequestParam String type,Model model,
                            HttpServletRequest request,HttpServletResponse response){
        GarbageQueryParam gbParam = new GarbageQueryParam();
        gbParam.setLocation(location);
        gbParam.setType(GARBAGE_TYPE.valueOf(type));
        //这里可能找到多个不同类型的垃圾箱
        List<GarbageBin> content = garbageService.list(gbParam).getContent();

        if (content.isEmpty()){
            model.addAttribute("msg","找不到所选垃圾箱或类型不匹配！");
            return "redirect:/takeout";
        }
        GarbageBin garbageBin = content.get(0);
        if (garbageBin.getContain()>=garbageBin.getCapacity()){
            model.addAttribute("msg","垃圾箱已满，请选择其他可用垃圾箱！");
            return "redirect:/takeout";
        }
        UserLoginInfo info = (UserLoginInfo) request.getSession().getAttribute("userLoginInfo");
        String userId = info.getUserId();
        Record record = new Record();
        record.setOwnerId(userId);
        record.setGarbageBinId(garbageBin.getId());
        record.setGmtCreated(LocalDateTime.now());
        record.setGmtModified(LocalDateTime.now());
        Record add = recordService.add(record);
        if (add!=null){
            LOGGER.warn("Record "+add+" has been added");
            Integer contain = garbageBin.getContain();
            garbageBin.setContain(contain+1);
            garbageService.modifyBin(garbageBin);
        }else {
            model.addAttribute("msg","垃圾分类异常，请联系管理员");
            LOGGER.warn("add failed");
        }
        //回到历史记录页面
        return "records";
    }

    private Map<Integer, Long> getMonthlyData(String userId){
        RecordQueryParam param = new RecordQueryParam();
        param.setOwnerId(userId);
        param.setGmtCreated(LocalDateTime.now());
        //键为月份数，从0开始，值为记录数
        return recordService.CountByMonthAndUser(param);
    }

    private Map<String, Long> getTypeData(String userId){
        RecordQueryParam param = new RecordQueryParam();
        param.setOwnerId(userId);
        return recordService.CountByTypeAndUser(param);
    }

    private Page<Record> getRecordByUser(String userId){
        RecordQueryParam param = new RecordQueryParam();
        param.setOwnerId(userId);
        return recordService.list(param);
    }

}
