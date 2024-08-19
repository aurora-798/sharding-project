package com.xing.model.vo;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@AllArgsConstructor
@Builder
public class LoginVo implements Serializable {
    private String token;
    private String nickName;
}
