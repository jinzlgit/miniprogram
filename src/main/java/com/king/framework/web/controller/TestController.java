package com.king.framework.web.controller;

import com.king.framework.shiro.util.TokenUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.shiro.authz.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * @author 金振林
 * @version v1.0
 * @date 2020/12/14 13:49
 */
@Api(tags = "测试模块")
@RestController
public class TestController {

    @Autowired
    private RedisTemplate redisTemplate;

    @GetMapping("/test")
    public Object test() {
        long epochMilli = LocalDateTime.now().toInstant(ZoneOffset.of("+8")).toEpochMilli();
        redisTemplate.opsForValue().set("test", "123", 60, TimeUnit.SECONDS);
        // redisTemplate.expire("test", 60, TimeUnit.SECONDS);
        return "hello world";
    }

    @ApiOperation(value = "登录接口")
    @PostMapping("/login")
    public String login(@ApiParam("用户名") String username, @ApiParam("密码") String password) {
        username = "admin";
        password = "admin";
        if (!"admin".equals(username)) {
            return "用户名错误";
        }
        if (!"admin".equals(password)) {
            return "密码错误";
        }

        long epochMilli = LocalDateTime.now().toInstant(ZoneOffset.of("+8")).toEpochMilli();
        String token = TokenUtil.sign(username, epochMilli);
        // redisTemplate.opsForValue().set(username, epochMilli, TokenUtil.REFRESH_EXPIRE_TIME);
        return token;
    }

    @PostMapping("/user")
    @RequiresAuthentication
    @RequiresRoles(logical = Logical.OR,value = {"user"})
    public String user(){
        return "成功访问user接口！";
    }

    @PostMapping("/admin")
    @RequiresPermissions("admin")
    @RequiresRoles(logical = Logical.OR,value = {"admin"})
    public String admin() {
        return "成功访问admin接口！";
    }

    @PostMapping("/youke")
    public String youke() {
        return "游客访问成功";
    }

    @RequestMapping(value = "/notLogin", method = RequestMethod.GET)
    public String notLogin() {
        return "您尚未登陆！";
    }

    @RequestMapping(value = "/notRole", method = RequestMethod.GET)
    public String notRole() {
        return "您没有权限！";
    }

    @RequestMapping(value = "/unauthorized/{msg}")
    @ResponseStatus(HttpStatus.OK)
    public String unauthorized(@PathVariable String msg) {
        return Optional.ofNullable(msg).orElse("TOKEN有问题");
    }

}
