package com.notebook.note_back.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.notebook.note_back.pojo.entity.Note;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface NoteMapper extends BaseMapper<Note> {
    @Update("UPDATE note SET status = 1 - status WHERE id = #{id}")
    int updateStatusById(Integer id);

    @Update("UPDATE note SET top = 1 - top WHERE id = #{id}")
    int updateTopById(Integer id);

    @Select("SELECT title FROM note WHERE tags LIKE CONCAT('%', #{tag}, '%')")
    List<String> selectTitlesByTag(@Param("tag") String tag);

}
