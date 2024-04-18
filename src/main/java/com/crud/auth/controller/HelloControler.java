package com.crud.auth.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/rest")
public class HelloControler {

    @GetMapping("/ok")
    public String method(){
        return "Hello World!";
    }

}
