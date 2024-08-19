package com.xing;

import com.xing.mapper.CategoryMapper;
import com.xing.mapper.FileMapper;
import com.xing.mapper.UserMapper;
import com.xing.model.File;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import java.util.List;

//@SpringBootTest
class MapperTest {

    @Autowired
    private FileMapper fileMapper;


    @Autowired
    private CategoryMapper categoryMapper;


    @Autowired
    private UserMapper userMapper;


    @Test
    void contextLoads() {
        File files = fileMapper.selectByHash("dada");
        System.out.println(files);
    }



    @Test
    void testCategory() {
        System.out.println(categoryMapper.selectList(null));
    }


    @Test
    void testUser() {
        System.out.println(userMapper.selectList(null));
    }
}
