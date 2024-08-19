package com.xing.controller;
import com.xing.model.dto.DownLoadDto;
import com.xing.model.dto.FileDto;
import com.xing.model.dto.MergeDto;
import com.xing.result.JsonResult;
import com.xing.service.FileService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/file")
@CrossOrigin
public class FileController {

    @Resource
    private FileService fileService;


    /**
     * 上传切片文件
     * @return
     */
    @PostMapping("/upload")
    public JsonResult upload(FileDto fileDto) {
        if (fileDto == null || fileDto.getFileHash() == null || fileDto.getFileHash().equals("")
                || fileDto.getCutHash() == null || fileDto.getCutHash().equals("") ||
                fileDto.getCut() == null) {
            return JsonResult.fail("上传失败,请重新上传.");
        }
        return fileService.upload(fileDto);
    }

    @PostMapping("/merge")
    public JsonResult merge(@RequestBody MergeDto mergeDto) {
        if(mergeDto == null || mergeDto.getFileName() == null || mergeDto.getFileName().equals("") ||
                mergeDto.getCutSize() == null || mergeDto.getFileHash() == null ||
                mergeDto.getFileHash().equals("") || mergeDto.getFileSize() == null
        || mergeDto.getCategory() == null) {
            System.out.println("缺少参数");
            return JsonResult.fail("合并失败，请重新上传文件");
        }
        return fileService.merge(mergeDto);
    }


    /**
     *  文件列表
     */
    @PostMapping("/list")
    public JsonResult list() {
        return JsonResult.OK(fileService.listVo());
    }


    /**
     * 下载文件
     */
    @GetMapping("/download/{path}")
    public JsonResult download(@PathVariable String path, HttpServletResponse response) {
        return JsonResult.OK(fileService.download(path,response));
    }


    /**
     * 删除文件
     */
    @PostMapping ("/del")
    public JsonResult delete(@RequestBody DownLoadDto downLoadDto) {
        if (downLoadDto == null || downLoadDto.getFileName() == null) {
            return JsonResult.fail("参数为空");
        }
        return JsonResult.OK(fileService.del(downLoadDto.getFileName()));
    }

}
