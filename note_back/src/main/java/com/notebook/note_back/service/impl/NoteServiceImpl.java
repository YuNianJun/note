package com.notebook.note_back.service.impl;

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
import org.springframework.beans.BeanUtils;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;


@Service
@RequiredArgsConstructor
public class NoteServiceImpl implements NoteService {

    private final NoteMapper noteMapper;
    private final CommentMapper commentMapper;
    private final NoteShareMapper noteShareMapper;
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
        Map<String, Object> map = ThreadLocalUtil.get();
        Integer userId = (Integer) map.get("id");
        QueryWrapper<Note> queryWrapper = new QueryWrapper<>();

        queryWrapper.eq("user_id", userId);
        queryWrapper.isNotNull("delete_time");
        if (null != vo.getTitle() && !vo.getTitle().isEmpty()) {
            queryWrapper.eq("title", vo.getTitle());
        }
        if (null != vo.getTop()){
            queryWrapper.eq("top", vo.getTop());
        }
        if (null != vo.getTags() && !vo.getTags().isEmpty()){
            queryWrapper.eq("tags", vo.getTags());
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
        QueryWrapper<Note> wrapper = new QueryWrapper<>();
        wrapper.in("id", vo.getIds());

        return ResponseData.success(noteMapper.update(note, wrapper));
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
        String shareLink = "https://yourdomain.com/note/share/" + vo.getId() + "?token=" + token;
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
