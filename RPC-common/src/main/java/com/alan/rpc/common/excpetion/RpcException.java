package com.alan.rpc.common.excpetion;

public class RpcException extends RuntimeException {
    public RpcException(Object message){
        super(message.toString());
    }
    public RpcException(){

    }
}
