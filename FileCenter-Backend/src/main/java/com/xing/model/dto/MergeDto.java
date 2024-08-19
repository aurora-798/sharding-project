package com.xing.model.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class MergeDto implements Serializable {
    /**
     * 切片数量
     */
    private Integer cutSize;

    /**
     * 文件名
     */
    private String fileName;


    /**
     * 文件哈希
     */
    private String fileHash;

    /**
     * 文件大小
     */
    private Integer fileSize;

    /**
     * 分类
     */
    private Integer category;

}
