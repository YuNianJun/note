package com.notebook.note_back.pojo.dto;

import java.time.LocalDateTime;

public class CommentDto {
    private Integer id;
    private Integer userId;
    private Integer noteId;
    private String content;
    private LocalDateTime createTime;
}
