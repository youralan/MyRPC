package com.alan.rpc.register;

import com.alan.rpc.common.enumeration.RpcError;
import com.alan.rpc.common.excpetion.RpcException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class DefaultServiceRegister implements ServiceRegistry {

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultServiceRegister.class);

    private final Map<String, Object> serviceMap = new ConcurrentHashMap<>();//服务注册表，K是接口名，V是服务
    private final Set<String> registeredService = ConcurrentHashMap.newKeySet();//V是服务名，即实现类的类名

    @Override
    public synchronized  <T> void register(T service) throws RpcException {
        //更新registeredService
        String serviceName = service.getClass().getCanonicalName();
        if(registeredService.contains(serviceName)) return;
        registeredService.add(serviceName);
        //更新serviceMap
        Class<?>[] interfaces = service.getClass().getInterfaces();
        if(interfaces.length == 0){
            throw new RpcException(RpcError.SERVICE_NOT_IMPLEMENT_ANY_INTERFACE);
        }
        for (Class<?> i : interfaces) {
            serviceMap.put(i.getCanonicalName(), service);
        }
        LOGGER.info("向接口: {} 注册服务: {}", interfaces, serviceName);
    }

    @Override
    public synchronized Object getService(String serviceName) throws RpcException {
        Object service = serviceMap.get(serviceName);
        if(service == null){
            throw new RpcException(RpcError.SERVICE_NOT_FOUND);
        }
        return service;
    }
}
