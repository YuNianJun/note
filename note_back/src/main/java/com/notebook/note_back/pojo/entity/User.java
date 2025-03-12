package com.notebook.note_back.pojo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("user")
public class User {

    @TableId(value = "id",type = IdType.AUTO)
    private Integer id;
    private String username;
    private String password;
    // 密码盐
    private String salt;
    private String email;
    private String phone;
    private Integer status;
}
