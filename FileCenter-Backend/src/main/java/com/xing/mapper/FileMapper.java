package com.xing.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xing.model.File;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface FileMapper extends BaseMapper<File> {
    File selectByHash(String hash);

    Integer CustomUpdateIsDeleteById(File files);

    Integer PlusDownLoadCount(File file);
}
