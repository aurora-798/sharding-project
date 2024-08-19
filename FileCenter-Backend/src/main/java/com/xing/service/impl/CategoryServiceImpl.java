package com.xing.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xing.mapper.CategoryMapper;
import com.xing.model.Category;
import com.xing.service.CategoryService;
import org.springframework.stereotype.Service;

@Service
@DS("master")
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category>
        implements CategoryService {

}
