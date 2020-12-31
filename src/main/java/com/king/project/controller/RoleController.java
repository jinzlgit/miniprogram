package com.king.project.controller;

import com.king.framework.web.domain.Result;
import com.king.project.entity.Role;
import com.king.project.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author 金振林
 * @version v1.0
 * @date 2020/12/25 13:45
 */
@RequestMapping("/role")
@RestController
public class RoleController {

    @Autowired
    private RoleService roleService;

    @PostMapping
    public Result insert() {
        Role role = new Role();
        role.setRoleName("123");
        role.setRoleKey("456");
        role.setStatus("0");
        role.setDelFlag("0");
        role.setDataScope("1");
        role.setRoleSort(8);
        boolean save = roleService.save(role);

        System.out.println(save);
        System.out.println(role.getRoleId());
        // Role byId = roleService.getById(7);
        return Result.ok().data(save);
    }

    @DeleteMapping("/{id}")
    public Result delete(@PathVariable int id) {
        boolean b = roleService.removeById(id);
        return Result.ok().data(b);
    }

    @GetMapping
    public Result get() {
        List<Role> list = roleService.list();
        return Result.ok().data(list);
    }

    @GetMapping("/{id}")
    public Result getOne(@PathVariable int id) {
        Role byId = roleService.getById(id);

        return Result.ok().data(byId);
    }

}
