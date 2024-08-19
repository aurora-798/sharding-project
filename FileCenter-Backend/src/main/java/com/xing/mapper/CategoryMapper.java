package com.xing.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xing.model.Category;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CategoryMapper extends BaseMapper<Category> {
}
