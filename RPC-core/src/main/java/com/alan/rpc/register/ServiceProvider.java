package com.alan.rpc.register;

import com.alan.rpc.common.excpetion.RpcException;

/**
 * 本地服务注册表
 */
public interface ServiceProvider {

    <T> void addServiceProvider(T service, String serviceName);

    Object getServiceProvider(String serviceName);
}
