package com.youthcase.orderflow.auth.controller;

import com.youthcase.orderflow.auth.domain.Role;
import com.youthcase.orderflow.auth.service.RoleService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/roles")
public class RoleController {

    private final RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @GetMapping
    public List<Role> getAll() {
        return roleService.findAll();
    }

    @PostMapping
    public Role create(@RequestBody Role role) {
        return roleService.save(role);
    }
}
