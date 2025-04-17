package com.notebook.note_back.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.notebook.note_back.common.response.ResponseData;
import com.notebook.note_back.pojo.dto.NoteDto;
import com.notebook.note_back.pojo.entity.Comment;
import com.notebook.note_back.pojo.entity.NoteShare;
import com.notebook.note_back.pojo.vo.CommentVo;
import com.notebook.note_back.pojo.vo.NoteVo;

import java.util.List;

public interface NoteService {
    ResponseData save(NoteVo note);

    ResponseData update(NoteVo note);

    ResponseData pageQuery(NoteVo note);

    ResponseData delete(Integer id);

    ResponseData updateStatus(Integer id);

    ResponseData updateTop(Integer id);

    ResponseData getById(Integer id);

    ResponseData getByTags(String tags);

    ResponseData deleteIds(List<Integer> ids);

    ResponseData search(String title);

    ResponseData putRecycleBin(NoteVo vo);

    ResponseData saveComment(CommentVo comment);

    ResponseData shareNote(NoteVo vo);

    ResponseData viewSharedNote(NoteShare vo);

    ResponseData removeRecycleBin(NoteVo vo);

    ResponseData deleteComment(List<Integer> ids);

}
