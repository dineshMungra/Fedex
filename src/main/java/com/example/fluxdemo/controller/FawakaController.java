package com.example.fluxdemo.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FawakaController {

    @GetMapping("/fawaka")
    public String getFawaka() {
        return "Fa Waka !";
    }
}
