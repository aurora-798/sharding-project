package com.xing;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.xing.mapper")
public class FileCenterBackendApplication {
    public static void main(String[] args) {
        SpringApplication.run(FileCenterBackendApplication.class, args);
    }

}
