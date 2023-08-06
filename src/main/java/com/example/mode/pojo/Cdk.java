package com.example.mode.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("t_cdk")
public class Cdk {
    @TableId(value = "id", type = IdType.AUTO)
    private int id;

    private int userId;
    private String cdk;
    private int count;
}
