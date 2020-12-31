package com.king.project.controller;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.file.FileNameUtil;
import cn.hutool.extra.ftp.Ftp;
import cn.hutool.extra.ftp.FtpConfig;
import cn.hutool.extra.ftp.FtpMode;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.http.HttpUtil;
import com.king.common.utils.FileUtils;
import com.king.common.utils.poi.ExcelUtil;
import com.king.framework.web.domain.Result;
import com.king.project.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author 金振林
 * @version v1.0
 * @date 2020/12/23 10:47
 */
@RestController
public class Test1Controller {

    @Autowired
    private RestTemplate restTemplate;

    @GetMapping("/excel")
    public Result excel() {
        List<User> users = new ArrayList<>();
        User user = new User();
        user.setUserId(1L);
        user.setPassword("123");
        user.setSex("0");
        user.setAge(15);
        users.add(user);
        ExcelUtil<User> excelUtil = new ExcelUtil<User>(User.class);
        return excelUtil.exportExcel(users, "用户数据");
    }

    @GetMapping("/hello")
    public void hello(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.getRequestDispatcher("/hello2").forward(request, response);
    }

    @GetMapping("/hello2")
    public Result hello2() {
        return Result.ok().data("123");
    }

    @GetMapping("/redirect")
    public void redirect(HttpServletResponse response) throws IOException {
        response.sendRedirect("/hello2");
    }

    @GetMapping("/rest")
    public User rest() {
        User user = new User();
        user.setUserId(2L);
        user.setPassword("1224");
        user.setName("ooo");
        return user;
    }

    @GetMapping("/rest1")
    public String rest1() {
        String s = restTemplate.getForObject("http://127.0.0.1:9999/test", String.class);

        System.out.println(s);
        return s;
    }

    @GetMapping("/down")
    public void down(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setCharacterEncoding("utf-8");
        response.setContentType("multipart/form-data");
        response.setHeader("Content-Disposition",
                "attachment;fileName=" + FileUtils.setFileDownloadHeader(request, "首页.png"));
        long l = FileUtil.writeToStream(new File("D:\\JZL文档\\JYD公司项目\\神华\\登录\\bg.png"), response.getOutputStream());
        System.out.println(l);
    }

    public static void main(String[] args) {

    }

}
