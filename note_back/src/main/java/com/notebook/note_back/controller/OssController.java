package com.notebook.note_back.controller;

import com.notebook.note_back.common.response.ResponseData;
import com.notebook.note_back.common.utils.AliOssUtil;
import com.notebook.note_back.service.IOssService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("")
@RequiredArgsConstructor
public class OssController {

    private final AliOssUtil aliOssUtil;

    @PostMapping("/upload")
    public ResponseData upload(@RequestParam("file") MultipartFile file ){
        log.info("文件上传：{}",file);

        try {
            //原始文件名
            String originalFilename = file.getOriginalFilename();
            //截取原始文件名的后缀   dfdfdf.png
            String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            //构造新文件名称
            String objectName = UUID.randomUUID().toString() + extension;

            //文件的请求路径
            String filePath = aliOssUtil.upload(file.getBytes(), objectName);
            return ResponseData.success(filePath);
        } catch (IOException e) {
            log.error("文件上传失败：{}", e);
        }

        return ResponseData.error("上传失败");
    }
}
