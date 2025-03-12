package com.notebook.note_back.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.notebook.note_back.pojo.entity.Note;
import org.apache.ibatis.annotations.Update;

public interface NoteMapper extends BaseMapper<Note> {
    @Update("UPDATE note SET status = #{status} WHERE id = #{id}")
    int updateStatusById(Integer id, Integer status);

}
