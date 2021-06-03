package com.alan.rpc.common.util;

import com.alan.rpc.common.enumeration.RpcError;
import com.alan.rpc.common.excpetion.RpcException;
import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingFactory;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.Instance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class NacosUtil {

    private static final Logger logger = LoggerFactory.getLogger(NacosUtil.class);

    private static final NamingService NAMING_SERVICE;
    private static final Set<String> serviceNames = new HashSet<>();
    private static InetSocketAddress address;

    private static final String NACOS_ADDR = "127.0.0.1:8848";

    static {
        NAMING_SERVICE = getNamingService();
    }

    public static NamingService getNamingService(){
        try {
            return NamingFactory.createNamingService(NACOS_ADDR);
        } catch (NacosException e) {
            logger.error("连接到Nacos时有错误发生: ", e);
            throw new RpcException(RpcError.FAILED_TO_CONNECT_TO_SERVICE_REGISTRY);        }
    }

    public static void registerService(String serviceName, InetSocketAddress address) throws NacosException {
        NAMING_SERVICE.registerInstance(serviceName, address.getHostName(), address.getPort());
        NacosUtil.address = address;
        serviceNames.add(serviceName);
    }

    public static List<Instance> getAllInstance(String serviceName) throws NacosException {
        return NAMING_SERVICE.getAllInstances(serviceName);
    }

    public static void clearRegistry(){
        if(!serviceNames.isEmpty() && address != null){
            String host = address.getHostName();
            int port = address.getPort();
            Iterator<String> iterator = serviceNames.iterator();
            while (iterator.hasNext()){
                String serviceName = iterator.next();
                try {
                    NAMING_SERVICE.deregisterInstance(serviceName, host, port);
                } catch (NacosException e) {
                    logger.error("注销服务 {} 失败", serviceName, e);
                }
            }
        }
    }

}
