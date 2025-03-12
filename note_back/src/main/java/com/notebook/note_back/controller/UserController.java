package com.notebook.note_back.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.notebook.note_back.common.result.Result;
import com.notebook.note_back.pojo.entity.User;
import com.notebook.note_back.pojo.vo.UserVo;
import com.notebook.note_back.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@CrossOrigin
@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    /**
     * 注册
     * */
    @PostMapping("/register")
    public Result<User> register(@RequestBody User user) {
        log.info("用户注册：{}", user);
        userService.register(user);
        return Result.success();
    }

    /**
     * 登录
     * */
    @PostMapping("/login")
    public Result<Object> login(@RequestBody UserVo user) {
        log.info("用户登录：{}", user);
        //TODO JWT校验
        return userService.login(user);
    }

    /**
     * 退出
     */
    @PostMapping("/logout")
    public Result<String> logout() {
        return Result.success();
    }
    /**
     * 新增用户
     * */
    @PostMapping("/save")
    public Result<Integer> save(@RequestBody UserVo user) {
        log.info("新增用户：{}",user);
        return userService.save(user);
    }

    /**
     * 分页查询
     * */
    @PostMapping("/page")
    public  Page<UserVo> page(UserVo user){
        log.info("分页查询用户：{}",user);
        return userService.pageQuery(user);
    }
    /**
     * 更改用户状态
     */
    @PostMapping("/status/{status}")
    public Result<Integer> updateStatus(@PathVariable Integer status ,Integer id){
        log.info("启用禁用用户账号：status={},id={}",status,id);
        return userService.updateStatus(status,id);
    }
    /**
     * 根据id查询用户信息
     * */
    @GetMapping("/{id}")
    public Result<User> getById(@PathVariable Integer id){
        log.info("根据id查询用户信息：{}",id);
        User user = userService.getById(id);
        return Result.success(user);
    }
    /**
     * 更新用户信息
     * */
    @PostMapping("/update")
    public Result<Integer> update(@RequestBody UserVo user){
        log.info("更新用户信息：{}",user);
        return userService.update(user);
    }

    @GetMapping("/exist/{username}")
    public Result<Boolean> isExist(@PathVariable String username){
        log.info("查询用户名是否存在：{}",username);
        return Result.success(userService.isExist(username));
    }

}
