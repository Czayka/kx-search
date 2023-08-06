package com.example.service.impl;

import com.alibaba.fastjson.JSON;
import com.example.mode.dto.SearchParamDto;
import com.example.mode.dto.UserDto;
import com.example.service.SearchService;
import com.example.utils.AESUtil;
import com.example.utils.HttpAPIUtil;
import com.example.vo.HttpResult;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Slf4j
@Service
public class SearchServiceImpl implements SearchService {



    @Autowired
    private HttpAPIUtil httpAPIUtil;

    @Autowired
    private HttpServletRequest httpServletRequest;

    @Override
    public String login(UserDto userDto) {

        if (null == userDto){
            return "账号为空";
        }

        userDto.set_confirmed(false);
        String jsonString = JSON.toJSONString(userDto);
        HttpResult httpResult = httpAPIUtil.doPost("http://kaixinchaxun.com/api/login", jsonString);



        return httpResult.getBody();
    }

    /**
     * 加密解密放后端
     * @param searchParamDto
     * @return
     * @throws Exception
     */
    @Override
    public String search(SearchParamDto searchParamDto) throws Exception {

        //获取token
        String token = httpServletRequest.getHeader("Authorization");
        if (token == null || token.length() < 16){
            return "token过期";
        }
        String jsonString = JSON.toJSONString(searchParamDto);
        String aesKey = token.substring(0, 16);
        //加密

        String encrypt = AESUtil.encryptAES(aesKey, aesKey, jsonString);
        log.info("加密后为：{}",encrypt);

        HttpResult httpResult = httpAPIUtil.doPost("http://kaixinchaxun.com/api/sanguo_search2", encrypt);

        String result = httpResult.getBody();
        log.info("result:{}",result);
        if (httpResult.getCode() != 200){
            return result;
        }
        //解密
        String decrypt = AESUtil.decryptAES(aesKey, aesKey, result);
        log.info("解密后明文为：{}",decrypt);
        return decrypt;
    }

    /**
     * 加密解密放前端
     * @param ciphertext
     * @return
     * @throws Exception
     */
    @Override
    public String search(String ciphertext) throws Exception {
        //获取token
        String token = httpServletRequest.getHeader("Authorization");
        if (token == null || token.length() < 16){
            return "token过期";
        }
        String aesKey = token.substring(0, 16);
        String decrypt = AESUtil.decryptAES(aesKey, aesKey, ciphertext);
        log.info("解密后明文为：{}",decrypt);

        HttpResult httpResult = httpAPIUtil.doPost("http://kaixinchaxun.com/api/sanguo_search2", ciphertext);

        String result = httpResult.getBody();
        log.info("返回值为：{}",result);

        return result;

    }

}
