package com.notebook.note_back.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.notebook.note_back.common.response.ResponseData;
import com.notebook.note_back.mapper.NoteMapper;
import com.notebook.note_back.pojo.entity.Note;
import com.notebook.note_back.pojo.vo.NoteVo;
import com.notebook.note_back.service.NoteService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NoteServiceImpl implements NoteService {

    private final NoteMapper noteMapper;

    @Override
    public ResponseData save(NoteVo vo) {
        Note note = new Note();
        BeanUtils.copyProperties(vo, note);
        return ResponseData.success(noteMapper.insert(note));
    }

    @Override
    public ResponseData update(NoteVo vo) {
        Note note = new Note();
        BeanUtils.copyProperties(vo, note);
        QueryWrapper<Note> wrapper = new QueryWrapper<>();
        return ResponseData.success(noteMapper.update(note, wrapper));
    }

    @Override
    public ResponseData pageQuery(NoteVo vo) {
        Page<Note> page = new Page<>(vo.getPage(), vo.getSize());
        return ResponseData.success(noteMapper.selectPage(page, null));

    }

    @Override
    public ResponseData delete(Integer id) {
        return ResponseData.success(noteMapper.deleteById(id));
    }

    @Override
    public ResponseData updateStatus(Integer id) {
        int result = noteMapper.updateStatusById(id);
        return ResponseData.success(result);
    }

    @Override
    public ResponseData updateTop(Integer id) {
        int result = noteMapper.updateTopById(id);
        return ResponseData.success(result);
    }

    @Override
    public ResponseData getById(Integer id) {
        return ResponseData.success(noteMapper.selectById(id));
    }

    @Override
    public ResponseData getByTags(String tags) {
        List<String> titles = noteMapper.selectTitlesByTag(tags);
        return ResponseData.success(titles);
    }

    @Override
    public ResponseData deleteIds(List<Integer> ids) {
        return ResponseData.success(noteMapper.deleteBatchIds(ids));
    }

    @Override
    public ResponseData search(String title) {
        return ResponseData.success(noteMapper.selectOne(new QueryWrapper<Note>().like("title", title)));
    }
}
