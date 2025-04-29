package com.notebook.note_back.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.notebook.note_back.common.response.ResponseData;
import com.notebook.note_back.common.utils.ThreadLocalUtil;
import com.notebook.note_back.mapper.CategoryMapper;
import com.notebook.note_back.mapper.NoteMapper;
import com.notebook.note_back.mapper.UserMapper;
import com.notebook.note_back.pojo.dto.CategoryDto;
import com.notebook.note_back.pojo.entity.Category;
import com.notebook.note_back.pojo.entity.Note;
import com.notebook.note_back.pojo.entity.User;
import com.notebook.note_back.pojo.vo.CategoryVo;
import com.notebook.note_back.service.CategoryService;
import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryMapper categoryMapper;

    private final NoteMapper noteMapper;
    private final UserMapper userMapper;

    @Override
    public ResponseData save(CategoryVo vo) {
        Map<String, Object> map = ThreadLocalUtil.get();
        Integer userId = (Integer) map.get("id");
        Category category = new Category();
        BeanUtils.copyProperties(vo, category);
        category.setUserId(userId);
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
    public ResponseData page(CategoryVo vo) {
        Map<String, Object> map = ThreadLocalUtil.get();
        Integer userId = (Integer) map.get("id");
        // 分页查询笔记（核心分页逻辑）
        Page<Note> notePage = new Page<>(vo.getPage(), vo.getSize());
        User user = userMapper.selectById(userId);
        QueryWrapper<Note> noteWrapper = new QueryWrapper<>();
        if (user.getPermission() <= 2) {
            noteWrapper.eq("user_id", userId);
        }
        noteWrapper.isNull("delete_time");
        if (vo.getStatus() != null) {
            noteWrapper.eq("status", vo.getStatus());
        }
        if (vo.getTags() != null){
             noteWrapper.like("tags", vo.getTags());
        }
        noteMapper.selectPage(notePage, noteWrapper);
        if (notePage.getRecords().isEmpty()) {
            return ResponseData.success(notePage); // 直接返回空分页
        }
        // 获取分类ID列表（用于关联分类信息）
        List<Integer> categoryIds = notePage.getRecords().stream()
                .map(Note::getCategoryId)
                .distinct()
                .collect(Collectors.toList());

        // 批量查询分类信息
        QueryWrapper<Category> categoryWrapper = new QueryWrapper<>();
        categoryWrapper.in("id", categoryIds);
        List<Category> categories = categoryMapper.selectList(categoryWrapper);
        Map<Integer, Category> categoryMap = categories.stream()
                .collect(Collectors.toMap(Category::getId, c -> c));

        // 按分类ID分组笔记
        Map<Integer, List<Note>> notesByCategory = notePage.getRecords().stream()
                .collect(Collectors.groupingBy(Note::getCategoryId));

        // 组装DTO分页结果
        List<CategoryDto> dtoList = notesByCategory.entrySet().stream().map(entry -> {
            Category category = categoryMap.get(entry.getKey());
            if (category == null) {
                return null; // 后续需要过滤null值
            }
            CategoryDto dto = new CategoryDto();
            BeanUtils.copyProperties(category, dto);
            dto.setNotes(entry.getValue());
            return dto;
        }).filter(Objects::nonNull).collect(Collectors.toList());

        // 构造分页对象（总记录数为笔记总数）
        Page<CategoryDto> resultPage = new Page<>(
                notePage.getCurrent(),
                notePage.getSize(),
                notePage.getTotal()
        );
        resultPage.setRecords(dtoList);

        return ResponseData.success(resultPage);
    }

    @Override
    @Transactional
    public ResponseData delete(Integer id) {
        QueryWrapper<Note> noteWrapper = new QueryWrapper<>();
        noteWrapper.eq("category_id", id);
        noteMapper.delete(noteWrapper);
        return ResponseData.success(categoryMapper.deleteById(id));
    }

    @Override
    public ResponseData deleteIds(Integer[] ids) {
        QueryWrapper<Note> noteWrapper = new QueryWrapper<>();
        noteWrapper.in("category_id", ids);
        noteMapper.deleteByIds(Collections.singleton(noteWrapper));
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
