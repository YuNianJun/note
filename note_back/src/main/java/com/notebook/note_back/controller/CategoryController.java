package com.notebook.note_back.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.notebook.note_back.common.response.ResponseData;
import com.notebook.note_back.pojo.dto.CategoryDto;
import com.notebook.note_back.pojo.vo.CategoryVo;
import com.notebook.note_back.service.CategoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@CrossOrigin
@RestController
@RequiredArgsConstructor
@RequestMapping("/category")
public class CategoryController {
    private final CategoryService categoryService;
    @PostMapping("/save")
    public ResponseData save(@RequestBody CategoryVo vo) {
        log.info("新增分类：{}",vo);
        return categoryService.save(vo);
    }
    @PostMapping("/update")
    public ResponseData update(@RequestBody CategoryVo vo) {
        log.info("更新分类：{}",vo);
        return categoryService.update(vo);
    }
    @PostMapping("/page")
    public ResponseData page(@RequestBody CategoryVo vo) {
        log.info("分页查询分类：{}",vo);
        return categoryService.page(vo);
    }
    @PostMapping("/delete/{id}")
    public ResponseData delete(@PathVariable Integer id) {
        log.info("根据id删除分类：{}",id);
        return categoryService.delete(id);
    }
    @PostMapping("/delete/ids")
    public ResponseData delete(@RequestBody Integer[] ids) {
        log.info("根据id批量删除分类：{}",ids);
        return categoryService.deleteIds(ids);
    }
    @GetMapping("/{id}")
    public ResponseData getById(@PathVariable Integer id) {
        log.info("根据id查询分类：{}",id);
        return categoryService.getById(id);
    }
    @GetMapping("/list")
    public ResponseData getList() {
        log.info("查询所有分类");
        return categoryService.getList();
    }
}
