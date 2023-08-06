package com.example.mode.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QNUserDto {

    private String username;
    private String password;
    private int inviteCode;
    private String repassword;
    private String openid;

}
