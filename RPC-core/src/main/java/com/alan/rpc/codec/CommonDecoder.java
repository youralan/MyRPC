package com.alan.rpc.codec;

import com.alan.rpc.common.entity.RpcRequest;
import com.alan.rpc.common.entity.RpcResponse;
import com.alan.rpc.common.enumeration.PackageType;
import com.alan.rpc.common.enumeration.RpcError;
import com.alan.rpc.common.excpetion.RpcException;
import com.alan.rpc.serializer.CommonSerializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.ReplayingDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class CommonDecoder extends ReplayingDecoder {

    private static final Logger LOGGER = LoggerFactory.getLogger(CommonDecoder.class);
    private static final int MAGIC_NUMBER = 0XCAFEBABE;

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        //解码魔数
        int magic = in.readInt();
        if(magic != MAGIC_NUMBER){
            LOGGER.error("不识别的包协议：{}",magic);
            throw new RpcException(RpcError.UNKNOWN_PROTOCOL);
        }
        //解码包类型
        int packageCode = in.readInt();
        Class<?> packageClass;
        if(packageCode == PackageType.REQUEST_PACK.getCode()){
            packageClass = RpcRequest.class;
        }else if(packageCode == PackageType.RESPONSE_PACK.getCode()){
            packageClass = RpcResponse.class;
        }else{
            LOGGER.error("不识别的包协议：{}", packageCode);
            throw new RpcException(RpcError.UNKNOWN_PACKAGE_TYPE);
        }
        //解码序列化方式
        int serializerCode = in.readInt();
        CommonSerializer serializer = CommonSerializer.getByCode(serializerCode);
        if(serializer == null) {
            LOGGER.error("不识别的反序列化器: {}", serializerCode);
            throw new RpcException(RpcError.UNKNOWN_SERIALIZER);
        }
        int length = in.readInt();
        byte[] bytes = new byte[length];
        in.readBytes(bytes);
        Object obj = serializer.deserialize(bytes, packageClass);
        out.add(obj);
    }
}
