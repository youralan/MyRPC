package com.alan.rpc.transport.netty.client;

import com.alan.rpc.common.entity.RpcResponse;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.AttributeKey;
import io.netty.util.ReferenceCountUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NettyClientHandler extends SimpleChannelInboundHandler<RpcResponse> {

    private static final Logger logger = LoggerFactory.getLogger(NettyClientHandler.class);

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcResponse msg) throws Exception {
        try {
            logger.info(String.format("客户端收到消息：%s", msg));
            //Channel上的AttributeMap就是大家共享的，每一个ChannelHandler都能获取到
            AttributeKey<RpcResponse> key = AttributeKey.valueOf("rpcResponse");
            ctx.channel().attr(key).set(msg);
            ctx.channel().close();
        }finally {
            /*
            ReferenceCountUtil.release()其实是ByteBuf.release()方法（从ReferenceCounted接口继承而来）的包装。
            netty4中的ByteBuf使用了引用计数（netty4实现了一个可选的ByteBuf池），
            每一个新分配的ByteBuf的引用计数值为1，每对这个ByteBuf对象增加一个引用，需要调用ByteBuf.retain()方法，而每减少一个引用，
            需要调用ByteBuf.release()方法。当这个ByteBuf对象的引用计数值为0时，表示此对象可回收。
            我这只是用ByteBuf说明，还有其他对象实现了ReferenceCounted接口，此时同理。
             */
            ReferenceCountUtil.release(msg);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error("过程调用时有错误发生:");
        cause.printStackTrace();
        ctx.close();
    }

}
