package com.notebook.note_back.controller;

import com.notebook.note_back.common.result.Result;
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
    public Result<Integer> save(@RequestBody NoteVo note) {
        return noteService.save(note);
    }
    @PostMapping("/update")
    public Result<Integer> update(@RequestBody NoteVo note) {
        return noteService.update(note);
    }
    @PostMapping("/page")
    public Result<Object> page(NoteVo note) {
        return noteService.pageQuery(note);
    }
    @PostMapping("/delete/{id}")
    public Result<Integer> delete(@PathVariable Integer id) {
        return noteService.delete(id);
    }
    @PostMapping("/delete/ids")
    public Result<Integer> delete(@RequestBody List<Integer> ids) {
        return noteService.deleteIds(ids);
    }

    @PostMapping("/status/{status}")
    public Result<Integer> updateStatus(@PathVariable Integer status, Integer id) {
        return noteService.updateStatus(status,id);
    }
    @PostMapping("/top/{id}")
    public Result<Integer> updateTop(@PathVariable Integer id) {
        return noteService.updateTop(id);
    }
    @GetMapping("/{id}")
    public Result<Object> getById(@PathVariable Integer id) {
        return noteService.getById(id);
    }
    @GetMapping("/{tags}")
    public Result<Object> getByTags(@PathVariable String tags) {
        return noteService.getByTags(tags);
    }

}
