package com.xing;

import com.xing.service.CategoryService;
import com.xing.service.FileService;
import com.xing.service.UserService;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

//@SpringBootTest
public class ServiceTest {

    @Resource
    private UserService userService;


    @Resource
    private CategoryService categoryService;

    @Resource
    private FileService fileService;

    @Test
    void testUser() {
        System.out.println(userService.list());
    }

    @Test
    void testCategory() {
        System.out.println(categoryService.list());
    }


    @Test
    void testFile() {
        System.out.println(fileService.list());
    }
}

