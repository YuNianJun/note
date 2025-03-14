package com.notebook.note_back.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.notebook.note_back.common.response.ResponseData;
import com.notebook.note_back.pojo.dto.CategoryDto;
import com.notebook.note_back.pojo.vo.CategoryVo;

public interface CategoryService {
    ResponseData save(CategoryVo vo);

    ResponseData update(CategoryVo vo);

    IPage<CategoryDto> page(CategoryVo vo);

    ResponseData delete(Integer id);

    ResponseData deleteIds(Integer[] ids);

    ResponseData getById(Integer id);

    ResponseData getList();
}
