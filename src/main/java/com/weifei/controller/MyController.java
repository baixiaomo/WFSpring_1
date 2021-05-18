package com.weifei.controller;

import com.weifei.anotation.WFAutowried;
import com.weifei.anotation.WFController;
import com.weifei.anotation.WFRequestMapping;
import com.weifei.service.MyService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WFController
@WFRequestMapping("/wf")
public class MyController {

    @WFAutowried
    private MyService service;

    @WFRequestMapping("/index")
    public void index(HttpServletRequest request, HttpServletResponse response) throws IOException {

        response.getWriter().write("MY MVC");
    }
}
