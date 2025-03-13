package com.notebook.note_back.controller;

import com.notebook.note_back.common.response.ResponseData;
import com.notebook.note_back.pojo.vo.NoteVo;
import com.notebook.note_back.service.NoteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@CrossOrigin
@RestController
@RequiredArgsConstructor
@RequestMapping("/note")
public class NoteController {
    private final NoteService noteService;

    @PostMapping("/save")
    public ResponseData save(@RequestBody NoteVo note) {
        return noteService.save(note);
    }
    @PostMapping("/update")
    public ResponseData update(@RequestBody NoteVo note) {
        return noteService.update(note);
    }
    @PostMapping("/page")
    public ResponseData page(NoteVo note) {
        return noteService.pageQuery(note);
    }
    @PostMapping("/delete/{id}")
    public ResponseData delete(@PathVariable Integer id) {
        return noteService.delete(id);
    }
    @PostMapping("/delete/ids")
    public ResponseData delete(@RequestBody List<Integer> ids) {
        return noteService.deleteIds(ids);
    }

    @PostMapping("/status/{id}")
    public ResponseData updateStatus(@PathVariable Integer id) {
        return noteService.updateStatus(id);
    }
    @PostMapping("/top/{id}")
    public ResponseData updateTop(@PathVariable Integer id) {
        return noteService.updateTop(id);
    }
    @GetMapping("/{id}")
    public ResponseData getById(@PathVariable Integer id) {
        return noteService.getById(id);
    }
    @GetMapping("/{tags}")
    public ResponseData getByTags(@PathVariable String tags) {
        return noteService.getByTags(tags);
    }

    @GetMapping("/search/{title}")
    public ResponseData search(@PathVariable String title) {
        return noteService.search(title);
    }
}
