package com.xing.controller;

import com.xing.model.Category;
import com.xing.result.JsonResult;
import com.xing.service.CategoryService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/category")
@CrossOrigin
public class CategoryController {
    @Resource
    private CategoryService categoryService;


    @GetMapping("/list")
    public JsonResult list() {
        List<Category> list = categoryService.list();
        List<String> CategoryNameList = list.stream().map(category -> category.getCategoryName())
                .collect(Collectors.toList());
        return JsonResult.OK(CategoryNameList);
    }
}
