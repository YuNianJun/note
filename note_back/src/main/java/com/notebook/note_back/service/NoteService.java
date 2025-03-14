package com.notebook.note_back.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.notebook.note_back.common.response.ResponseData;
import com.notebook.note_back.pojo.dto.NoteDto;
import com.notebook.note_back.pojo.vo.NoteVo;

import java.util.List;

public interface NoteService {
    ResponseData save(NoteVo note);

    ResponseData update(NoteVo note);

    IPage<NoteDto> pageQuery(NoteVo note);

    ResponseData delete(Integer id);

    ResponseData updateStatus(Integer id);

    ResponseData updateTop(Integer id);

    ResponseData getById(Integer id);

    ResponseData getByTags(String tags);

    ResponseData deleteIds(List<Integer> ids);

    ResponseData search(String title);

}
