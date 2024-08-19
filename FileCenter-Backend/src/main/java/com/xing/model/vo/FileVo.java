package com.xing.model.vo;

import com.xing.model.File;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Builder
@Data
@AllArgsConstructor
public class FileVo  implements Serializable {
    private String categoryName;
    private List<File> fileList;
}
