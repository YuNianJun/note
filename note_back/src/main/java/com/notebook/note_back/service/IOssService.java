package com.notebook.note_back.service;

import org.springframework.web.multipart.MultipartFile;

public interface IOssService {

    Object upload(MultipartFile file);

}
