package com.notebook.note_back.controller;

import com.notebook.note_back.common.utils.LoadUtil;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@RequestMapping("upload")
@RequiredArgsConstructor
public class UploadFileController {
    @Value("${file.windows.path}")
    private String basePath;

    /**
     * 上传图片
     */
    @PostMapping("uploadImg")
    public ResponseEntity<String> uploadImg(MultipartFile file) {
        String imgUrl = LoadUtil.uploadImg(file, basePath);
        log.info("上传图片地址：{}", imgUrl);
        return ResponseEntity.ok(imgUrl);
    }

    /**
     * 回显图片
     */
    @GetMapping("showImg")
    public void showImg(HttpServletResponse response, @RequestParam("imgUrl") String imgUrl) {
        log.info("回显图片地址：{}", imgUrl);
        LoadUtil.showImg(imgUrl, response);
    }


}
