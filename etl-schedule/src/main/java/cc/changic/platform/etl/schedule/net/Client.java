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

    public void write(String host, DuplexMessage message) throws Exception {
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap();
            b.group(group)
                    .channel(NioSocketChannel.class)
                    .handler(messageHandlerInitializer);
            ChannelFuture future = b.connect(host, port).sync();
            Channel channel = future.channel();
            channel.writeAndFlush(message);
        } catch (Exception e){
            logger.error("Netty client exception: {}",e.getMessage(),e);
            group.shutdownGracefully();
            throw e;
        }finally {
//            group.shutdownGracefully();
        }
    }
}
