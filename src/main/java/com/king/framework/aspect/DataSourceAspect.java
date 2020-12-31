package com.king.framework.aspect;

import cn.hutool.core.util.ObjectUtil;
import com.king.framework.aspect.annotation.DataSource;
import com.king.framework.datasource.DynamicDataSourceContextHolder;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * 多数据源切面
 *
 * @author 金振林
 * @version v1.0
 * @date 2020/12/25 10:46
 */
@Aspect
@Order(1)
@Component
public class DataSourceAspect {

    private static final Logger log = LoggerFactory.getLogger(DataSourceAspect.class);

    @Pointcut("@annotation(com.king.framework.aspect.annotation.DataSource)"
             + " || @within(com.king.framework.aspect.annotation.DataSource)")
    public void dsPointCut() {

    }

    @Around("dsPointCut()")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        DataSource dataSource = getDataSource(point);
        if (ObjectUtil.isNotNull(dataSource)) {
            DynamicDataSourceContextHolder.setDataSourceType(dataSource.value().name());
        }
        try {
            return point.proceed();
        } finally {
            // 销毁数据源，在执行方法之后
            DynamicDataSourceContextHolder.cleanDataSourceType();
        }
    }

    /**
     * 获取需要切换的数据源
     */
    public DataSource getDataSource(ProceedingJoinPoint point) {
        MethodSignature signature = (MethodSignature) point.getSignature();
        Class<? extends Object> targetClass = point.getTarget().getClass();
        DataSource dataSource = targetClass.getAnnotation(DataSource.class);
        if (ObjectUtil.isNotNull(dataSource)) {
            return dataSource;
        } else {
            Method method = signature.getMethod();
            DataSource annotation = method.getAnnotation(DataSource.class);
            return annotation;
        }
    }

}
