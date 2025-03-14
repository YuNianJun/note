package com.notebook.note_back.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.notebook.note_back.common.response.ResponseData;
import com.notebook.note_back.pojo.dto.UserDto;
import com.notebook.note_back.pojo.entity.User;
import com.notebook.note_back.pojo.vo.UserVo;

public interface UserService {
    ResponseData register(User user);

    ResponseData login(UserVo user);

    IPage<UserDto> pageQuery(UserVo user);

    ResponseData save(UserVo user);

    ResponseData getById(Integer id);

    ResponseData updatePwd(UserVo user);

    ResponseData updateStatus(Integer status, Integer id);

    ResponseData queryByName();

    ResponseData update(UserVo user);

    void updateAvatar(String avatarUrl);
}
