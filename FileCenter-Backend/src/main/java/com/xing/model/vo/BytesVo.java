package com.xing.model.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Data
@Builder
public class BytesVo {
    /**
     * 数值
     */
    private Double val;

    /**
     * 单位
     */
    private String unit;
}
