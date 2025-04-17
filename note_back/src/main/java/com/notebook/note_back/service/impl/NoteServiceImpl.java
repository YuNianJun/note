package com.notebook.note_back.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.notebook.note_back.common.response.ResponseData;
import com.notebook.note_back.common.utils.ThreadLocalUtil;
import com.notebook.note_back.mapper.CommentMapper;
import com.notebook.note_back.mapper.NoteMapper;
import com.notebook.note_back.mapper.NoteShareMapper;
import com.notebook.note_back.pojo.dto.NoteDto;
import com.notebook.note_back.pojo.entity.Comment;
import com.notebook.note_back.pojo.entity.Note;
import com.notebook.note_back.pojo.entity.NoteShare;
import com.notebook.note_back.pojo.vo.CommentVo;
import com.notebook.note_back.pojo.vo.NoteVo;
import com.notebook.note_back.service.NoteService;
import lombok.RequiredArgsConstructor;
import org.apache.ibatis.annotations.Update;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;


@Service
@RequiredArgsConstructor
public class NoteServiceImpl implements NoteService {

    private final NoteMapper noteMapper;
    private final CommentMapper commentMapper;
    private final NoteShareMapper noteShareMapper;

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
    public ResponseData deleteComment(List<Integer> ids) {

        // 删除评论
        return ResponseData.success(commentMapper.deleteByIds(ids));
    }

    @Override
    public ResponseData saveComment(CommentVo vo) {
        Map<String, Object> map = ThreadLocalUtil.get();
        Integer userId = (Integer) map.get("id");
        Comment comment = new Comment();
        comment.setNoteId(vo.getNoteId());
        comment.setUserId(userId);
        comment.setContent(vo.getContent());
        comment.setCreateTime(LocalDateTime.now());
        // 保存评论
        return ResponseData.success(commentMapper.insert(comment));
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
    public ResponseData viewSharedNote(NoteShare vo) {
        // 从数据库中查询 token 是否有效
        QueryWrapper<NoteShare> wrapper = new QueryWrapper<>();
        wrapper.eq("token", vo.getToken());
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
