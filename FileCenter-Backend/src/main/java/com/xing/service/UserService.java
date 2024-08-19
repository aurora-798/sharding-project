package com.xing.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xing.model.User;
import com.xing.model.dto.UserDto;
import com.xing.result.JsonResult;

public interface UserService extends IService<User> {
    JsonResult login(UserDto userDto);
}
