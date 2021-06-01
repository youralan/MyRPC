package com.alan.rpc.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;

public class HelloServiceImpl implements HelloService {
    private final static Logger LOGGER = LoggerFactory.getLogger(HelloServiceImpl.class);
    @Override
    public String hello(HelloObject helloObject) {
        LOGGER.info("服务端接收到来自客户端的请求"+helloObject.getMessage());
        return "返回给客户端的信息："+helloObject.getId();
    }
}
