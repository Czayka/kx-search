package com.example.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HttpResult {

    // 响应的状态码
    private int code;

    // 响应的响应体
    private String body;
}
