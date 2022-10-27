package com.itheima.reggie.common;

/**
 * 基于threadLocal封装工具类 ，便于用户存取当前线程id值
 */
public class BaseContext {
    private static ThreadLocal<Long> threadLocal = new ThreadLocal<>();

    public static void setCurrentId(Long id) {
        threadLocal.set(id);
    }

    public static Long getCurrentId() {
        return threadLocal.get();
    }
}
