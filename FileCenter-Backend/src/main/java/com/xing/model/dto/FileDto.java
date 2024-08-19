package com.xing.model.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.io.Serializable;

@Data
public class FileDto implements Serializable {
    private String fileHash;
    private String cutHash;
    private MultipartFile cut;
}
