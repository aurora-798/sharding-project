package com.xing;

import com.xing.util.TokenUtils;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

//@SpringBootTest
public class TokenTest {

    @Test
    void test() {
        String changge = TokenUtils.token("changge");
        System.out.println(changge);
        System.out.println("解密:" + TokenUtils.decodeToken(changge));
        System.out.println("验证: " + TokenUtils.verify(changge));
        System.out.println("验证:" + TokenUtils.verify(changge + "1"));
    }
}
