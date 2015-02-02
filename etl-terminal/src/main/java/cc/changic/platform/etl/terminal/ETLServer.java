package cc.changic.platform.etl.terminal;

import cc.changic.platform.etl.protocol.handler.ETLMessageHandlerInitializer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LoggingHandler;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by Panda.Z on 2015/1/28.
 */
public class ETLServer {

    private int port;

    public ETLServer(int port) {
        this.port = port;
    }

    @Autowired
    private LoggingHandler loggingHandler;

    @Autowired
    private ETLMessageHandlerInitializer messageHandlerInitializer;

    public void bind() throws Exception {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap nettyBootstrap = new ServerBootstrap();
            nettyBootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 1024)
                    .handler(loggingHandler)
                    .childHandler(messageHandlerInitializer);

            ChannelFuture future = nettyBootstrap.bind(port).sync();

            future.channel().closeFuture().sync();
        } finally {
//            bossGroup.shutdownGracefully();
//            workerGroup.shutdownGracefully();
        }
    }
}
