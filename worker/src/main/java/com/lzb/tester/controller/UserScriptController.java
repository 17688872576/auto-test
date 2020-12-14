package com.lzb.tester.controller;

import com.lzb.tester.service.UserScriptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user-script")
public class UserScriptController {

    @Autowired
    private UserScriptService service;


}
