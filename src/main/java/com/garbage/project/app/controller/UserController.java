package com.garbage.project.app.controller;

import com.garbage.project.model.GarbageBin;
import com.garbage.project.model.Record;
import com.garbage.project.model.User;
import com.garbage.project.param.RecordInfo;
import com.garbage.project.param.RecordQueryParam;
import com.garbage.project.param.UserLoginInfo;
import com.garbage.project.param.UserQueryParam;
import com.garbage.project.service.GarbageService;
import com.garbage.project.service.RecordService;
import com.garbage.project.service.UserService;
import com.garbage.project.util.MailUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

/**
 * 所有的预加载属性都在controller里写好
 * ID不需要预加载
 * */
@Controller
@RequestMapping("/user")
public class UserController {

    private static final Logger LOG = LoggerFactory.getLogger(UserController.class);


    private UserService userService;

    private RecordService recordService;

    private final GarbageService garbageService;

    @Autowired
    public UserController(UserService userService, RecordService recordService, GarbageService garbageService) {
        this.userService = userService;
        this.recordService = recordService;
        this.garbageService = garbageService;
    }

    @PostConstruct
    public void init() {
        LOG.info("UserControl 启动啦");
        LOG.info("userService 注入啦");
    }

    @RequestMapping(path = "/login")
    public String loginPage(Model model) {
        return "login";
    }

    @RequestMapping(path = "/logout")
    public String logout(HttpServletRequest request) {
        request.getSession().invalidate();
        return "redirect:/user/login";
    }

    @RequestMapping(path = "/authenticate")
    public String login(@RequestParam String name, @RequestParam String password, HttpServletRequest request,
                     HttpServletResponse response,Model model) {
        // 根据登录名查询用户
        User regedUser = getUserByLoginName(name);

        // 找不到此登录用户
        if (regedUser == null) {
            model.addAttribute("result", false);
            model.addAttribute("msg", "userName not correct");
            model.addAttribute("url","login");
            return "login";
        }

        if (regedUser.getPassword().equals(password)) {
            UserLoginInfo userLoginInfo = new UserLoginInfo();
            userLoginInfo.setUserId(regedUser.getId());
            userLoginInfo.setUserName(name);
            // 取得 HttpSession 对象
            HttpSession session = request.getSession();
            // 写入登录信息
            session.setAttribute("userLoginInfo", userLoginInfo);
            session.setAttribute("isAdmin",regedUser.isAdmin());
            model.addAttribute("result", true);
            model.addAttribute("msg", "login successful");
            return "redirect:/";
        } else {
            model.addAttribute("msg", "userName or password not correct");
            model.addAttribute("result", false);
            return "login";
        }
    }

    private User getUserByLoginName(String loginName) {
        User regedUser = null;
        UserQueryParam param = new UserQueryParam();
        param.setName(loginName);
        Page<User> users = userService.list(param);

        // 如果登录名正确，只取第一个，要保证用户名不能重复
        if (users != null && users.getContent().size() > 0) {
            regedUser = users.getContent().get(0);
        }

        return regedUser;
    }

    @RequestMapping("/register")
    public String sign(){
        return "register";
    }

    @RequestMapping("/registerAction")
    public String registerAction(@RequestParam String name, @RequestParam String email
            ,@RequestParam String password,@RequestParam String password2,Model model) {

        // 判断登录名是否已存在
        User regedUser = getUserByLoginName(name);
        if (regedUser != null) {
            model.addAttribute("msg","用户名已被占用");
            LOG.error("login name already exist");
            return "register";
        }
        if (!password.equals(password2)){
            model.addAttribute("msg","两次密码输入不相同！");
            LOG.error("passwords are not the same!");
            return "register";
        }
        User user = new User();
        user.setName(name);
        user.setPassword(password);
        user.setEmail(email);
        User newUser = userService.add(user);
        if (newUser != null && StringUtils.hasText(newUser.getId())) {
            model.addAttribute("msg", "注册成功，请登录");
            LOG.error("register succeed");
        } else {
            model.addAttribute("msg", "注册失败，请联系工作人员");
            LOG.error("register failed");
        }
        return "login";
    }

    /**
     * 用户信息页
     * */
    @RequestMapping("/profile")
    public String showProfile(Model model, HttpServletRequest request){
        UserLoginInfo userLoginInfo = (UserLoginInfo) request.getSession().getAttribute("userLoginInfo");
        if (userLoginInfo==null){
            return "redirect:/user/login";
        }
        String userId = userLoginInfo.getUserId();
        String userName = userLoginInfo.getUserName();
        model.addAttribute("username", userName);

        User user = userService.getUserById(userId);
        String email = user.getEmail();
        model.addAttribute("email",email);
        model.addAttribute("user",user);


        List<Record> records = getRecordByUser(userId).getContent();
        List<RecordInfo> infoList = new ArrayList<>();
        for (Record r:records){
            GarbageBin bin = garbageService.getBinById(r.getGarbageBinId());
            String location = bin.getLocation();
            RecordInfo recordInfo = new RecordInfo(location,userId,r.getType(),r.getGmtCreated());
            infoList.add(recordInfo);
        }
        model.addAttribute("records",infoList);
        return "user-profile";
    }


    @RequestMapping("/forgotPassword")
    public String resetPassword(){
        return "forgot-password";
    }

    @RequestMapping("/send")
    public String send(@RequestParam String email,HttpServletRequest request,Model model){
        LOG.warn("send方法被调用");
        UserQueryParam param = new UserQueryParam();
        param.setEmail(email);
        Page<User> list = userService.list(param);
        if (list == null || list.isEmpty()){
            model.addAttribute("msg","用户不存在,请先注册");
            return "forgot-password";
        }
        if (list.getContent().size()>1){
            LOG.warn("不止一个用户，请注销账号或联系管理员");
            model.addAttribute("msg","不止一个用户，请注销账号或联系管理员");
            return "forgot-password";
        }
        User user = list.getContent().get(0);
        String url = "127.0.0.1/user/forgotPassword";
        try {
            MailUtil.sendEmail(url,email);
            request.setAttribute("userId",user.getId());
        } catch (Exception e) {
            e.printStackTrace();
            LOG.error("failed to send email");
        }
        LOG.warn("邮件发送成功，请查看邮箱");
        model.addAttribute("msg","邮件发送成功，请查看邮箱");
        return "forgot-password";
    }

    @RequestMapping("/reset")
    public String resetPwd(@RequestParam String pwd,@RequestParam String pwd2
            ,int code,HttpServletRequest request,Model model){
        //TODO: 加入redis判断
        String userId = (String) request.getSession().getAttribute("userId");
        User user = userService.getUserById(userId);
        if (user==null){
            model.addAttribute("msg","未找到用户，请联系管理员");
            return "forgot-password";
        }
        if (!pwd.equals(pwd2)){
            model.addAttribute("msg","2次输入密码不匹配!");
            return "forgot-password";
        }
        user.setPassword(pwd);
        userService.modifyUser(user);
        model.addAttribute("msg","密码修改成功，请用新密码登录");
        return "login";
    }


    private Page<Record> getRecordByUser(String userId){
        RecordQueryParam param = new RecordQueryParam();
        param.setOwnerId(userId);
        return recordService.list(param);
    }

}
