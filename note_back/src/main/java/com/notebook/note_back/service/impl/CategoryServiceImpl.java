package com.notebook.note_back.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.notebook.note_back.common.response.ResponseData;
import com.notebook.note_back.common.utils.ThreadLocalUtil;
import com.notebook.note_back.mapper.CategoryMapper;
import com.notebook.note_back.mapper.NoteMapper;
import com.notebook.note_back.pojo.dto.CategoryDto;
import com.notebook.note_back.pojo.entity.Category;
import com.notebook.note_back.pojo.entity.Note;
import com.notebook.note_back.pojo.vo.CategoryVo;
import com.notebook.note_back.service.CategoryService;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryMapper categoryMapper;

    private final NoteMapper noteMapper;
    @Override
    public ResponseData save(CategoryVo vo) {
        Category category = new Category();
        BeanUtils.copyProperties(vo, category);
        return ResponseData.success(categoryMapper.insert(category));
    }

    @Override
    public ResponseData update(CategoryVo vo) {
        Category category = new Category();
        BeanUtils.copyProperties(vo, category);
        QueryWrapper<Category> wrapper = new QueryWrapper<>();
        wrapper.eq("id", vo.getId());
        return ResponseData.success(categoryMapper.update(category, wrapper));
    }

    @Override
    public IPage<CategoryDto> page(CategoryVo vo) {
        Map<String, Object> map = ThreadLocalUtil.get();
        Integer userId = (Integer) map.get("id");
        // 1. 分页查询书架（核心分页逻辑）
        Page<Category> categoryPage = new Page<>(vo.getPage(), vo.getSize());
        QueryWrapper<Category> wrapper = new QueryWrapper<>();
        // 构建查询条件
        if (vo.getId() != null) {
            wrapper.eq("id", vo.getId());
        }
        if (vo.getName() != null) {
            wrapper.like("name", vo.getName());
        }
        wrapper.eq("user_id", userId);
        categoryMapper.selectPage(categoryPage, wrapper);
        // 2. 无数据直接返回空分页
        if (categoryPage.getRecords().isEmpty()) {
            return new Page<>(vo.getPage(), vo.getSize(), 0);
        }
        // 3. 批量查询关联笔记
        List<Integer> categoryIds = categoryPage.getRecords().stream()
                .map(Category::getId)
                .collect(Collectors.toList());
        QueryWrapper<Note> noteWrapper = new QueryWrapper<>();
        noteWrapper.in("category_id", categoryIds);
        noteWrapper.eq("user_id", userId);
        if (vo.getStatus() != null) {  // 添加对 status 字段的过滤条件
            noteWrapper.eq("status", vo.getStatus());
        }
        List<Note> notes = noteMapper.selectList(noteWrapper);
        // 按 categoryId 分组笔记
        Map<Integer, List<Note>> notesMap = notes.stream()
                .collect(Collectors.groupingBy(Note::getCategoryId));
        // 4. 组装 DTO 分页结果
        List<CategoryDto> dtoList = categoryPage.getRecords().stream().map(c -> {
            CategoryDto dto = new CategoryDto();
            BeanUtils.copyProperties(c, dto);
            dto.setNotes(notesMap.getOrDefault(c.getId(), Collections.emptyList()));
            return dto;
        }).collect(Collectors.toList());
        Page<CategoryDto> resultPage = new Page<>();
        BeanUtils.copyProperties(categoryPage, resultPage);
        resultPage.setRecords(dtoList);
        return resultPage;
    }

    @Override
    public ResponseData delete(Integer id) {
        return ResponseData.success(categoryMapper.deleteById(id));
    }

    @Override
    public ResponseData deleteIds(Integer[] ids) {
        return ResponseData.success(categoryMapper.deleteBatchIds(Collections.singleton(ids)));
    }

    @Override
    public ResponseData getById(Integer id) {
        return ResponseData.success(categoryMapper.selectById(id));
    }

    @Override
    public ResponseData getList() {
        Map<String, Object> map = ThreadLocalUtil.get();
        Integer userId = (Integer) map.get("id");
        QueryWrapper<Category> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId);
        return ResponseData.success(categoryMapper.selectList(wrapper));
    }
}
