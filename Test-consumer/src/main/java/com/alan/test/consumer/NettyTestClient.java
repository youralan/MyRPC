package com.alan.test.consumer;

import com.alan.rpc.api.HelloObject;
import com.alan.rpc.api.HelloService;
import com.alan.rpc.register.NacosServiceRegistry;
import com.alan.rpc.serializer.KryoSerializer;
import com.alan.rpc.transport.RpcClient;
import com.alan.rpc.transport.RpcClientProxy;
import com.alan.rpc.transport.netty.client.NettyClient;

public class NettyTestClient {
    public static void main(String[] args) {
        RpcClient client = new NettyClient(new NacosServiceRegistry());
        client.setSerializer(new KryoSerializer());
        RpcClientProxy rpcClientProxy = new RpcClientProxy(client);
        HelloService helloService = rpcClientProxy.getProxy(HelloService.class);
        HelloObject object = new HelloObject(12, "This is a message");
        String res = helloService.hello(object);
        System.out.println(res);
    }
}
