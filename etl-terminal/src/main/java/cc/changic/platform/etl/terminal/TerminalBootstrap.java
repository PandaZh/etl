package cc.changic.platform.etl.terminal;


import cc.changic.platform.etl.protocol.handler.ETLMessageHandlerInitializer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Created by Panda.Z on 2015/1/20.
 */
@Configuration
@ComponentScan(basePackages = "cc.changic.platform.etl")
public class TerminalBootstrap {

    private static Logger LOGGER = LoggerFactory.getLogger(TerminalBootstrap.class);

//    private int port;
//
//    public void setPort(int port) {
//        this.port = port;
//    }
//
//    @Autowired
//    private LoggingHandler loggingHandler;
//
//    @Autowired
//    private ETLMessageHandlerInitializer messageHandlerInitializer;

    public static void main(String[] args) {
        ConfigurableApplicationContext context = null;
        try {
            context = new ClassPathXmlApplicationContext("classpath:terminalContext.xml");
            ETLServer bootstrap = context.getBean(ETLServer.class);
            bootstrap.bind();
        } catch (Exception e) {
            LOGGER.error("Start ETL Terminal error: {}", e.getMessage(), e);
            System.exit(-1);
        }
    }


//    public void bind() throws Exception {
//        EventLoopGroup bossGroup = new NioEventLoopGroup();
//        EventLoopGroup workerGroup = new NioEventLoopGroup();
//        try {
//            ServerBootstrap nettyBootstrap = new ServerBootstrap();
//            nettyBootstrap.group(bossGroup, workerGroup)
//                    .channel(NioServerSocketChannel.class)
//                    .option(ChannelOption.SO_BACKLOG, 1024)
//                    .handler(loggingHandler)
//                    .childHandler(messageHandlerInitializer);
//
//            ChannelFuture future = nettyBootstrap.bind(port).sync();
//
//            future.channel().closeFuture().sync();
//        } finally {
//            bossGroup.shutdownGracefully();
//            workerGroup.shutdownGracefully();
//        }
//    }
}
