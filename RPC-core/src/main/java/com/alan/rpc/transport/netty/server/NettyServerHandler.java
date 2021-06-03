package com.alan.rpc.transport.netty.server;

import com.alan.rpc.common.entity.RpcRequest;
import com.alan.rpc.handler.RequestHandler;
import com.alan.rpc.register.ServiceProviderImpl;
import com.alan.rpc.register.ServiceProvider;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NettyServerHandler extends SimpleChannelInboundHandler<RpcRequest> {

    private static final Logger logger = LoggerFactory.getLogger(NettyServerHandler.class);
    private static RequestHandler requestHandler;
    private static ServiceProvider serviceRegistry;

    static {
        requestHandler = new RequestHandler();
        serviceRegistry = new ServiceProviderImpl();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcRequest msg) throws Exception {
        logger.info("服务器收到请求：{}", msg);
        String interfaceName = msg.getInterfaceName();
        Object service = serviceRegistry.getServiceProvider(interfaceName);
        Object result = requestHandler.handle(msg, service);
        ChannelFuture channelFuture = ctx.writeAndFlush(result);
        channelFuture.addListener(ChannelFutureListener.CLOSE);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error("处理过程调用时有错误发生:");
        cause.printStackTrace();
        ctx.close();
    }

}
