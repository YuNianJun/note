package com.notebook.note_back.service;

import com.notebook.note_back.common.result.Result;
import com.notebook.note_back.pojo.vo.NoteVo;

import java.util.List;

public interface NoteService {
    Result<Integer> save(NoteVo note);

    Result<Integer> update(NoteVo note);

    Result<Object> pageQuery(NoteVo note);

    Result<Integer> delete(Integer id);

    Result<Integer> updateStatus(Integer status,Integer id);

    Result<Integer> updateTop(Integer id);

    Result<Object> getById(Integer id);

    Result<Object> getByTags(String tags);

    Result<Integer> deleteIds(List<Integer> ids);

}
