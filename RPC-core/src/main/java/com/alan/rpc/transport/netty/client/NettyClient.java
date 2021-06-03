package com.alan.rpc.transport.netty.client;

import com.alan.rpc.codec.CommonDecoder;
import com.alan.rpc.codec.CommonEncoder;
import com.alan.rpc.common.entity.RpcRequest;
import com.alan.rpc.common.entity.RpcResponse;
import com.alan.rpc.common.enumeration.RpcError;
import com.alan.rpc.common.excpetion.RpcException;
import com.alan.rpc.register.NacosServiceRegistry;
import com.alan.rpc.register.ServiceRegistry;
import com.alan.rpc.serializer.CommonSerializer;
import com.alan.rpc.transport.RpcClient;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.AttributeKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.net.InetSocketAddress;

public class NettyClient implements RpcClient {

    private static final Logger logger = LoggerFactory.getLogger(NettyClient.class);
    private static final Bootstrap bootstrap;
    private CommonSerializer serializer;
    private ServiceRegistry serviceRegistry ;

    public NettyClient(ServiceRegistry serviceRegistry) {
        this.serviceRegistry = serviceRegistry;
    }

    static {
        EventLoopGroup group = new NioEventLoopGroup();
        bootstrap = new Bootstrap();
        bootstrap.group(group)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.SO_KEEPALIVE, true);
    }


    @Override
    public Object sendRequest(RpcRequest rpcRequest) {
        if(serializer == null) {
            logger.error("未设置序列化器");
            throw new RpcException(RpcError.SERIALIZER_NOT_FOUND);
        }
        try {
            InetSocketAddress inetSocketAddress = serviceRegistry.lookupService(rpcRequest.getInterfaceName());
            ChannelFuture channelFuture = bootstrap.connect(inetSocketAddress).sync();
            logger.info("客户端连接到服务器{}:{}",inetSocketAddress);
            Channel channel = channelFuture.channel();
            if(channel != null){
                channel.writeAndFlush(rpcRequest).addListener(future -> {
                    if(future.isSuccess()){
                        logger.info(String.format("客户端发送消息：%s",rpcRequest));
                    }else {
                        logger.error("发送消息时有错误发生：",future.cause());
                    }
                });
                    channel.closeFuture().sync();
                    AttributeKey<RpcResponse> key = AttributeKey.valueOf("rpcResponse");
                    RpcResponse rpcResponse = channel.attr(key).get();
                    return rpcResponse;
                }
            } catch (InterruptedException e) {
                logger.error("发送消息时有异常发生：",e);
            }
        return null;
    }

    public void setSerializer(CommonSerializer serializer) {
        this.serializer = serializer;
        bootstrap.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel socketChannel) throws Exception {
                ChannelPipeline pipeline = socketChannel.pipeline();
                pipeline.addLast(new CommonDecoder())
                        .addLast((new CommonEncoder(serializer)))
                        .addLast(new NettyClientHandler());
            }
        });
    }
}
