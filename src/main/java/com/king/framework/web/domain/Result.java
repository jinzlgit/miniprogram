package com.king.framework.web.domain;

import com.king.framework.aspect.enums.R;

import java.io.Serializable;

/**
 * 统一返回实体类
 *
 * @author 金振林
 * @version v1.0
 * @date 2020/12/22 11:42
 */
public class Result implements Serializable {

    /**
     * 状态码
     */
    private Integer code;

    /**
     * 响应描述
     */
    private String msg;

    /**
     * 数据对象
     */
    private Object data;

    /**
     * 构造器私有
     */
    private Result() {
    }

    /**-----------------------通用返回方法--------------------------**/

    /**
     * 通用返回成功
     */
    public static Result ok() {
        Result result = new Result();
        result.setCode(R.SUCCESS.code());
        result.setMsg(R.SUCCESS.msg());
        return result;
    }

    /**
     * 通用返回未知错误
     */
    public static Result error() {
        Result result = new Result();
        result.setCode(R.ERROR.code());
        result.setMsg(R.ERROR.msg());
        return result;
    }

    /**
     * 通用设置返回结果
     *
     * @param r 通用返回枚举
     */
    public static Result setResult(R r) {
        Result result = new Result();
        result.setCode(r.code());
        result.setMsg(r.msg());
        return result;
    }


    /**-----------------------使用链式编程，返回类本身--------------------------**/

    /**
     * 自定义状态码
     */
    public Result code(Integer code) {
        this.code = code;
        return this;
    }

    /**
     * 自定义返回描述
     */
    public Result msg(String msg) {
        this.msg = msg;
        return this;
    }

    /**
     * 自定义返回数据
     */
    public Result data(Object data) {
        this.data = data;
        return this;
    }

    /**-----------------------Getter and Setter--------------------------**/

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

}
