package com.xing;


import com.xing.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

//@SpringBootTest
public class MoreDataSourceTest {

    @Autowired
    private UserService userService;


    @Test
    void Test() {
        System.out.println(userService.list());
    }
}
