package com.alan.rpc.register;

import java.net.InetSocketAddress;

/**
 * 远程服务注册中心
 */
public interface ServiceRegistry {
    //注册服务
    void register(String serviceName, InetSocketAddress inetSocketAddress);
    //服务查询
    InetSocketAddress lookupService(String serviceName);
}
