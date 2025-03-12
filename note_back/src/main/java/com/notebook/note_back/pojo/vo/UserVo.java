package com.notebook.note_back.pojo.vo;

import lombok.Data;

@Data
public class UserVo {

    private Integer page;
    private Integer size;
    private Integer id;
    private String username;
    private String password;
    private String salt;
    private String email;
    private String phone;
    private Integer status;
}
