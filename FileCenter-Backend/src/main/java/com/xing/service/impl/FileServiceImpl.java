package com.xing.service.impl;
import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xing.mapper.CategoryMapper;
import com.xing.mapper.FileMapper;
import com.xing.model.Category;
import com.xing.model.File;
import com.xing.model.dto.FileDto;
import com.xing.model.dto.MergeDto;
import com.xing.model.vo.BytesVo;
import com.xing.model.vo.FileVo;
import com.xing.result.JsonResult;
import com.xing.service.FileService;
import com.xing.util.ByteUtils;
import jakarta.annotation.Resource;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@DS("master")
public class FileServiceImpl extends ServiceImpl<FileMapper, File> implements FileService {
    @Autowired
    private FileMapper fileMapper;
    @Resource
    private CategoryMapper categoryMapper;

    private static final String directoryPath = "/www" + java.io.File.separator + "data";
    /**
     * 上传文件
     * @return
     */
    @Override
    @Transactional
    public JsonResult upload(FileDto fileDto) {

        //  //  H://data//dadadadadada
        // 1.以文件的hash作为文件夹
        String cutPath = directoryPath + java.io.File.separator +  fileDto.getFileHash();
        java.io.File f = new java.io.File(cutPath);
        if (!f.exists()) {
            f.mkdirs();
        }
        // 2.将文件的切片放入到此文件夹中
        String cutHash = fileDto.getCutHash();
        MultipartFile cut = fileDto.getCut();
        try {
            java.io.File cutF = new java.io.File(cutPath,cutHash);
            cut.transferTo(cutF);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return JsonResult.OK("上传成功");
    }

    @Override
    @Transactional
    public JsonResult merge(MergeDto mergeDto) {
        String fileName = mergeDto.getFileName();
        Integer cutSize = mergeDto.getCutSize();
        String fileHash = mergeDto.getFileHash();
        Integer fileSize = mergeDto.getFileSize();
        Integer category = mergeDto.getCategory();

        // H://data//dadadadada
        String mergePath = directoryPath + java.io.File.separator + fileHash;
        java.io.File f = new java.io.File(mergePath);
        if (!f.exists()) {
            System.out.println("未找到要合并的目录");
            return JsonResult.fail("合并失败，请重新上传文件");
        }
        // H://data//fileName.exe
        java.io.File targetFilePath =
                new java.io.File(directoryPath + java.io.File.separator +  fileName);
        System.out.println("合并文件名:" + targetFilePath);

        String outPutPath =  mergePath + java.io.File.separator  + fileHash  +"-";

        try (BufferedOutputStream mergingStream = new BufferedOutputStream(new FileOutputStream(targetFilePath))) {
            for (int i = 0; i < cutSize; i++) {
                // 构建分片文件的路径
                Path cutPath = Paths.get(outPutPath + i);
                // 将分片文件的内容复制到合并文件的输出流中
                Files.copy(cutPath, mergingStream);
                // 删除已经复制的分片文件
                Files.delete(cutPath);
            }
            //删除切片文件夹
            f.delete();
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResult.fail("上传失败,请重新上传");
        }

        BytesVo bytesVo = ByteUtils.convertBytes(fileSize);
        // 查询是否首次上传该数据
        File hash = fileMapper.selectByHash(fileHash);
        //1.文件是新文件，在数据库中没有记录
        if (hash == null) {
            // 保存文件信息到数据库中
            File file = File.builder()
                    .fileName(fileName)
                    .userId("1")
                    .size(bytesVo.getVal())
                    .suffix(bytesVo.getUnit())
                    .categoryId(category)
                    .downloadCount(0)
                    .hash(fileHash)
                    .createTime(LocalDateTime.now())
                    .updateTime(LocalDateTime.now())
                    .isDelete(0).build();
            fileMapper.insert(file);
        }
        // 2. 重复插入只更新时间
        else if (hash.getIsDelete() == 0) {
            hash.setUpdateTime(LocalDateTime.now());
            fileMapper.CustomUpdateIsDeleteById(hash);
        }
        // 3. 删除过文件再次插入在更新时间还要修改为未删除
        else if(hash.getIsDelete() == 1) {
            hash.setUpdateTime(LocalDateTime.now());
            hash.setIsDelete(0);
            fileMapper.CustomUpdateIsDeleteById(hash);
        }
        return JsonResult.OK("合并成功");
    }

    /**
     * 返回列表数据
     * @return
     */
    @Override
    public List<FileVo> listVo() {
        List<FileVo> fileVoList = new ArrayList<>();
        List<Category> categoryList = categoryMapper.selectList(null);
        List<File> fileList = fileMapper.selectList(null);
        for (Category category : categoryList) {
            Long categoryId = category.getId();
            String categoryName = category.getCategoryName();
            List<File> fileResult = fileList.stream().filter(file -> Long.valueOf(file.getCategoryId()) == categoryId)
                    .collect(Collectors.toList());
            fileVoList.add(new FileVo(categoryName,fileResult));
        }
        return fileVoList;
    }


    /**
     * 下载文件
     * @param path
     * @param response
     * @return
     */
    @Override
    @Transactional
    public JsonResult download(String path, HttpServletResponse response) {
        // 读到流中
        InputStream inputStream = null;// 文件的存放路径
        try {
            inputStream = new FileInputStream(directoryPath + java.io.File.separator + path);
            response.reset();
            response.setContentType("application/octet-stream");
            String filename = new java.io.File(path).getName();
            response.addHeader("Content-Disposition", "attachment; filename=" + URLEncoder.encode(filename, "UTF-8"));
            ServletOutputStream outputStream = response.getOutputStream();
            byte[] b = new byte[65536];
            int len;

            // 下载次数要+1
            LambdaQueryWrapper<File> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(File::getFileName,path);
            File file = fileMapper.selectOne(lambdaQueryWrapper);
            if (file == null) {
                return JsonResult.fail("下载失败，文件貌似已经不存在");
            }
            file.setDownloadCount(file.getDownloadCount() + 1);
            fileMapper.PlusDownLoadCount(file);
            //从输入流中读取一定数量的字节，并将其存储在缓冲区字节数组中，读到末尾返回-1
            while ((len = inputStream.read(b)) > 0) {
                outputStream.write(b, 0, len);
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return JsonResult.OK("下载完成");
    }

    /**
     * 删除文件
     * @param fileName
     * @return
     */
    @Override
    @Transactional
    public JsonResult del(String fileName)  {
        if (fileName == null) {
            return JsonResult.fail("文件不存在");
        }
        java.io.File file = new
                java.io.File(directoryPath + java.io.File.separator + fileName);
        if (!file.isFile()) {
            return JsonResult.fail("文件不存在");
        }
        Path path = Paths.get(directoryPath + java.io.File.separator + fileName);
        boolean res = false;
        try {
            res = Files.deleteIfExists(path);
            if (!res) {
                return JsonResult.fail("删除失败");
            }
            LambdaQueryWrapper<File> fileLambdaQueryWrapper = new LambdaQueryWrapper<>();
            fileLambdaQueryWrapper.eq(File::getFileName,fileName);
            int result = fileMapper.delete(fileLambdaQueryWrapper);
            if (result > 0)
                return JsonResult.OK("删除成功");
            return JsonResult.fail("删除失败");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
