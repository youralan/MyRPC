package com.alan.rpc.common.entity;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/**
 * rpc调用请求
 */
@Data
@Builder//让我们可以链式编程去初始化一个对象
public class RpcRequest implements Serializable {
    /**
     * 调用的接口名
     */
    private String interfaceName;

    /**
     * 调用的方法名
     */
    private String methodName;

    /**
     * 调用的参数
     */
    private Object[] parameters;

    /**
     * 调用的参数类型
     */
    private Class<?>[] parametersType;
}
