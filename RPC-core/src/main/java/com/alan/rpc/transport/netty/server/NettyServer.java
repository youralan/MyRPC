package com.alan.rpc.transport.netty.server;

import com.alan.rpc.codec.CommonDecoder;
import com.alan.rpc.codec.CommonEncoder;
import com.alan.rpc.common.enumeration.RpcError;
import com.alan.rpc.common.excpetion.RpcException;
import com.alan.rpc.hook.ShutdownHook;
import com.alan.rpc.loadBalancer.RandomLoadBalancer;
import com.alan.rpc.register.NacosServiceRegistry;
import com.alan.rpc.register.ServiceProvider;
import com.alan.rpc.register.ServiceProviderImpl;
import com.alan.rpc.register.ServiceRegistry;
import com.alan.rpc.serializer.CommonSerializer;
import com.alan.rpc.serializer.KryoSerializer;
import com.alan.rpc.transport.AbstractRpcServer;
import com.alan.rpc.transport.RpcServer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;

public class NettyServer extends AbstractRpcServer {

    private static final Logger logger = LoggerFactory.getLogger(NettyServer.class);
    private String host;
    private int port;
    private CommonSerializer serializer;
    private ServiceRegistry serviceRegistry;
    private ServiceProvider serviceProvider;

    public NettyServer(String host, int port, Integer serializer) {
        this.host = host;
        this.port = port;
        serviceRegistry = new NacosServiceRegistry();
        serviceProvider = new ServiceProviderImpl();
        this.serializer = CommonSerializer.getByCode(serializer);
        scanServices();
    }


    @Override
    public void start() {
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workGroup = new NioEventLoopGroup();
        try {
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        serverBootstrap.group(bossGroup, workGroup)
                .channel(NioServerSocketChannel.class)
                .handler(new LoggingHandler(LogLevel.INFO))
                .option(ChannelOption.SO_BACKLOG, 256)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .childOption(ChannelOption.TCP_NODELAY, true)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        ChannelPipeline pipeline = socketChannel.pipeline();
                        pipeline.addLast(new CommonDecoder());
                        pipeline.addLast(new CommonEncoder(serializer));
                        pipeline.addLast(new NettyServerHandler());
                    }
                });
            ChannelFuture channelFuture = serverBootstrap.bind(port).sync();
            ShutdownHook.getShutdownHook().addClearAllHook();
            channelFuture.channel().closeFuture().sync();

        } catch (InterruptedException e) {
            logger.error("启动服务时有异常发生：",e.getMessage());
        } finally {
            bossGroup.shutdownGracefully();
            workGroup.shutdownGracefully();
        }

    }

    @Override
    public <T> void publishService(T service, String serviceName) {
        serviceProvider.addServiceProvider(service, serviceName);
        serviceRegistry.register(serviceName, new InetSocketAddress(host, port));
    }

//    @Override
//    public <T> void publishService(T service, String serviceName) {
//
//    }
//
//
//    public <T> void publishService(Object service, Class<T> serviceClass) {
//        if(serializer == null) {
//            logger.error("未设置序列化器");
//            throw new RpcException(RpcError.SERIALIZER_NOT_FOUND);
//        }
//        serviceProvider.addServiceProvider(service, serviceClass.getCanonicalName());
//        serviceRegistry.register(serviceClass.getCanonicalName(), new InetSocketAddress(host, port));
//        start();
//    }
}
