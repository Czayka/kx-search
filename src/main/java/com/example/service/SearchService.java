package com.example.service;

import com.example.dto.SearchParamDto;
import com.example.dto.UserDto;

public interface SearchService {

    String login(UserDto userDto);

    String search(SearchParamDto searchParamDto) throws Exception;

    String search(String ciphertext) throws Exception;

}
