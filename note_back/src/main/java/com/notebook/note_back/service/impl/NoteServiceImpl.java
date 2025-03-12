package com.notebook.note_back.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.notebook.note_back.common.result.Result;
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
    public Result<Integer> save(NoteVo vo) {
        Note note = new Note();
        BeanUtils.copyProperties(vo, note);
        return Result.success(noteMapper.insert(note));
    }

    @Override
    public Result<Integer> update(NoteVo vo) {
        Note note = new Note();
        BeanUtils.copyProperties(vo, note);
        QueryWrapper<Note> wrapper = new QueryWrapper<>();
        return Result.success(noteMapper.update(note, wrapper));
    }

    @Override
    public Result<Object> pageQuery(NoteVo vo) {
        Page<Note> page = new Page<>(vo.getPage(), vo.getSize());
        return Result.success(noteMapper.selectPage(page, null));

    }

    @Override
    public Result<Integer> delete(Integer id) {
        return Result.success(noteMapper.deleteById(id));
    }

    @Override
    public Result<Integer> updateStatus(Integer status,Integer id) {
        int result = noteMapper.updateStatusById(id, status);
        return Result.success(result);
    }

    @Override
    public Result<Integer> updateTop(Integer id) {
        return null;
    }

    @Override
    public Result<Object> getById(Integer id) {
        return null;
    }

    @Override
    public Result<Object> getByTags(String tags) {
        return null;
    }

    @Override
    public Result<Integer> deleteIds(List<Integer> ids) {
        return null;
    }
}
