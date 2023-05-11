package com.wuyi.wyms.controller;

import com.wuyi.wyms.common.BaseResponse;
import com.wuyi.wyms.common.ErrorCode;
import com.wuyi.wyms.common.ResultUtils;
import com.wuyi.wyms.exception.BusinessException;
import com.wuyi.wyms.model.domain.User;
import com.wuyi.wyms.model.dto.UserDto;
import com.wuyi.wyms.model.request.UserLoginRequest;
import com.wuyi.wyms.model.request.UserRegisterRequest;
import com.wuyi.wyms.service.UserService;
import com.wuyi.wyms.utils.UserHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {

    @Resource
    private UserService userService;

    /**
     * 用户注册
     * @param userRegisterRequest 注册信息
     */
    @PostMapping("/register")
    public BaseResponse<Long> userRegister(@RequestBody UserRegisterRequest userRegisterRequest){
        if(userRegisterRequest == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Long result = userService.userRegister(userRegisterRequest.getUserAccount(), userRegisterRequest.getUserPassword(), userRegisterRequest.getCheckPassword());
        return ResultUtils.success(result);
    }

    /**
     *
     * @param userLoginRequest 登录信息
     * @param request
     */
    @PostMapping("/login")
    public BaseResponse<String> userLogin(@RequestBody UserLoginRequest userLoginRequest, HttpServletRequest request){
        if(userLoginRequest == null){
            return  ResultUtils.error(ErrorCode.PARAMS_ERROR);
        }
        return ResultUtils.success(userService.userLogin(userLoginRequest.getUserAccount(), userLoginRequest.getUserPassword()));
    }

    /**
     * 用户登出
     * @param token 登录令牌
     */
    @PostMapping("/logout")
    public BaseResponse<Long> userLogout(@RequestHeader String token) {
        long result = userService.userLogout(token);
        return ResultUtils.success(result);
    }

    /**
     * 返回用户信息
     */
    @GetMapping("/info")
    public BaseResponse<UserDto> userInfo(){
        UserDto userDto = UserHolder.getUser();
        return ResultUtils.success(userDto);
    }

    /**
     * 用户修改个人信息
     * @param user 用户信息
     * @param token 登录令牌
     */
    @PutMapping("/modify")
    public BaseResponse<Long> userModify(@RequestBody User user, @RequestHeader String token){
        long result = userService.userModify(user,token);
        return ResultUtils.success(result);
    }
}
