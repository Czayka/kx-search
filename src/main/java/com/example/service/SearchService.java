package com.example.service;

import com.example.mode.dto.SearchParamDto;
import com.example.mode.dto.UserDto;

public interface SearchService {

    String login(UserDto userDto);

    String search(SearchParamDto searchParamDto) throws Exception;

    String search(String ciphertext) throws Exception;

}
