package com.weifei.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Controller
@RequestMapping("/hello")
public class HelloController {
    @RequestMapping({"/",""})
    public ModelAndView index() {

        return new ModelAndView("/hello");
    }
    @RequestMapping("/index")
    public void index2(HttpServletRequest request, HttpServletResponse response) throws IOException {

        response.getWriter().write("hello yayaya");
    }
}
