package com.alan.test.consumer;

import com.alan.rpc.api.HelloObject;
import com.alan.rpc.api.HelloService;
import com.alan.rpc.transport.RpcClientProxy;

public class TestConsumer {
    public static void main(String[] args) {
        //获取一个代理对象，代理HelloService服务
        RpcClientProxy proxy = new RpcClientProxy("127.0.0.1",8888);
        HelloService helloService = proxy.getProxy(HelloService.class);
        //参数
        HelloObject object = new HelloObject(88,"hello server");
        //代理对象调用服务
        Object hello = helloService.hello(object);
        System.out.println(hello);
    }
}
