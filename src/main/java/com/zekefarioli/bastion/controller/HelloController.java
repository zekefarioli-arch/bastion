package com.zekefarioli.bastion;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
public class HelloController {
    @GetMapping("/")
    public String getHello() {
        return "Hello";
    }
    
}
