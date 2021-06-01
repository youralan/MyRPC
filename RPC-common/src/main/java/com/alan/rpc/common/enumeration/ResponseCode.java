package com.alan.rpc.common.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum  ResponseCode {

    SUCCESS(200, "调用方法成功"),
    FAIL(500,"调用方法出错"),
    METHOD_NOT_FOUND(500,"未找到指定方法"),
    CLASS_NOT_FOUND(500,"未找到指定类");

    private final int code;
    private final String message;
}
