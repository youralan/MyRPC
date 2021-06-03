package com.alan.rpc.transport;

import com.alan.rpc.common.entity.RpcRequest;
import com.alan.rpc.serializer.CommonSerializer;
import com.alan.rpc.serializer.KryoSerializer;

public interface RpcClient {
    Object sendRequest(RpcRequest rpcRequest);

    void setSerializer(CommonSerializer serializer);
}
