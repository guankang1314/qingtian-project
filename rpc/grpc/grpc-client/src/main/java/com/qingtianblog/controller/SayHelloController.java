package com.qingtianblog.controller;

import com.qingtianblog.service.GrpcClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SayHelloController {

    @Autowired
    private GrpcClientService service;

    @GetMapping("/say-hello")
    public String sayHello(@RequestParam("name") String name){
        return service.sendMessage(name);
    }

}
