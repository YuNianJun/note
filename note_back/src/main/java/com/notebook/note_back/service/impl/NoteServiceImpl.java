package com.notebook.note_back.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.notebook.note_back.common.response.ResponseData;
import com.notebook.note_back.common.utils.ThreadLocalUtil;
import com.notebook.note_back.mapper.NoteMapper;
import com.notebook.note_back.pojo.dto.NoteDto;
import com.notebook.note_back.pojo.dto.UserDto;
import com.notebook.note_back.pojo.entity.Note;
import com.notebook.note_back.pojo.entity.User;
import com.notebook.note_back.pojo.vo.NoteVo;
import com.notebook.note_back.service.NoteService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NoteServiceImpl implements NoteService {

    private final NoteMapper noteMapper;
    @Override
    public ResponseData save(NoteVo vo) {
        Map<String, Object> map = ThreadLocalUtil.get();
        Integer userId = (Integer) map.get("id");
        Note note = new Note();
        BeanUtils.copyProperties(vo, note);
        note.setUserId(userId);
        return ResponseData.success(noteMapper.insert(note));
    }

    @Override
    public ResponseData update(NoteVo vo) {
        Note note = new Note();
        BeanUtils.copyProperties(vo, note);
        QueryWrapper<Note> wrapper = new QueryWrapper<>();
        wrapper.eq("id", vo.getId());
        return ResponseData.success(noteMapper.update(note, wrapper));
    }

    @Override
    public IPage<NoteDto> pageQuery(NoteVo vo) {
        QueryWrapper<Note> queryWrapper = new QueryWrapper<>();
        if (null != vo.getTitle() && !vo.getTitle().isEmpty()) {
            queryWrapper.eq("title", vo.getTitle());
        }
        if (null != vo.getTop()){
            queryWrapper.eq("top", vo.getTop());
        }
        if (null != vo.getTags() && !vo.getTags().isEmpty()){
            queryWrapper.eq("tags", vo.getTags());
        }
        if (null != vo.getStatus()){
            queryWrapper.eq("status", vo.getStatus());
        }
        if (null != vo.getCategoryId()){
            queryWrapper.eq("category_id", vo.getCategoryId());
        }
        Page<Note> notePage = noteMapper.selectPage(new Page<>(vo.getPage(), vo.getSize()), queryWrapper);
        return notePage.convert(note -> {
            NoteDto noteDto = new NoteDto();
            BeanUtils.copyProperties(note, noteDto);
            return noteDto;
        });
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
        Map<String, Object> map = ThreadLocalUtil.get();
        Integer userId = (Integer) map.get("id");
        return ResponseData.success(noteMapper.selectOne(new QueryWrapper<Note>().like("title", title).eq("user_id", userId)));
    }
}
