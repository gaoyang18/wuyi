package com.wuyi.wyms.utils;

import com.wuyi.wyms.model.dto.UserDto;

public class UserHolder {
    private static final ThreadLocal<UserDto> TL = new ThreadLocal<>();

    public static void saveUser(UserDto userDto){
        TL.set(userDto);
    }

    public static UserDto getUser(){
        return TL.get();
    }

    public static void removeUser(){
        TL.remove();
    }
}
