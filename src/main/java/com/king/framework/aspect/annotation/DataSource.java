package com.king.framework.aspect.annotation;

import com.king.framework.aspect.enums.DataSourceType;

import java.lang.annotation.*;

/**
 * 自定义多数据源切换注解
 *
 * @author 金振林
 */
// ElementType.TYPE 表示只能用在类、接口、枚举类型上。
@Target({ ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
// 此注解表示能被子类继承
@Inherited
public @interface DataSource {

    /**
     * 切换数据源名称
     */
    public DataSourceType value() default DataSourceType.MASTER;

}
