package com.zjj.util;

import com.zjj.domain.CommonRespo;

public class CommonRespoUtil<T> {
    public static CommonRespo isOk(String msg) {
        return CommonRespo.builder().code("100200").msg(msg).build();
    }

    public static CommonRespo isOk(String code, String msg) {
        return CommonRespo.builder().code(code).msg(msg).build();
    }

    public static CommonRespo isOk(String code, String msg, Object o) {
        return CommonRespo.builder().code(code).msg(msg).object(o).build();
    }

    public static CommonRespo isFail(String msg) {
        return CommonRespo.builder().code("100400").msg(msg).build();
    }

    public static CommonRespo isFail(String code, String msg) {
        return CommonRespo.builder().code(code).msg(msg).build();
    }

    public static CommonRespo isFail(String code, String msg, Object o) {
        return CommonRespo.builder().code(code).msg(msg).object(o).build();
    }
}
