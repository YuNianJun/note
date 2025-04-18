package com.notebook.note_back.service;

import com.notebook.note_back.common.response.ResponseData;
import com.notebook.note_back.pojo.vo.CommentVo;

import java.util.List;

public interface CommentService {
    ResponseData saveComment(CommentVo vo);

    ResponseData deleteComment(List<Integer> ids);

    ResponseData pageComment(CommentVo vo);
}
