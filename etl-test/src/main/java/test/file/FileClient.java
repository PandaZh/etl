package test.file;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.CharsetUtil;

/**
 * Created by Panda.Z on 2015/1/24.
 */
public class FileClient {

    public static void main(String[] args) throws Exception {
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap();
            b.group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {

                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();
//                            pipeline.addLast(new LineBasedFrameDecoder(8192));
                            pipeline.addLast(new StringEncoder(CharsetUtil.UTF_8));
                            pipeline.addLast(new FileClientHandler());
                        }
                    });

            // Make a new connection.
            ChannelFuture f = b.connect("127.0.0.1", 8023).sync();

            Channel channel = f.channel();
//            channel.writeAndFlush("C:\\Users\\Panda.Z\\Desktop\\eclipse-java-indigo-SR2-win32-x86_64.zip\n");
            channel.writeAndFlush("C:\\Users\\Panda.Z\\Desktop\\eclipse-java-indigo-SR2-win32-x86_64.zip\n");
//            channel.write("E:\\logs\\sdk\\error.2014-12-26");
        } finally {
//            group.shutdownGracefully();
        }
    }

}
