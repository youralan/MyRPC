package com.alan.rpc.common.excpetion;

public class RpcException extends Throwable {
    public RpcException(Object message){
        super(message.toString());
    }
    public RpcException(){

    }
}
