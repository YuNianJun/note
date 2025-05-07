package com.notebook.note_back.service.impl;

import com.aliyun.oss.OSSClient;
import com.notebook.note_back.service.IOssService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

import static org.springframework.boot.autoconfigure.liquibase.LiquibaseProperties.UiService.LOGGER;

@Service
@RequiredArgsConstructor
public class OssServiceImpl implements IOssService {


    @Override
    public Object upload(MultipartFile file) {
        return null;
    }
}
