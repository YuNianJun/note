package com.notebook.note_back.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.notebook.note_back.common.response.ResponseData;
import com.notebook.note_back.common.utils.JwtUtil;
import com.notebook.note_back.common.utils.SaltMD5Util;
import com.notebook.note_back.common.utils.ThreadLocalUtil;
import com.notebook.note_back.pojo.dto.NoteDto;
import com.notebook.note_back.pojo.dto.UserDto;
import com.notebook.note_back.pojo.entity.User;
import com.notebook.note_back.mapper.UserMapper;
import com.notebook.note_back.pojo.vo.UserVo;
import com.notebook.note_back.service.UserService;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.BeanUtils;


import org.springframework.stereotype.Service;

import org.springframework.util.StringUtils;


import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;

    public ResponseData register(User user) {
        // 检查用户名是否存在
        if (isExist(user.getUsername())) {
            return ResponseData.error("用户名已存在");
        }
        if (StringUtils.hasText(user.getUsername())) {
            String trimmedName = user.getUsername().trim();
            if (!trimmedName.equals(user.getUsername())) {
                return ResponseData.error("名称前后不能有空格");
            }
        }
        // 加密密码并保存
        user.setPassword(SaltMD5Util.generateSaltPassword(user.getPassword()));
        user.setStatus(1);
        return ResponseData.success(userMapper.insert(user));
    }


    public ResponseData login(UserVo vo) {
        String username = vo.getUsername();
        String password = vo.getPassword();
        if (StringUtils.isEmpty(username) || StringUtils.isEmpty(password)) {
            return ResponseData.error("账号和密码都不能为空！");
        }
        User loginUser = userMapper.selectOne(new QueryWrapper<User>().eq("username", username));

        if (loginUser != null) {
            String saltPassword = loginUser.getPassword();
            boolean passwordFlag = SaltMD5Util.verifySaltPassword(password, saltPassword);
            if (!passwordFlag) {
                return ResponseData.error("登录失败,账号或者密码错误！");
            }
            if (loginUser.getStatus().equals(0)) {
                return ResponseData.error("登录失败,该账号已被禁用,请联系管理员！");
            }
            Map<String, Object> claims = new HashMap<>();
            claims.put("id", loginUser.getId());
            claims.put("username", loginUser.getUsername());
            String token = JwtUtil.genToken(claims);

            return ResponseData.success(token);
        }
        return ResponseData.error("登录失败,账号或者密码错误！");
    }

    @Override
    public ResponseData pageQuery(UserVo userVo) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        if (userVo.getUsername() != null && !userVo.getUsername().isEmpty()) {
            queryWrapper.eq("username", userVo.getUsername());
        }

        Page<User> userPage = userMapper.selectPage(new Page<>(userVo.getPage(), userVo.getSize()), queryWrapper);
        return ResponseData.success(userPage.convert(user -> {
            UserDto userDto = new UserDto();
            BeanUtils.copyProperties(user, userDto);
            return userDto;
        }));
    }

    @Override
    public ResponseData save(UserVo vo) {
        if (isExist(vo.getUsername())) {
            return ResponseData.error("用户名已存在");
        }
        User user = new User();
        BeanUtils.copyProperties(vo, user);
        //设置账号的状态，默认正常状态 1表示正常 0表示锁定
        user.setStatus(1);
        //设置密码 默认密码123456
        user.setPassword(SaltMD5Util.generateSaltPassword(user.getPassword()));
        return ResponseData.success(userMapper.insert(user));
    }

    @Override
    public ResponseData updateStatus(Integer status) {
        Map<String, Object> map = ThreadLocalUtil.get();
        Integer id = (Integer) map.get("id");
        User user = User.builder()
                .id(id)
                .status(status)
                .build();
        return ResponseData.success(userMapper.updateById(user));
    }

    @Override
    public ResponseData queryByName() {
        //从ThreadLocalUtil中获取用户名
        Map<String, Object> map = ThreadLocalUtil.get();
        String username = (String) map.get("username");
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("username", username);
        return ResponseData.success(userMapper.selectOne(wrapper));
    }

    public boolean isExist(String username) {
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("username", username);
        return userMapper.exists(wrapper);
    }

    @Override
    public ResponseData update(UserVo vo) {
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("username", vo.getUsername());
        boolean exists = userMapper.selectCount(wrapper) > 1;
        if (exists) {
            return ResponseData.error("名称已存在");
        }
        User user = new User();
        BeanUtils.copyProperties(vo, user);
        int updateResult = userMapper.updateById(user);
        if (updateResult > 0) {
            return ResponseData.success("更新成功");
        } else {
            return ResponseData.error("更新失败");
        }
    }

    @Override
    public void updateAvatar(String avatarUrl) {

    }

    @Override
    public ResponseData getById(Integer id) {
        return ResponseData.success(userMapper.selectById(id));
    }

    @Override
    public ResponseData updatePwd(UserVo vo) {
        User user = userMapper.selectById(vo.getId());
        if (user == null) {
            return ResponseData.error("用户不存在");
        }
        // 验证旧密码是否正确
        String saltPassword = user.getPassword();
        boolean passwordFlag = SaltMD5Util.verifySaltPassword(vo.getOldPassword(), saltPassword);
        if (!passwordFlag) {
            return ResponseData.error("旧密码错误");
        }

        BeanUtils.copyProperties(vo, user);
        user.setPassword(SaltMD5Util.generateSaltPassword(user.getPassword()));
        int updateResult = userMapper.updateById(user);
        if (updateResult > 0) {
            return ResponseData.success("更新成功");
        } else {
            return ResponseData.error("更新失败");
        }
    }
}
