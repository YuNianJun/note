package com.notebook.note_back.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.notebook.note_back.common.result.Result;
import com.notebook.note_back.pojo.entity.User;
import com.notebook.note_back.pojo.vo.UserVo;

public interface UserService {
    void register(User user);
    Result<Object> login(UserVo user);

    Page<UserVo> pageQuery(UserVo user);

    Result<Integer> save(UserVo user);

    User getById(Integer id);

    Result<Integer> update(UserVo user);

    Result<Integer> updateStatus(Integer status, Integer id);

    boolean isExist(String username);
}
