package com.wuyi.wyms.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.lang.UUID;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wuyi.wyms.common.ErrorCode;
import com.wuyi.wyms.exception.BusinessException;
import com.wuyi.wyms.mapper.UserMapper;
import com.wuyi.wyms.model.domain.User;
import com.wuyi.wyms.model.dto.UserDto;
import com.wuyi.wyms.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.wuyi.wyms.utils.RedisConstants.LOGIN_USER_KEY;
import static com.wuyi.wyms.utils.RedisConstants.LOGIN_USER_TTL;

/**
* @author 高阳
* @description 针对表【tb_user】的数据库操作Service实现
* @createDate 2023-03-06 15:30:01
*/
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
implements UserService{

    @Resource
    private UserMapper userMapper;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 盐值，混淆密码
     */
    private static final String SALT = "wuyi";

    /**
     *用户注册
     */
    @Override
    public Long userRegister(String userAccount, String userPassword, String checkPassword) {
        //1.校验
        if(StringUtils.isAnyBlank(userAccount, userPassword, checkPassword)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"参数为空");
        }
        if (userAccount.length() < 1) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户账号为空");
        }
        if (userPassword.length() < 1 || checkPassword.length() < 1) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户密码过短");
        }
        //账号不能包含特殊字符
        String validPattern = "[`~!@#$%^&*()+=|{}':;',\\\\[\\\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
        Matcher matcher = Pattern.compile(validPattern).matcher(userAccount);
        if (matcher.find()) {
            return null;
        }
        // 密码和校验密码相同
        if (!userPassword.equals(checkPassword)) {
            return null;
        }
        // 账户不能重复
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("nick_name", userAccount);
        long count = userMapper.selectCount(queryWrapper);
        if (count > 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号重复");
        }
        //2.加密
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
        //3.插入数据
        User user = new User();
        user.setNickName(userAccount);
        user.setPassword(userPassword);
        boolean result = this.save(user);
        if (!result){
            return null;
        }
        return user.getId();
    }

    /**
     *用户登录
     */
    @Override
    public String  userLogin(String userAccount, String userPassword) {
        // 1. 校验
        if (StringUtils.isAnyBlank(userAccount, userPassword)) {
            return null;
        }
        if (userAccount.length() < 1) {
            return null;
        }
        if (userPassword.length() < 1) {
            return null;
        }
        //排除特殊字符
        String validPattern = "[`~!@#$%^&*()+=|{}':;',\\\\[\\\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
        Matcher matcher = Pattern.compile(validPattern).matcher(userAccount);
        if (matcher.find()) {
            return null;
        }
        //2.加密
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
        //查询用户是否存在
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("nick_name",userAccount);
        queryWrapper.eq("password",encryptPassword);
        User user = userMapper.selectOne(queryWrapper);
        //用户不存在
        if(user == null){
            return null;
        }
        //3.记录登录状态
        //3.1、生成token作为登录令牌
        String token = UUID.randomUUID().toString(true);
        //3.2、将User对象转化为HashMap储存
        UserDto userDto = BeanUtil.copyProperties(user, UserDto.class);
        Map<String, Object> userMap = BeanUtil.beanToMap(userDto, new HashMap<>(),
                CopyOptions.create()
                        .setIgnoreNullValue(true)
                        .setFieldValueEditor((fieldName, fieldValue) -> fieldValue.toString()));
        //3.3将令牌以及对象存进Redis中
        String tokenKey = LOGIN_USER_KEY + token;
        stringRedisTemplate.opsForHash().putAll(tokenKey, userMap);
        //3.4设置token有效期
        stringRedisTemplate.expire(tokenKey, LOGIN_USER_TTL, TimeUnit.MINUTES);;
        return token;
    }

    /**
     *用户登出
     */
    @Override
    public long userLogout(String token) {
        stringRedisTemplate.delete(LOGIN_USER_KEY + token);
        return 1;
    }

    /**
     *修改用户个人信息
     */
    @Override
    public long userModify(User user, String token) {
        // 1. 校验
        if (StringUtils.isAnyBlank(user.getNickName(), user.getIcon(), user.getPassword(), user.getPhone())) {
            return -1;
        }
        //排除特殊字符
        String validPattern = "[`~!@#$%^&*()+=|{}':;',\\\\[\\\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
        Matcher matcher = Pattern.compile(validPattern).matcher(user.getNickName());
        if (matcher.find()) {
            return -1;
        }
        //2.加密
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + user.getPassword()).getBytes());
        //3.修改用户信息

        Map<Object, Object> map = stringRedisTemplate.opsForHash().entries(LOGIN_USER_KEY + token);

        Long id = Long.valueOf((String) map.get("id"));
        System.out.println(id);
        user.setId(id);
        user.setPassword(encryptPassword);
        int result = userMapper.updateById(user);
        if (result == 0){
            return -1;
        }
        //5.更新用户登录状态
        UserDto userDto = BeanUtil.copyProperties(user, UserDto.class);
        Map<String, Object> userMap = BeanUtil.beanToMap(userDto, new HashMap<>(),
                CopyOptions.create()
                        .setIgnoreNullValue(true)
                        .setFieldValueEditor((fieldName, fieldValue) -> fieldValue.toString()));
        stringRedisTemplate.opsForHash().putAll(LOGIN_USER_KEY + token, userMap);
        stringRedisTemplate.expire(LOGIN_USER_KEY + token, LOGIN_USER_TTL, TimeUnit.MINUTES);;
        return 1;
    }

}
