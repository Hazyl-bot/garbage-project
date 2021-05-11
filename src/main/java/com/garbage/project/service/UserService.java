package com.garbage.project.service;

import com.garbage.project.model.User;
import com.garbage.project.param.UserQueryParam;
import org.springframework.data.domain.Page;

import java.util.List;

public interface UserService {

    List<User> getAll();

    Page<User> list(UserQueryParam param);

    User add(User user);

    boolean deleteUser(String id);

    /**
     * 邮箱验证，修改密码，修改用户资料and so on...
     * */
    boolean modifyUser(User user);

    User getUserById(String id);
}
