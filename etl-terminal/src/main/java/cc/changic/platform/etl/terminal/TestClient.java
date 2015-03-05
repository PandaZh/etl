package cc.changic.platform.etl.terminal;

import cc.changic.platform.etl.protocol.handler.ETLMessageHandlerInitializer;
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
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * 2.0
 * Created by Panda.Z on 2015/1/27.
 */
@Configuration
@ComponentScan(basePackages = "cc.changic.platform.etl")
public class TestClient {

    private static Logger LOGGER = LoggerFactory.getLogger(TerminalBootstrap.class);

    @Autowired
    private LoggingHandler loggingHandler;

    @Autowired
    private ETLMessageHandlerInitializer messageHandlerInitializer;

    public static void main(String[] args) {

        ConfigurableApplicationContext context = null;
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            context = new ClassPathXmlApplicationContext("classpath:terminalContext.xml");

            TestClient client = context.getBean(TestClient.class);
            client.connect(context);
        } catch (Exception e) {
            LOGGER.error("Start ETL Terminal error: {}", e.getMessage(), e);
            System.exit(-1);
        }
    }


    public void connect(ConfigurableApplicationContext context) throws Exception {
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap();
            b.group(group)
                    .channel(NioSocketChannel.class)
                    .handler(messageHandlerInitializer);
//            for (int i = 0; i < 100; i++) {
                // Make a new connection.
                ChannelFuture f = b.connect("127.0.0.1", 10000).sync();

                Channel channel = f.channel();
                ETLMessage message = new ETLMessage();
                message.setBody("C:\\Users\\Panda.Z\\Desktop\\eclipse-java-indigo-SR2-win32-x86_64.zip");
                ExampleMessageHandler handler = context.getBean(ExampleMessageHandler.class);
                handler.setMessage(message);
                handler.fileName = "E:\\logs\\sdk\\test" + System.currentTimeMillis() + ".zip";
                channel.writeAndFlush(handler);
//            }

        } finally {
//            group.shutdownGracefully();
        }
    }
}
