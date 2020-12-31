package com.king.common.exception;

/**
 * 业务异常
 *
 * @author 金振林
 * @version v1.0
 * @date 2020/12/22 13:56
 */
public class BusinessException extends RuntimeException {

    protected final String message;

    public BusinessException(String message) {
        this.message = message;
    }

    public BusinessException(String message, Throwable e) {
        super(message, e);
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
