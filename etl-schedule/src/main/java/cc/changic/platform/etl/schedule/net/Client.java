package cc.changic.platform.etl.schedule.net;

import cc.changic.platform.etl.protocol.handler.ETLMessageHandlerInitializer;
import cc.changic.platform.etl.protocol.message.DuplexMessage;
import cc.changic.platform.etl.protocol.message.ExampleMessageHandler;
import cc.changic.platform.etl.protocol.rmi.ETLMessage;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LoggingHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Netty客户端
 */
public class Client {

    private static Logger logger = LoggerFactory.getLogger(Client.class);

    private final int port;

    @Autowired
    private ETLMessageHandlerInitializer messageHandlerInitializer;

    public Client(int port) {
        this.port = port;
    }


    static EventLoopGroup group = new NioEventLoopGroup();

    public void write(String host, DuplexMessage message) throws Exception {

        try {
            Bootstrap b = new Bootstrap();
            b.group(group)
                    .channel(NioSocketChannel.class)
                    .handler(messageHandlerInitializer);
            logger.info("Connect remote server: [host={}, port={}]", host, port);
            ChannelFuture future = b.connect(host, port).sync();
            Channel channel = future.channel();
            channel.writeAndFlush(message);
            channel.closeFuture().sync();
        } catch (Exception e) {
            logger.error("Netty client exception: {}", e.getMessage(), e);
//            group.shutdownGracefully();
            throw e;
        } finally {
//            group.shutdownGracefully();
        }
    }
}
