package com.notebook.note_back.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.notebook.note_back.common.response.ResponseData;
import com.notebook.note_back.pojo.dto.NoteDto;
import com.notebook.note_back.pojo.vo.NoteVo;
import com.notebook.note_back.service.NoteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Slf4j
@CrossOrigin
@RestController
@RequiredArgsConstructor
@RequestMapping("/note")
public class NoteController {
    private final NoteService noteService;

    @PostMapping("/save")
    public ResponseData save(@RequestBody NoteVo note) {
        log.info("新增笔记：{}",note);
        return noteService.save(note);
    }
    @PostMapping("/update")
    public ResponseData update(@RequestBody NoteVo note) {
        log.info("更新笔记：{}",note);
        return noteService.update(note);
    }

    @PostMapping("/page")
    public IPage<NoteDto> page(NoteVo note) {
        log.info("分页查询笔记：{}",note);
        return noteService.pageQuery(note);
    }

    @PostMapping("/delete/{id}")
    public ResponseData delete(@PathVariable Integer id) {
        log.info("根据id删除单个笔记：{}",id);
        return noteService.delete(id);
    }

    @PostMapping("/delete/ids")
    public ResponseData delete(@RequestBody List<Integer> ids) {
        log.info("根据id批量删除笔记：{}",ids);
        return noteService.deleteIds(ids);
    }

    @PostMapping("/status/{id}")
    public ResponseData updateStatus(@PathVariable Integer id) {
        log.info("是否公开笔记：id={}",id);
        return noteService.updateStatus(id);
    }

    @PostMapping("/top/{id}")
    public ResponseData updateTop(@PathVariable Integer id) {
        log.info("是否置顶笔记：id={}",id);
        return noteService.updateTop(id);
    }

    @GetMapping("/{id}")
    public ResponseData getById(@PathVariable Integer id) {
        log.info("根据id查询笔记：{}",id);
        return noteService.getById(id);
    }

    @GetMapping("/{tags}")
    public ResponseData getByTags(@PathVariable String tags) {
        log.info("根据标签查询笔记：{}",tags);
        return noteService.getByTags(tags);
    }

    @GetMapping("/search/{title}")
    public ResponseData search(@PathVariable String title) {
        log.info("根据标题查询笔记：{}",title);
        return noteService.search(title);
    }

    /**
     * 文件上传
     * */
    @PostMapping("/upload")
    public ResponseData upload(MultipartFile file) throws IOException {
        //把文件内容存储到本地磁盘中
        String Filename = file.getOriginalFilename(); //自动获取文件名
        //保证文件名唯一 防止被覆盖
        String filename = UUID.randomUUID() + (Filename != null ? Filename.substring(Filename.lastIndexOf(".")) : null);
        //文件传输
        file.transferTo( new File("E:\\"+filename));
        //返回
        return ResponseData.success("url访问地址……");
    }
}
