package com.alan.rpc.register;

import com.alan.rpc.common.excpetion.RpcException;

/**
 * 服务注册表接口
 */
public interface ServiceRegistry {
    //注册服务，获取服务
    <T> void register(T service) throws RpcException;
    Object getService(String serviceName) throws RpcException;
}
