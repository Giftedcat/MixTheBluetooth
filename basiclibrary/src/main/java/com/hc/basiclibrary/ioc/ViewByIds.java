package com.hc.basiclibrary.ioc;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/*
* 此注解是用于绑定属性，代替findViewById方法的
* 相同类型的属性，可以一起绑定，例如：
   @ViewByIds(value = {R.id.test1,R.id.test2,R.id.test3,R.id.test11},name = {"mSetContent","mSetName","testB2","testC1"})
    private TextView mSetContent,mSetName,testB2,testC1;
*
* 注意：属性与注解值要一一对应
*
* */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ViewByIds{
    int[] value();
    String[] name();
}
