package com.xing;


import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.DigestUtils;

import java.nio.charset.StandardCharsets;

//@SpringBootTest
public class HuToolsTest {


    @Test
    void Md5test() {
        //391715f4b72c4f0988d2c7d4c3532fdd
        System.out.println(DigestUtils.md5DigestAsHex("520707".getBytes(StandardCharsets.UTF_8)));
    }
}
