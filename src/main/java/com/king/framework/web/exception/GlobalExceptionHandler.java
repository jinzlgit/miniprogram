package com.king.framework.web.exception;

import com.king.common.exception.BusinessException;
import com.king.framework.web.domain.Result;
import org.apache.shiro.ShiroException;
import org.apache.shiro.authz.UnauthenticatedException;
import org.apache.shiro.authz.UnauthorizedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;


/**
 * 全局异常处理器
 *
 * @author 金振林
 * @version v1.0
 * @date 2020/12/16 13:58
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * 业务异常
     */
    @ExceptionHandler(BusinessException.class)
    public Result business(BusinessException e) {
        log.error(e.getMessage(), e);
        return Result.error().code(400).msg(e.getMessage());
    }

    /**
     * 捕捉所有Shiro异常
     */
    @ExceptionHandler(ShiroException.class)
    public Result handle401(ShiroException e) {
        // return "无权访问(Unauthorized):" + e.getMessage();
        return Result.error().code(401).msg("SHIRO异常");
    }

    /**
     * 单独捕捉Shiro(UnauthorizedException)异常 该异常为访问有权限管控的请求而该用户没有所需权限所抛出的异常
     */
    @ExceptionHandler(UnauthorizedException.class)
    public Result handle401(UnauthorizedException e) {
        // return "无权访问(Unauthorized):当前Subject没有此请求所需权限(" + e.getMessage() + ")";
        return Result.error().code(401).msg("没有权限");
    }

    /**
     * 单独捕捉Shiro(UnauthenticatedException)异常
     * 该异常为以游客身份访问有权限管控的请求无法对匿名主体进行授权，而授权失败所抛出的异常
     */
    @ExceptionHandler(UnauthenticatedException.class)
    public Result handle401(UnauthenticatedException e) {
        // return "无权访问(Unauthorized):当前Subject是匿名Subject，请先登录(This subject is anonymous.)";
        return Result.error().code(401).msg("认证失败");
    }

    /**
     * 请求方式不支持
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public Result httpMethodNot(HttpRequestMethodNotSupportedException e) {
        log.error(e.getMessage(), e);
        return Result.error().msg("不支持[" + e.getMethod() + "]请求");
    }

    @ExceptionHandler(RuntimeException.class)
    public Result runtime(RuntimeException e) {
        log.error(e.getMessage(), e);
        return Result.error().msg("运行时异常");
    }

    /**
     * 捕捉404异常
     */
    @ExceptionHandler(NoHandlerFoundException.class)
    public Result handle(NoHandlerFoundException e) {
        log.error(e.getMessage(), e);
        return Result.error().msg("此路径[" + e.getRequestURL() + "]不存在");
    }

    /**
     * 系统异常
     */
    @ExceptionHandler(Exception.class)
    public Result globalException(Exception e) {
        log.error(e.getMessage(), e);
        return Result.error().msg("服务器500错误");
    }

    /**
     * 参数校验异常
     */
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public Result notValid(MethodArgumentNotValidException e) {
        log.error(e.getMessage(), e);
        return Result.error().msg(e.getMessage());
    }

}
