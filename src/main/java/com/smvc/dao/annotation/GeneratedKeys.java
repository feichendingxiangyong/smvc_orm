/**
 * @filename GeneratedKeys.java
 * @createtime 2015年11月8日 下午4:07:34
 * @author dingxiangyong
 * @comment 
 */
package com.smvc.dao.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 自动生成主键注解
 * @author Big Martin
 *
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface GeneratedKeys {
}
