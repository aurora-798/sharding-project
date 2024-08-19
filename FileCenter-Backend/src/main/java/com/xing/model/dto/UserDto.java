package com.xing.model.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class UserDto implements Serializable {
    String email;
    String password;
}
