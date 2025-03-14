package com.notebook.note_back.pojo.vo;


import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

@Data
public class NoteVo {
    private Integer page = 1;
    private Integer size = 10;
    private Integer id;
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

    private Integer categoryId;
    @TableField(value = "create_time",fill = FieldFill.INSERT)
    private String createTime;
    @TableField(value = "update_time",fill = FieldFill.UPDATE)
    private String updateTime;

    /**
     * 是否置顶(1:置顶, 0:不置顶)
     * */
    private Integer top;
    /**
     * 笔记状态(0:私有,1:公开)
     * */
    private Integer status;

    private Integer userId;
}
