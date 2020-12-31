package com.king.framework.aspect.enums;

/**
 * HttpApi统一返回枚举
 *
 * @author 金振林
 * @version v1.0
 * @date 2020/12/22 11:58
 */
public enum R {

    /**
     * 成功
     */
    SUCCESS(0, "成功"),
    /**
     * 未知错误
     */
    ERROR(500, "未知错误");

    private Integer code;

    private String msg;

    R(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public Integer code() {
        return this.code;
    }

    public String msg() {
        return this.msg;
    }

}
