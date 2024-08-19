package com.xing.result;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class JsonResult implements Serializable {
    private Integer code;
    private String msg;
    private Object data;


    public static JsonResult OK(Integer code,String msg,Object data) {
        return new JsonResult(code,msg,data);
    }

    public static JsonResult OK() {
        return new JsonResult(200,"OK",null);
    }

    public static JsonResult OK(Object data) {
        return new JsonResult(200,"OK",data);
    }

   public static JsonResult fail() {
        return new JsonResult(401,"error",null);
   }

    public static JsonResult fail(String msg) {
        return new JsonResult(401,msg,null);
    }

    public static JsonResult fail(Integer code,String msg) {
        return new JsonResult(code,msg,null);
    }
}
