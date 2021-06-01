package com.alan.rpc.handler;

import com.alan.rpc.common.entity.RpcRequest;
import com.alan.rpc.common.entity.RpcResponse;
import com.alan.rpc.common.excpetion.RpcException;
import com.alan.rpc.register.ServiceRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class RequestHandlerThread implements Runnable{

    private static final Logger logger = LoggerFactory.getLogger(RequestHandlerThread.class);

    private Socket socket;
    private RequestHandler requestHandler;
    private ServiceRegistry serviceRegister;

    public RequestHandlerThread(Socket socket, RequestHandler requestHandler, ServiceRegistry serviceRegister) {
        this.socket = socket;
        this.requestHandler = requestHandler;
        this.serviceRegister = serviceRegister;
    }

    @Override
    public void run() {

        try(ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream())) {
            //从请求对象中获取服务
            RpcRequest rpcRequest = (RpcRequest) ois.readObject();
            String interfaceName = rpcRequest.getInterfaceName();
            Object service = serviceRegister.getService(interfaceName);
            //服务处理
            Object result = requestHandler.handle(rpcRequest, service);
            oos.writeObject(RpcResponse.success(result));
            oos.flush();
        }catch (IOException | ClassNotFoundException | RpcException e){
                logger.error("调用或发送时有异常发生：",e);
        }
    }
}
