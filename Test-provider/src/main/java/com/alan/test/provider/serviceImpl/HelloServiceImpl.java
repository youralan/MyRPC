package com.alan.test.provider.serviceImpl;

import com.alan.rpc.annotation.Service;
import com.alan.rpc.api.HelloObject;
import com.alan.rpc.api.HelloService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class HelloServiceImpl implements HelloService {
    private final static Logger LOGGER = LoggerFactory.getLogger(HelloServiceImpl.class);
    @Override
    public String hello(HelloObject helloObject) {
        LOGGER.info("服务端接收到来自客户端的请求"+helloObject.getMessage());
        return "返回给客户端的信息："+helloObject.getId();
    }
}
