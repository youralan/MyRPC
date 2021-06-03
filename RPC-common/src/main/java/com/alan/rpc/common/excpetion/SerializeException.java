package com.alan.rpc.common.excpetion;

public class SerializeException extends RuntimeException {
    public SerializeException(Object message) {
        super(message.toString());
    }

    public SerializeException() {
    }
}
