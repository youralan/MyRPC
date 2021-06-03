package com.alan.test.provider;

import com.alan.rpc.annotation.ServiceScan;
import com.alan.rpc.serializer.CommonSerializer;
import com.alan.rpc.transport.netty.server.NettyServer;

@ServiceScan()
public class NettyTestServer {
    public static void main(String[] args) {
        NettyServer server = new NettyServer("127.0.0.1", 9999, CommonSerializer.KRYO_SERIALIZER);
        server.start();
    }
}
