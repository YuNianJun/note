package com.notebook.note_back.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.notebook.note_back.common.response.ResponseData;
import com.notebook.note_back.common.utils.ThreadLocalUtil;
import com.notebook.note_back.mapper.CategoryMapper;
import com.notebook.note_back.mapper.NoteMapper;
import com.notebook.note_back.mapper.NoteShareMapper;
import com.notebook.note_back.pojo.dto.CategoryDto;
import com.notebook.note_back.pojo.dto.NoteDto;
import com.notebook.note_back.pojo.entity.Category;
import com.notebook.note_back.pojo.entity.Note;
import com.notebook.note_back.pojo.entity.NoteShare;
import com.notebook.note_back.pojo.vo.NoteVo;
import com.notebook.note_back.service.CategoryService;
import com.notebook.note_back.service.NoteService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class NoteServiceImpl implements NoteService {

    private final NoteMapper noteMapper;

    private final NoteShareMapper noteShareMapper;

    private final CategoryMapper categoryMapper;

    @Value("${note.share}")
    private String noteShareLink;
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
    public ResponseData pageQuery(NoteVo vo) {
        Map<String, Object> map = ThreadLocalUtil.get();
        Integer userId = (Integer) map.get("id");
        // 分页查询笔记（核心分页逻辑）
        Page<Note> notePage = new Page<>(vo.getPage(), vo.getSize());
        QueryWrapper<Note> noteWrapper = new QueryWrapper<>();
        noteWrapper.eq("user_id", userId);
        noteWrapper.isNotNull("delete_time"); // 查询进入回收站的笔记
        if (null != vo.getTitle() && !vo.getTitle().isEmpty()) {
            noteWrapper.eq("title", vo.getTitle());
        }
        if (null != vo.getTop()) {
            noteWrapper.eq("top", vo.getTop());
        }
        if (null != vo.getTags() && !vo.getTags().isEmpty()) {
            noteWrapper.eq("tags", vo.getTags());
        }
        if (null != vo.getCategoryId()) {
            noteWrapper.eq("category_id", vo.getCategoryId());
        }

        noteMapper.selectPage(notePage, noteWrapper);

        IPage<NoteDto> noteDtoPage = notePage.convert(note -> {
            NoteDto noteDto = new NoteDto();
            BeanUtils.copyProperties(note, noteDto);
            return noteDto;
        });

        // 将 IPage<NoteDto> 转换为一个包含分页信息的对象
        Map<String, Object> result = new HashMap<>();
        result.put("total", noteDtoPage.getTotal());
        result.put("pages", noteDtoPage.getPages());
        result.put("current", noteDtoPage.getCurrent());
        result.put("size", noteDtoPage.getSize());
        result.put("records", noteDtoPage.getRecords());

        return ResponseData.success(result);
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

    @Override
    public ResponseData putRecycleBin(NoteVo vo) {
        Note note = new Note();
        note.setDeleteTime(LocalDateTime.now());
        QueryWrapper<Note> wrapper = new QueryWrapper<>();
        wrapper.in("id", vo.getIds());

        return ResponseData.success(noteMapper.update(note, wrapper));
    }

    // 每天凌晨1点执行 删除7天前的笔记
    @Scheduled(cron = "0 0 1 * * ?")
    public void deleteOldNotes() {
        LocalDateTime sevenDaysAgo = LocalDateTime.now().minusDays(7);
        QueryWrapper<Note> wrapper = new QueryWrapper<>();
        wrapper.lt("delete_time", sevenDaysAgo);
        noteMapper.delete(wrapper);
    }
    @Override
    public ResponseData removeRecycleBin(NoteVo vo) {
        Note note = new Note();
        note.setDeleteTime(null);
        UpdateWrapper<Note> wrapper = new UpdateWrapper<>();
        wrapper.in("id", vo.getIds()).set("delete_time", null);

        return ResponseData.success(noteMapper.update(note, wrapper));
    }



    @Override
    public byte[] getCoverImg(String coverImg) {
        return null;
    }

    @Override
    public ResponseData pageOpen(NoteVo vo) {
        // 分页查询 公开 笔记（核心分页逻辑）
        Page<Note> notePage = new Page<>(vo.getPage(), vo.getSize());
        QueryWrapper<Note> noteWrapper = new QueryWrapper<>();
        noteWrapper.isNull("delete_time");
        noteWrapper.eq("status", 1);
        if (vo.getStatus() != null) {
            noteWrapper.eq("status", vo.getStatus());
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
            CategoryDto dto = new CategoryDto();
            BeanUtils.copyProperties(category, dto);
            dto.setNotes(entry.getValue());
            return dto;
        }).collect(Collectors.toList());

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
    public ResponseData shareNote(NoteVo vo) {
        // 生成随机 token
        String token = UUID.randomUUID().toString();
        // 将 token 和 noteId 存储到数据库
        NoteShare noteShare = new NoteShare();
        noteShare.setToken(token);
        noteShare.setNoteId(vo.getId());
        noteShare.setCreateTime(LocalDateTime.now());

        noteShareMapper.insert(noteShare);
        // 返回分享链接
        String shareLink = noteShareLink + vo.getId() + "?token=" + token;
        return ResponseData.success(shareLink);
    }

    @Override
    public ResponseData viewSharedNote(Integer noteId, String token) {
        // 从数据库中查询 token 是否有效
        QueryWrapper<NoteShare> wrapper = new QueryWrapper<>();
        wrapper.eq("token", token);
        NoteShare noteShare = noteShareMapper.selectOne(wrapper);
        // 验证 token 是否存在且未过期（7 天内有效）
        if (noteShare == null || noteShare.getCreateTime().isBefore(LocalDateTime.now().minusDays(7))) {
            return ResponseData.error("分享链接无效或已过期");
        }
        // 返回笔记内容
        return ResponseData.success(noteMapper.selectById(noteShare.getNoteId()));
    }


    // 每天凌晨1点执行 清理7天前的分享链接
    @Scheduled(cron = "0 0 1 * * ?")
    public void cleanExpiredShares() {
        LocalDateTime sevenDaysAgo = LocalDateTime.now().minusDays(7);
        QueryWrapper<NoteShare> wrapper = new QueryWrapper<>();
        wrapper.lt("create_time", sevenDaysAgo);
        noteShareMapper.delete(wrapper);
    }
}
