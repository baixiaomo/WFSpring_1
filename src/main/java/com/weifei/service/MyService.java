package com.weifei.service;

import com.weifei.anotation.WFService;

@WFService
public class MyService {

    public String hello(String msg) {
        return "hello " + msg;
    }
}
