package com.notebook.note_back.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.notebook.note_back.common.response.ResponseData;
import com.notebook.note_back.common.utils.ThreadLocalUtil;
import com.notebook.note_back.mapper.CommentMapper;
import com.notebook.note_back.pojo.entity.Comment;
import com.notebook.note_back.pojo.vo.CommentVo;
import com.notebook.note_back.service.CommentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentMapper commentMapper;

    @Override
    public ResponseData saveComment(CommentVo vo) {
        Map<String, Object> map = ThreadLocalUtil.get();
        Integer userId = (Integer) map.get("id");
        Comment comment = new Comment();
        comment.setNoteId(vo.getNoteId());
        comment.setUserId(userId);
        comment.setContent(vo.getContent());
        comment.setCreateTime(LocalDateTime.now());

        return ResponseData.success(commentMapper.insert(comment));
    }

    @Override
    public ResponseData deleteComment(List<Integer> ids) {
        return ResponseData.success(commentMapper.deleteByIds(ids));
    }

    @Override
    public ResponseData pageComment(CommentVo vo) {
        Page<Comment> page = new Page<>(vo.getPage(), vo.getSize());

        QueryWrapper<Comment> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("note_id", vo.getNoteId()); // 根据笔记 ID 查询

        IPage<Comment> commentPage = commentMapper.selectPage(page, queryWrapper);

        return ResponseData.success(commentPage);
    }
}
