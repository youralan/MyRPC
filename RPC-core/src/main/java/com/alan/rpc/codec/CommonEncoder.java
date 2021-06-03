package com.alan.rpc.codec;

import com.alan.rpc.common.entity.RpcRequest;
import com.alan.rpc.common.enumeration.PackageType;
import com.alan.rpc.serializer.CommonSerializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * 自定义Netty编码器
 *     自定义协议
 * +---------------+---------------+-----------------+-------------+
 * |  Magic Number |  Package Type | Serializer Type | Data Length |
 * |    4 bytes    |    4 bytes    |     4 bytes     |   4 bytes   |
 * +---------------+---------------+-----------------+-------------+
 * |                          Data Bytes                           |
 * |                   Length: ${Data Length}                      |
 * +---------------------------------------------------------------+
 * 首先是 4 字节魔数，表识一个协议包。
 * 接着是 Package Type，标明这是一个调用请求还是调用响应，
 * Serializer Type 标明了实际数据使用的序列化器，这个服务端和客户端应当使用统一标准；
 * Data Length 就是实际数据的长度，设置这个字段主要防止粘包，
 * 最后就是经过序列化后的实际数据，可能是 RpcRequest 也可能是 RpcResponse 经过序列化后的字节，取决于 Package Type
 */
public class CommonEncoder extends MessageToByteEncoder {

    private static final int MAGIC_NUMBER = 0XCAFEBABE;

    private final CommonSerializer serializer;

    public CommonEncoder(CommonSerializer serializer) {
        this.serializer = serializer;
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out) throws Exception {
        //发送魔数
        out.writeInt(MAGIC_NUMBER);
        //发送PackageType
        if(msg instanceof RpcRequest){
            out.writeInt(PackageType.REQUEST_PACK.getCode());
        }else {
            out.writeInt(PackageType.RESPONSE_PACK.getCode());
        }
        //发送序列化类型
        out.writeInt(serializer.getCode());
        byte[] bytes = serializer.serialize(msg);
        //发送数据长度，解决粘包拆包
        out.writeInt(bytes.length);
        //发送数据
        out.writeBytes(bytes);
    }
}
