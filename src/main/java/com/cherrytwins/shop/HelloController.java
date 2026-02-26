package com.cherrytwins.shop;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.ui.Model;

@Controller
public class HelloController {
    
    @GetMapping("/Welcome")
    public String hello(Model model){
        model.addAttribute("message", "Hello World from Thymeleaf!");
        return "hello";
    }
}
