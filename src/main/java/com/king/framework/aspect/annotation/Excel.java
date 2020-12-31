package com.king.framework.aspect.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 自定义导出Excel数据注解
 * @author 金振林
 */
// Retention注解决定MyAnnotation注解的生命周期
@Retention(RetentionPolicy.RUNTIME)
// Target注解决定MyAnnotation注解可以加在哪些成分上，如加在类身上，或者属性身上，或者方法身上等成分
@Target(ElementType.FIELD)
public @interface Excel {

    /**
     * 导出到Excel中的名字
     */
    public String name() default "";

    /**
     * 日期格式，如：yyyy-MM-dd
     */
    public String dateFormat() default "";

    /**
     * 读取内容转表达式，如：0=男，1=女
     */
    public String readConverterExp() default "";

    /**
     * 导出时在Excel中每个列的高度，单位为字符
     */
    public double height() default 14;

    /**
     * 导出时在Excel中每个列的宽度，单位为字符
     */
    public double width() default 16;

    /**
     * 文字后缀，如% （90变成90%）
     */
    public String suffix() default "";

    /**
     * 字段为空时的默认值
     */
    public String defaultValue() default "";

    /**
     * 提示信息
     */
    public String prompt() default "";

    /**
     * 设置只能选择不能输入的列内容
     */
    public String[] combo() default {};

    /**
     * 是否导出数据，应对需求：有时我们需要导出一份模板，这是标题需要但是内容需要用户手工填写
     */
    public boolean isExport() default true;

    /**
     * 另一个类中的属性名称，支持多级获取，以小数点隔开
     */
    public String targetAttr() default "";

    /**
     * 字段类型（0：导入导出；1：仅导出；2：仅导入）
     */
    public Type type() default Type.ALL;

    public enum Type {
        /**
         * 导入导出
         */
        ALL(0),
        /**
         * 仅导出
         */
        EXPORT(1),
        /**
         * 仅导入
         */
        IMPORT(2);

        private final int value;

        Type(int value) {
            this.value = value;
        }

        public int value() {
            return this.value;
        }

    }

}
