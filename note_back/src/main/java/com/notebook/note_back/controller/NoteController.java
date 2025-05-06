package com.notebook.note_back.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;

import com.notebook.note_back.common.response.ResponseData;
import com.notebook.note_back.pojo.dto.NoteDto;

import com.notebook.note_back.pojo.entity.Note;
import com.notebook.note_back.pojo.entity.NoteShare;
import com.notebook.note_back.pojo.vo.CommentVo;
import com.notebook.note_back.pojo.vo.NoteVo;
import com.notebook.note_back.service.NoteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.io.File;

import java.util.UUID;

@Slf4j
@CrossOrigin
@RestController
@RequiredArgsConstructor
@RequestMapping("/note")
public class NoteController {
    private final NoteService noteService;

    /**
     * 新增笔记
     * */
    @PostMapping("/save")
    public ResponseData save(@RequestBody NoteVo note) {
        log.info("新增笔记：{}",note);
        return noteService.save(note);
    }

    /**
     * 更新笔记
     * */
    @PostMapping("/update")
    public ResponseData update(@RequestBody NoteVo note) {
        log.info("更新笔记：{}",note);
        return noteService.update(note);
    }
    /**
     * 分页查询公开笔记
     * */
    @PostMapping("/pageOpen")
    public ResponseData pageOpen(@RequestBody NoteVo note) {
        log.info("分页查询公开笔记：{}",note);
        return noteService.pageOpen(note);
    }

    /**
     * 分页查询回收站中的笔记
     * */
    @PostMapping("/page")
    public ResponseData page(@RequestBody NoteVo note) {
        log.info("分页查询回收站中的笔记：{}",note);
        return noteService.pageQuery(note);
    }

    /**
     * 笔记批量移入回收站
     * */
    @PostMapping("/putRecycleBin")
    public ResponseData putRecycleBin(@RequestBody NoteVo vo) {
        log.info("笔记批量移入回收站：{}",vo.getIds());
        return noteService.putRecycleBin(vo);
    }
    /**
     * 笔记移除回收站
     * */
    @PostMapping("/recover")
    public ResponseData removeRecycleBin(@RequestBody NoteVo vo) {
        log.info("笔记移除回收站：{}",vo.getIds());
        return noteService.removeRecycleBin(vo);
    }

    /**
     * 根据id删除单个笔记
     * */
    @PostMapping("/delete/{id}")
    public ResponseData delete(@PathVariable Integer id) {
        log.info("根据id删除单个笔记：{}",id);
        return noteService.delete(id);
    }

    /**
     * 根据id批量删除笔记
     * */
    @PostMapping("/delete/ids")
    public ResponseData delete(@RequestBody NoteVo vo) {
        log.info("根据id批量删除笔记：{}",vo.getIds());
        return noteService.deleteIds(vo.getIds());
    }

    /**
     * 是否公开笔记
     * */
    @PostMapping("/status/{id}")
    public ResponseData updateStatus(@PathVariable Integer id) {
        log.info("是否公开笔记：id={}",id);
        return noteService.updateStatus(id);
    }

    /**
     * 是否置顶笔记
     * */
    @PostMapping("/top/{id}")
    public ResponseData updateTop(@PathVariable Integer id) {
        log.info("是否置顶笔记：id={}",id);
        return noteService.updateTop(id);
    }

    /**
     * 根据id查询笔记
     * */
    @GetMapping("/{id}")
    public ResponseData getById(@PathVariable Integer id) {
        log.info("根据id查询笔记：{}",id);
        return noteService.getById(id);
    }

    /**
     * 根据标签查询笔记
     * */
    @GetMapping("/{tags}")
    public ResponseData getByTags(@PathVariable String tags) {
        log.info("根据标签查询笔记：{}",tags);
        return noteService.getByTags(tags);
    }

    /**
     * 根据标题查询笔记
     * */
    @GetMapping("/search/{title}")
    public ResponseData search(@PathVariable String title) {
        log.info("根据标题查询笔记：{}",title);
        return noteService.search(title);
    }

    /**
     * 文件上传
     * */
//    @PostMapping("/upload")
//    public ResponseData upload(MultipartFile file) throws IOException {
//        //把文件内容存储到本地磁盘中
//        String Filename = file.getOriginalFilename(); //自动获取文件名
//        //保证文件名唯一 防止被覆盖
//        String filename = UUID.randomUUID() + (Filename != null ? Filename.substring(Filename.lastIndexOf(".")) : null);
//        //文件传输
//        file.transferTo( new File("E:\\"+filename));
//        //返回
//        return ResponseData.success("url访问地址……");
//    }

    /**
     * 笔记分享
     * */
    @PostMapping("/shareNote")
    public ResponseData shareNote(@RequestBody NoteVo vo) {
        log.info("分享链接：{}",vo);
        return noteService.shareNote(vo);
    }

    /**
     * 访问分享链接
     * */
    @GetMapping("/viewSharedNote/{noteId}")
    public ResponseData viewSharedNote(@PathVariable Integer noteId, @RequestParam String token) {
        log.info("访问分享链接：{}",noteId);
        return noteService.viewSharedNote(noteId, token);
    }


    //直接取值返给前端就行
    @GetMapping("/getCoverImg")
    public ResponseData getCrowdInfoById() {
        log.info("获取封面图片");
        Note note = new Note();
        byte[] imageData = noteService.getCoverImg(note.getCoverImg()); // 假设getCoverImg返回的是图片的字节数组
        String base64Image = Base64.getEncoder().encodeToString(imageData); // 将字节数组转换为Base64编码
        return ResponseData.success(base64Image);
    }

    /**
     * 将用户上传的信息存入数据库中
     * 图片以MultipartFile格式上传
     * @return
     */
    @CrossOrigin(origins = {"*", "3600"})  //跨域注解，所有域名都可访问，且cookie的有效期为3600秒
    @RequestMapping(value = "/pushMessageParam", method = RequestMethod.POST)
    public int pushMessageBody(@RequestParam String id, MultipartFile file1, MultipartFile file2, MultipartFile file3) throws IOException{//若参数为map或json格式，必须写@RequestBody

        List<MultipartFile> files =new ArrayList<>();//保存用户上传的所有图片，最多三张。
        files.add(file1);
        files.add(file2);
        files.add(file3);

//****给上传的所有jpg、jpeg格式的图片添加头部header（这样取得时候不用解码，直接拿值就行），并进行转码。****

        try {

            List<String> base64EncoderImgs = new ArrayList<>();//存放转码后的图片
            String header = "";//为转码后的图片添加头部信息


            for (int i = 0; i < files.size(); i++) {//遍历所有文件
                if (files.get(i) != null) {
                    if (!files.get(i).getOriginalFilename().endsWith(".jpg") && !files.get(i).getOriginalFilename().endsWith(".jpeg")) {
                        System.out.println("文件格式非法！");
                    } else if ("jpg".equals(files.get(i).getOriginalFilename())) {//files.get(i).getOriginalFilename() 获取文件的扩展名.jpg .jpeg
                        header = "data:image/jpg;base64,";
                    } else if ("jpeg".equals(files.get(i).getOriginalFilename())) {
                        header = "data:image/jpeg;base64,";
                    }
                    base64EncoderImgs.add(header + Base64.getEncoder().encodeToString(files.get(i).getBytes()));              } else {
                    base64EncoderImgs.add(null);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


//        subMessageService.saveSubMessage(new SubMessage(id, base64EncoderImgs.get(0),
//                base64EncoderImgs.get(1), base64EncoderImgs.get(2));
        System.out.println("用户消息已存入数据库！");

        return 0;
    }

    @PostMapping("/upload")
    public String handleFileUpload(@RequestParam("file") MultipartFile file) {
        if (!file.isEmpty()) {
            try {
                // 保存文件，这里只是简单示例，实际应用中需要处理文件名冲突等问题
                byte[] bytes = file.getBytes();
                java.nio.file.Path path = Paths.get("E:\\" + file.getOriginalFilename());
                Files.write(path, bytes);
                return "You successfully uploaded " + file.getOriginalFilename() + "!";
            } catch (IOException e) {
                e.printStackTrace();
                return "Failed to upload " + file.getOriginalFilename() + "!";
            }
        } else {
            return "Please select a file to upload.";
        }
    }
}
