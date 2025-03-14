package com.notebook.note_back.pojo.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("note")
public class Note {

    @TableId(value = "id",type = IdType.AUTO)
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

    /**
     * 所属书架
     * */
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
