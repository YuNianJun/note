package com.notebook.note_back.controller;

import com.notebook.note_back.common.response.ResponseData;
import com.notebook.note_back.pojo.vo.CommentVo;
import com.notebook.note_back.service.CommentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@CrossOrigin
@RestController
@RequiredArgsConstructor
@RequestMapping("/comment")
public class CommentController {

    private final CommentService commentService;
    /**
     * 新增用户评论
     * */
    @PostMapping("/save")
    public ResponseData saveComment(@RequestBody CommentVo vo) {
        log.info("用户评论：{}",vo);
        return commentService.saveComment(vo);
    }
    /**
     * 删除用户评论
     * */
    @PostMapping("/delete/ids")
    public ResponseData deleteComment(@RequestBody CommentVo vo) {
        log.info("删除用户评论：{}",vo.getIds());
        return commentService.deleteComment(vo.getIds());
    }

    /**
     * 根据笔记id分页查询评论
     * */
    @PostMapping("/page")
    public ResponseData pageComment(@RequestBody CommentVo vo) {
        log.info("根据笔记id分页查询评论：{}",vo);
        return commentService.pageComment(vo);
    }
}
