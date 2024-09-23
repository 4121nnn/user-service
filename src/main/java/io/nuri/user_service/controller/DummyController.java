package io.nuri.user_service.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/dummy")
public class DummyController {

    @GetMapping("/public")
    public String publicApi(){
        return "It is public api";
    }

    @GetMapping("/secured")
    public String securedApi(){
        return "It is SECURED api";
    }

    @GetMapping("/admin")
    public String adminApi(){
        return "It is ADMIN api";
    }
}
