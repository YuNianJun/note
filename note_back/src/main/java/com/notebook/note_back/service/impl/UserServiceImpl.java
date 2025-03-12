package com.notebook.note_back.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.notebook.note_back.common.result.Result;
import com.notebook.note_back.pojo.entity.User;
import com.notebook.note_back.mapper.UserMapper;
import com.notebook.note_back.pojo.vo.UserVo;
import com.notebook.note_back.service.UserService;
import lombok.RequiredArgsConstructor;
import org.apache.shiro.crypto.SecureRandomNumberGenerator;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.springframework.beans.BeanUtils;


import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.springframework.web.util.HtmlUtils;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;

    public void register(User user) {
        // 检查用户名是否存在
        String username = user.getUsername();
        username = HtmlUtils.htmlEscape(username);
        user.setUsername(username);
        if (isExist(user.getUsername())){
            Result.error("用户名已存在");
            return;
        }
        // 加密密码并保存
        String rawPassword = user.getPassword();
        String salt = new SecureRandomNumberGenerator().nextBytes().toString();
        int hashIterations = 2;
        String algorithmName = "md5";
        String encodedPassword = new SimpleHash(algorithmName, rawPassword, salt, hashIterations).toString();
        user.setSalt(salt);
        user.setPassword(encodedPassword);
        Result.success(userMapper.insert(user));
    }


    public Result<Object> login(UserVo vo) {
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("username", vo.getUsername());
        User user = userMapper.selectOne(wrapper);
        if (user == null) {
            //账号不存在
            return Result.error("账号不存在");
        }
        // 密码比对
        String rawPassword = vo.getPassword();
        if (!rawPassword.equals(user.getPassword())) {
            return Result.error("密码错误");
        }
        if (user.getStatus() == 0) {
            return Result.error("抱歉，您的账号已被锁定，请联系管理员");
        }
        return Result.success("登录成功");
    }

    @Override
    public Page<UserVo> pageQuery(UserVo user) {
        return null;
    }

    @Override
    public Result<Integer> save(UserVo vo) {
        User user = new User();
        BeanUtils.copyProperties(vo, user);
        //设置账号的状态，默认正常状态 1表示正常 0表示锁定
        user.setStatus(1);
        //设置密码 默认密码123456
        user.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));
        return Result.success(userMapper.insert(user));
    }

    @Override
    public Result<Integer> updateStatus(Integer status, Integer id) {
        User user = User.builder()
                .id(id)
                .status(status)
                .build();
        return Result.success(userMapper.updateById(user));
    }

    @Override
    public boolean isExist(String username) {
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("username", username);
        return userMapper.exists(wrapper);
    }

    @Override
    public User getById(Integer id) {
        User user = userMapper.selectById(id);
        user.setPassword("******");
        return user;
    }

    @Override
    public Result<Integer> update(UserVo vo) {
        User user = new User();
        BeanUtils.copyProperties(vo,user);
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("id", vo.getId());
        return Result.success(userMapper.update(user, wrapper));
    }


}
