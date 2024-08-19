package com.xing.util;

import com.xing.model.vo.BytesVo;

public class ByteUtils {
    // 将byte -> 转化
    public static BytesVo convertBytes(long bytes) {
        if (bytes < 1024) {
            return new BytesVo(bytes * 1.0, "B");
        } else if (bytes < 1024 * 1024) {
            return new BytesVo(Double.parseDouble(String.format("%.2f", bytes / 1024.0)), "KB");
        } else if (bytes < 1024 * 1024 * 1024) {
            return new BytesVo(Double.parseDouble(String.format("%.2f", bytes / (1024.0 * 1024))), "MB");
        } else {
            return new BytesVo(Double.parseDouble(String.format("%.2f", bytes / (1024.0 * 1024 * 1024))), "GB");
        }
    }
}
