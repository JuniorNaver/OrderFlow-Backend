package com.youthcase.orderflow.auth.controller;

import com.youthcase.orderflow.auth.domain.Authority;
import com.youthcase.orderflow.auth.service.AuthorityService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/authorities")
public class AuthorityController {

    private final AuthorityService authorityService;

    public AuthorityController(AuthorityService authorityService) {
        this.authorityService = authorityService;
    }

    @GetMapping
    public List<Authority> getAll() {
        return authorityService.findAll();
    }

    @PostMapping
    public Authority create(@RequestBody Authority authority) {
        return authorityService.save(authority);
    }
}
