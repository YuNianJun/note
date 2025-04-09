package com.notebook.note_back.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.notebook.note_back.pojo.entity.Comment;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CommentMapper extends BaseMapper<Comment> {

}
