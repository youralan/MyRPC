package com.alan.test.provider;

import com.alan.rpc.api.HelloService;
import com.alan.rpc.api.HelloServiceImpl;
import com.alan.rpc.common.excpetion.RpcException;
import com.alan.rpc.register.DefaultServiceRegister;
import com.alan.rpc.register.ServiceRegistry;
import com.alan.rpc.transport.RpcServer;

public class TestProvider {
    public static void main(String[] args) throws RpcException {
        //服务对象
        HelloService helloService = new HelloServiceImpl();
        //注册表
        ServiceRegistry serviceRegister = new DefaultServiceRegister();
        //注册服务
        serviceRegister.register(helloService);
        //启动服务
        RpcServer rpcServer = new RpcServer(serviceRegister);
        rpcServer.start(8888);
    }
}
