package com.xing.service.impl;
import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xing.mapper.UserMapper;
import com.xing.model.User;
import com.xing.model.dto.UserDto;
import com.xing.model.vo.LoginVo;
import com.xing.result.JsonResult;
import com.xing.service.UserService;
import com.xing.util.TokenUtils;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import java.nio.charset.StandardCharsets;

@Service
@DS("remote")
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Resource
    private UserMapper userMapper;


    @Override
    public JsonResult login(UserDto userDto) {
        String email = userDto.getEmail();
        String password = userDto.getPassword();
        String md5Pwd = DigestUtils.md5DigestAsHex(password.getBytes(StandardCharsets.UTF_8));
        LambdaQueryWrapper<User> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(User::getEmail,email);
        User user = userMapper.selectOne(lambdaQueryWrapper);
        if (user == null) {
            return JsonResult.fail(402,"用户不存在，请注册");
        }
        String targetPwd = user.getPassword();
        if (!targetPwd.equals(md5Pwd) || !user.getEmail().equals(email)) {
            return  JsonResult.fail("账号或者密码错误");
        }
        // 生成JWT给前端
        String token = TokenUtils.token(user.getNickName());
        return JsonResult.OK(new LoginVo(token, user.getNickName()));
    }

}
