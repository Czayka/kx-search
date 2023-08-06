package com.example.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.config.HeaderMapRequestWrapper;
import com.example.mapper.CdkMapper;
import com.example.mapper.UserMapper;
import com.example.mode.dto.QNUserDto;
import com.example.mode.pojo.Cdk;
import com.example.mode.pojo.User;
import com.example.service.QNService;
import com.example.utils.AESUtil;
import com.example.utils.HttpAPIUtil;
import com.example.vo.HttpResult;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

@Service
@Slf4j
public class QNServiceImpl implements QNService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private CdkMapper cdkMapper;

    @Autowired
    private HttpAPIUtil httpAPIUtil;

    @Autowired
    private HttpServletRequest httpServletRequest;

    private final String KEY = "TAt3cxVCB2wV51aH";

    @Override
    public void login() throws Exception {
        List<User> users = userMapper.selectList(null);
        users.forEach(user -> {
            String username = user.getUsername();
            String password = user.getPassword();
            String openid = user.getOpenid();
            try {
                String token = logins(username, password);
                rewardedVideo(token,openid);
                getCdk(token,100,user.getId());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public void getCard() throws Exception {
        // 1.注册
        String userName = getUserName();
        String passWord = getPassWord();
        String openId = getOpenId();
        log.info("账号：{}，密码：{}，openid：{}",userName,passWord,openId);
        User user = register(userName, passWord, openId);

        log.info("userId:{}",user.getId());
        // 2.登录
        String token = logins(userName, passWord);

        // 3.观看影片
        rewardedVideo(token,openId);

        // 4.转cdk
        //查询用户信息
        String userInfo = getUserInfo(token);
        JSONObject jsonObject = JSONObject.parseObject(userInfo).getJSONObject("data");
        String queryCount = jsonObject.getString("queryCount");
        int count = Integer.parseInt(queryCount);
        if (count >= 100){
            getCdk(token,100,user.getId());
        }


    }

    public void getCdk(String token,int count,int userId){
        String url = "https://bzsg.qqsgtk.cn/account/cdk/transfer";
        Map<String , String> header = new HashMap<>();
        header.put("Referer","https://servicewechat.com/wxbeade8e701e701e0dd01/85/page-frame.html");
        header.put("Authorization","Bearer "+token);
        Map<String , String> map = new HashMap<>();
        map.put("cdkType","3");
        map.put("ckdCount",String.valueOf(count));
        HttpResult httpResult = httpAPIUtil.doPost(url,header, JSON.toJSONString(map));
        String body = httpResult.getBody();
//        log.info(body);
        JSONObject jsonObject = JSONObject.parseObject(body);
        if ("0".equals(jsonObject.getString("code"))){
            Cdk cdk = new Cdk();
            cdk.setCdk(jsonObject.getString("data"));
            cdk.setCount(count);
            cdk.setUserId(userId);
            cdkMapper.insert(cdk);
        }
    }

    public void rewardedVideo(String token,String openId) throws InterruptedException {
        String userInfo = getUserInfo(token);
        if (!"1".equals(JSONObject.parseObject(userInfo).getString("adFlag"))){
            return;
        }
        int rewardedVideo = getRewardedVideo(token, openId, "0");
        while (rewardedVideo > 0){
            rewardedVideo = getRewardedVideo(token, openId, "1");
            Thread.sleep(15000+new Random().nextInt(1000,10000));
        }

    }
    public int getRewardedVideo(String token,String openId,String state){
        String url = "https://bzsg.qqsgtk.cn/account/applet/rewardedVideo";
        Map<String , String> header = new HashMap<>();
        header.put("Referer","https://servicewechat.com/wxbeade8e701e701e0dd01/85/page-frame.html");
        header.put("Authorization","Bearer "+token);
        Map<String , String> map = new HashMap<>();
        map.put("state",state);
        map.put("openid",openId);
        HttpResult httpResult = httpAPIUtil.doPost(url,header, JSON.toJSONString(map));
        String body = httpResult.getBody();
//        log.info(body);
        return Integer.parseInt(JSONObject.parseObject(body).getString("totalCount"));
    }

    public String getUserInfo(String token){
        String url = "https://bzsg.qqsgtk.cn/account/member/getMemberInfo";
        Map<String , String> header = new HashMap<>();
        header.put("Referer","https://servicewechat.com/wxbeade8e701e701e0dd01/85/page-frame.html");
        header.put("Authorization","Bearer "+token);
        Map<String , String> map = new HashMap<>();
        HttpResult httpResult = httpAPIUtil.doPost(url,header, JSON.toJSONString(map));
        log.info(httpResult.getBody());
        return httpResult.getBody();
    }

    public String logins(String userName, String passWord) throws Exception {
        String url = "https://bzsg.qqsgtk.cn/auth/oauth2/token?grant_type=password&scope=server";

        String encPassWord = AESUtil.encryptAESCFB(KEY, KEY, passWord);
//        log.info(encPassWord);
        Map<String , String> map = new HashMap<>();
        map.put("username",userName);
        map.put("password",encPassWord);

        HttpResult httpResult = httpAPIUtil.getContent(url,map);
//        log.info(httpResult.getBody());
        String token = JSONObject.parseObject(httpResult.getBody()).getString("access_token");
        return token;
    }

    public User register(String userName, String passWord, String openId){
        String url = "https://bzsg.qqsgtk.cn/admin/register/user";

        HeaderMapRequestWrapper requestWrapper = new HeaderMapRequestWrapper(httpServletRequest);
        requestWrapper.addHeader("Referer","https://servicewechat.com/wxbeade8e701e701e0dd01/85/page-frame.html");

        requestWrapper.addHeader("Host","bzsg.qqsgtk.cn");
        QNUserDto qnUserDto = new QNUserDto();
        qnUserDto.setUsername(userName);
        qnUserDto.setRepassword(passWord);
        qnUserDto.setOpenid(openId);
        qnUserDto.setPassword(passWord);
        qnUserDto.setInviteCode(2765);
        HttpResult httpResult = httpAPIUtil.doPost(url, JSON.toJSONString(qnUserDto));
        String body = httpResult.getBody();
//        log.info(body);
        JSONObject jsonObject = JSON.parseObject(body);
        if ("1".equals(jsonObject.getString("code"))){
            throw new RuntimeException("注册失败");
        }
        //插入用户数据
        User user = new User();
        BeanUtils.copyProperties(qnUserDto,user);
        userMapper.insert(user);
        return user;
    }

    public String getUserName(){
        String characters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random nameLengthRandom = new Random();
        int nameLength = nameLengthRandom.nextInt(6, 13);
        StringBuilder sb = new StringBuilder(nameLength);
        Random random = new Random();
        for (int i = 0; i < nameLength; i++) {
            sb.append(characters.charAt(random.nextInt(characters.length())));
        }
        return sb.toString();
    }

    public String getPassWord(){
        String characters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random passWordLengthRandom = new Random();
        int passWordLength = passWordLengthRandom.nextInt(8, 14);
        StringBuilder sb = new StringBuilder(passWordLength);
        Random random = new Random();
        for (int i = 0; i < passWordLength; i++) {
            sb.append(characters.charAt(random.nextInt(characters.length())));
        }
//        sb.append(".");
        return sb.toString();
    }

    public String getOpenId(){
        String characters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder sb = new StringBuilder(28);
        Random random = new Random();
        for (int i = 0; i < 28; i++) {
            sb.append(characters.charAt(random.nextInt(characters.length())));
        }
        return sb.toString();
    }
}
