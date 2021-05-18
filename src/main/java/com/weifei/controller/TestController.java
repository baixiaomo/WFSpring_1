package com.weifei.controller;

import com.weifei.anotation.WFController;
import com.weifei.anotation.WFRequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WFController
@WFRequestMapping("/test")
public class TestController {

    @WFRequestMapping("/index")
    public void index2(HttpServletRequest request, HttpServletResponse response) throws IOException {

        response.getWriter().write("MY MVC");
    }
}
