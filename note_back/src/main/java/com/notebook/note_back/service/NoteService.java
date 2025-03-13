package com.notebook.note_back.service;

import com.notebook.note_back.common.response.ResponseData;
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

}
