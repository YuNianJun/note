package com.notebook.note_back.pojo.dto;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.time.LocalDateTime;

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
    private Integer permission;

    @TableField(value = "create_time",fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
