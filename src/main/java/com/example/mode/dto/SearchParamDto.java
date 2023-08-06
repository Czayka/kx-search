package com.example.mode.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SearchParamDto {
    private String region_name;
    private String order_type;
    private Integer size;
    private String keyword;
    private Double lowest_price;
    private Double highest_price;
    private String[] attributes;
    private String[] no_attributes;
    private String search_type;
}
