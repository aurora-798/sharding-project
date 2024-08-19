package com.xing.controller;

import com.xing.model.User;
import com.xing.model.dto.UserDto;
import com.xing.result.JsonResult;
import com.xing.service.UserService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class UserController {


    @Resource
    private UserService userService;

    /**
     * 登录接口
     */
    @PostMapping("/login")
    public JsonResult login(@RequestBody UserDto userDto){
        if (userDto == null || userDto.getPassword() == null || userDto.getEmail() == null
        || userDto.getEmail().equals("") || userDto.getPassword().equals("")) {
            return  JsonResult.fail("请求参数为空");
        }
        return userService.login(userDto);
    }
}
