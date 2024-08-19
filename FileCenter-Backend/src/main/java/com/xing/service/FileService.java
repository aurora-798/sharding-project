package com.xing.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xing.model.File;
import com.xing.model.dto.FileDto;
import com.xing.model.dto.MergeDto;
import com.xing.model.vo.FileVo;
import com.xing.result.JsonResult;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface FileService extends IService<File> {


    /*
    1. 新文件上传 -> 数据库没有记录
    2. 将文件删除，再次上传文件 -> 数据库有删除记录

    * */
    JsonResult upload(FileDto fileDto);

    JsonResult merge(@RequestBody MergeDto mergeDto);

    List<FileVo> listVo();

    JsonResult download(String path, HttpServletResponse response);

    JsonResult del(String fileName);
}
