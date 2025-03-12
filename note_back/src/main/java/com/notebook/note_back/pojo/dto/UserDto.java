package com.notebook.note_back.pojo.dto;

import lombok.Data;

@Data
public class UserDto {
    private Integer id;
    private String username;
    private String password;
    private String salt;
    private String email;
    private String phone;
    private Integer status;
}
