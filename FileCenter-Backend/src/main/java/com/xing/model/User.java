package com.xing.model;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;


@Data
@Builder
@TableName("user_info")
public class User implements Serializable {
    private String userId;
    private String nickName;
    private String email;
    private String password;
    private Integer sex;
    private String personDescription;
    private LocalDateTime joinTime;
    private LocalDateTime lastLoginTime;
    private String lastLoginIp;
    private String lastLoginIpAddress;
    private Integer totalIntegral;
    private Integer currentIntegral;
    private Integer status;
}
