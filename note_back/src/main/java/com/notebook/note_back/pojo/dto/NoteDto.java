package com.notebook.note_back.pojo.dto;


import lombok.Data;

@Data
public class NoteDto {
    private Integer page = 1;
    private Integer size = 10;
    /**
     * 标题
     * */
    private String title;
    /**
     * 标签
     * */
    private String tags;
    /**
     * 笔记内容
     * */
    private String body;
    /**
     * 完整内容
     * */
    private String content;

    private String createTime;

    private String updateTime;
    /**
     * 所属用户id
     * */
    private Integer userId;
    /**
     * 是否置顶(1:置顶, 0:不置顶)
     * */
    private Integer top;
    /**
     * 笔记状态(0:私有,1:公开)
     * */
    private Integer status;
}
