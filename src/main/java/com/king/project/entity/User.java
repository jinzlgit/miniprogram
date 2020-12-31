package com.king.project.entity;

import com.king.framework.aspect.annotation.Excel;

/**
 * @author 金振林
 * @version v1.0
 * @date 2020/12/23 10:49
 */
public class User {

    @Excel(name = "主键ID")
    private Long userId;

    @Excel(name = "用户名", prompt = "用户编号")
    private String name;

    @Excel(name = "用户密码")
    private String password;

    @Excel(name = "年龄", suffix = "岁")
    private Integer age;

    @Excel(name = "性别", readConverterExp = "0=男,1=女")
    private String sex;

    @Excel(name = "部门", combo = {"研发", "行政"})
    private String dept;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getDept() {
        return dept;
    }

    public void setDept(String dept) {
        this.dept = dept;
    }
}
