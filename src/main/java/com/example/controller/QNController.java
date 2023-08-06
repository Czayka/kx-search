package com.example.controller;

import com.example.service.QNService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class QNController {

    @Autowired
    private QNService qnService;

    @GetMapping("/login")
    public Object login() throws Exception {
        qnService.login();
        return null;
    }

    @GetMapping("/getCar")
    public Object getCard() throws Exception {
        qnService.getCard();
        return null;
    }
}
