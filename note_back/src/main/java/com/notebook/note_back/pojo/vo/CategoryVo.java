package com.notebook.note_back.pojo.vo;

import com.notebook.note_back.pojo.entity.Note;
import lombok.Data;

import java.util.List;

@Data
public class CategoryVo {
    private Integer page = 1;
    private Integer size = 10;
    private Integer id;
    private String name;
    private String userId;
    private String synopsis;         // 书架简介
    private List<Note> notes;        // 笔记列表
    private Integer status;          // 笔记状态
}
