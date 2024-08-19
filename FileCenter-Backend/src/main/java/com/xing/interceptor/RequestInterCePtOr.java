package com.xing.interceptor;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.xing.model.User;
import com.xing.result.JsonResult;
import com.xing.service.UserService;
import com.xing.util.TokenUtils;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.io.PrintWriter;

@Component
@Slf4j
public class RequestInterCePtOr implements HandlerInterceptor {


    @Resource
    private UserService userService;
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (request.getRequestURI().contains("/login") || request.getRequestURI().contains("/list")) {
            return true;
        }
        //获取token
        String token = request.getHeader("token");


        if(token == null || token.equals("")) {
            // 从url中尝试获取token
            token = request.getParameter("token");
        }


        if (token == null || token.equals("")) {
            PrintWriter writer = null;
            response.setCharacterEncoding("UTF-8");
            response.setContentType("text/html; charset=utf-8");
            try {
                writer = response.getWriter();
                JsonResult result = JsonResult.fail(407, "非法操作,token无效");
                ObjectMapper objectMapper = new ObjectMapper();
                writer.print(objectMapper.writeValueAsString(result));
            }catch (IOException e) {

            }finally {
                if (writer != null) {
                    writer.close();
                }
            }
            return false;
        }

        // 校验token是否合法
        boolean verify = TokenUtils.verify(token);

        if (!verify) {
            PrintWriter writer = null;
            response.setCharacterEncoding("UTF-8");
            response.setContentType("text/html; charset=utf-8");
            try {
                writer = response.getWriter();
                JsonResult result = JsonResult.fail(407, "非法操作");
                ObjectMapper objectMapper = new ObjectMapper();
                writer.print(objectMapper.writeValueAsString(result));
            }catch (IOException e) {

            }finally {
                if (writer != null) {
                    writer.close();
                }
            }
            return false;
        }



        // 校验token是否存在
        String nickName = TokenUtils.decodeToken(token);
        LambdaQueryWrapper<User> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(User::getNickName,nickName);
        User user = userService.getOne(lambdaQueryWrapper);
        if (user == null) {
            PrintWriter writer = null;
            response.setCharacterEncoding("UTF-8");
            response.setContentType("text/html; charset=utf-8");
            try {
                writer = response.getWriter();
                JsonResult result = JsonResult.fail(407, "非法操作");
                ObjectMapper objectMapper = new ObjectMapper();
                writer.print(objectMapper.writeValueAsString(result));
            }catch (IOException e) {
            }finally {
                if (writer != null) {
                    writer.close();
                }
            }
            return false;
        }

        // 放行
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        HandlerInterceptor.super.postHandle(request, response, handler, modelAndView);
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        HandlerInterceptor.super.afterCompletion(request, response, handler, ex);
    }
}
