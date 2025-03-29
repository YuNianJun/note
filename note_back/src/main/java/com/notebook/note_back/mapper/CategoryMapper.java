package com.notebook.note_back.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.notebook.note_back.pojo.entity.Category;
import com.notebook.note_back.pojo.vo.CategoryVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CategoryMapper extends BaseMapper<Category> {

}
