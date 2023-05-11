package com.wuyi.wyms.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wuyi.wyms.model.domain.User;

/**
* @author 高阳
* @description 针对表【tb_user】的数据库操作Service
* @createDate 2023-03-06 15:30:01
*/
public interface UserService extends IService<User> {

    Long userRegister(String userAccount, String userPassword, String checkPassword);

    String userLogin(String userAccount, String userPassword);

    long userLogout(String token);

    long userModify(User user, String token);
}
