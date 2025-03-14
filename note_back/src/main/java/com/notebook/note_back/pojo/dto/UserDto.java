package com.notebook.note_back.pojo.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

@Data
public class UserDto {
    private Integer page = 1;
    private Integer size = 10;
    private Integer id;
    private String username;
    @JsonIgnore
    private String password;

    private String email;
    private String phone;
    private Integer status;
}
