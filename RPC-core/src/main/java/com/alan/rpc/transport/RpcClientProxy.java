package com.alan.rpc.transport;

import com.alan.rpc.common.entity.RpcRequest;
import com.alan.rpc.common.entity.RpcResponse;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * 代理中间类invocationHandler
 */
public class RpcClientProxy implements InvocationHandler {
    private String host;
    private int port;

    public RpcClientProxy(String host, int port){
        this.host = host;
        this.port = port;
    }

    @SuppressWarnings("unchecked")
    public <T> T getProxy(Class<T> clazz){
        return (T) Proxy.newProxyInstance(clazz.getClassLoader(), new Class<?>[]{clazz}, this);
    }

    /**
     * 在代理类的invoke方法中执行远程过程调用，获得server的执行结果
     * @param proxy
     * @param method
     * @param args
     * @return
     * @throws Throwable
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        //rpc请求
        RpcRequest request = RpcRequest.builder()
                .interfaceName(method.getDeclaringClass().getCanonicalName())//服务接口名
                .methodName(method.getName())//调用方法名
                .parameters(args)//方法参数
                .parametersType(method.getParameterTypes())//参数类型
                .build();
        RpcClient rpcClient = new RpcClient();//rpc客户端
        //返回执行的data信息
        return ((RpcResponse) rpcClient.sendRequest(request, host, port)).getData();
    }

}
