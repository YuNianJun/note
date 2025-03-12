package com.notebook.note_back.pojo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
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
