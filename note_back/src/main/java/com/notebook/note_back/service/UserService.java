package com.notebook.note_back.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.notebook.note_back.common.response.ResponseData;
import com.notebook.note_back.pojo.entity.User;
import com.notebook.note_back.pojo.vo.UserVo;

public interface UserService {
    ResponseData register(User user);
    ResponseData login(UserVo user);

    Page<UserVo> pageQuery(UserVo user);

    ResponseData save(UserVo user);

    User getById(Integer id);

    ResponseData update(UserVo user);

    ResponseData updateStatus(Integer status, Integer id);

    boolean isExist(String username);
}
