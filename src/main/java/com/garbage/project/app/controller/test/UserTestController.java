package com.garbage.project.app.controller.test;

import com.garbage.project.model.Record;
import com.garbage.project.model.User;
import com.garbage.project.param.RecordQueryParam;
import com.garbage.project.param.UserQueryParam;
import com.garbage.project.service.RecordService;
import com.garbage.project.service.UserService;
import io.lettuce.core.dynamic.annotation.Param;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/usertest")
public class UserTestController {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserTestController.class);

    private UserService userService;

    private RecordService recordService;

    @Autowired
    public UserTestController(UserService userService, RecordService recordService) {
        this.userService = userService;
        this.recordService = recordService;
    }

    @GetMapping("/testadd")
    public String add(@RequestParam String name,@RequestParam String email, @RequestParam String password, HttpServletRequest request){
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        //user.setId("testforaid");
        user.setPassword(password);
        User user1 = userService.add(user);
        return user1.toString();
    }

    @GetMapping("/testdel")
    public String delete(){
        UserQueryParam uqp = new UserQueryParam();
        uqp.setName("xiaoming");
        Page<User> list = userService.list(uqp);
        User user = list.getContent().get(0);
        return user.toString();
    }

    @GetMapping("/testmodify")
    public String modify(){
        User user = new User();
        user.setId("testforaid");
        user.setName("xiaopeng");
        boolean b = userService.modifyUser(user);
        if(b){
            return "modify succeed";
        }else {
            return "modify failed";
        }
    }

    @GetMapping("/getall")
    public String getAll(){
        return userService.getAll().toString();
    }

    @GetMapping("/getfullUser")
    public String testGetFullUser(){
        User user = new User();
        user.setId("testforaid");
        RecordQueryParam param = new RecordQueryParam();
        param.setOwnerId(user.getId());
        Page<Record> list = recordService.list(param);
        List<Record> content = list.getContent();
        user.setRecord(content);
        return user.toString();
    }

    @GetMapping("/getById")
    public String testGetById(String id){
        User userById = userService.getUserById(id);
        System.out.println(userById.toString());
        return userById.toString();
    }
}
