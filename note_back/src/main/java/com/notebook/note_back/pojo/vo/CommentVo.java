package com.notebook.note_back.pojo.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CommentVo {
    private Integer id;
    private Integer userId;
    private Integer noteId;
    private String content;
    private LocalDateTime createTime;
}
