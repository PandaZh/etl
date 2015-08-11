//package cc.changic.platform.etl.test;
//
//import cc.changic.platform.etl.file.proto.SimpleMessage;
//import io.netty.bootstrap.ServerBootstrap;
//import io.netty.channel.*;
//import io.netty.channel.nio.NioEventLoopGroup;
//import io.netty.channel.socket.SocketChannel;
//import io.netty.channel.socket.nio.NioServerSocketChannel;
//import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
//import io.netty.handler.codec.LengthFieldPrepender;
//import io.netty.handler.codec.protobuf.ProtobufDecoder;
//import io.netty.handler.codec.protobuf.ProtobufEncoder;
//import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
//import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;
//import io.netty.handler.logging.LogLevel;
//import io.netty.handler.logging.LoggingHandler;
//
///**
// * @author Panda.Z
// */
//public class Server {
//    public static void main(String[] args) {
//        EventLoopGroup bossGroup = new NioEventLoopGroup();
//        EventLoopGroup workerGroup = new NioEventLoopGroup();
//        try {
//            ServerBootstrap nettyBootstrap = new ServerBootstrap();
//            nettyBootstrap.group(bossGroup, workerGroup)
//                    .channel(NioServerSocketChannel.class)
//                    .option(ChannelOption.SO_BACKLOG, 1024)
//                    .handler(new LoggingHandler(LogLevel.INFO))
//                    .childHandler(new ChannelInitializer<SocketChannel>() {
//                        @Override
//                        protected void initChannel(SocketChannel ch) throws Exception {
//                            ChannelPipeline pipeline = ch.pipeline();
//                            pipeline.addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4, 0, 4));
//                            pipeline.addLast(new LengthFieldPrepender(4));
//                            pipeline.addLast(new ProtobufVarint32FrameDecoder());
//                            pipeline.addLast(new ProtobufDecoder(SimpleMessage.Message.getDefaultInstance()));
//                            pipeline.addLast(new ProtobufVarint32LengthFieldPrepender());
//                            pipeline.addLast(new ProtobufEncoder());
//                            pipeline.addLast(new SimpleChannelInboundHandler<SimpleMessage.Message>() {
//                                @Override
//                                protected void channelRead0(ChannelHandlerContext ctx, SimpleMessage.Message msg) throws Exception {
//                                    System.out.println(msg.getId() + ":" + msg.getName());
//                                }
//                            });
//                        }
//                    });
//            ChannelFuture future = nettyBootstrap.bind(5555).sync();
//
//            future.channel().closeFuture().sync();
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            bossGroup.shutdownGracefully();
//            workerGroup.shutdownGracefully();
//        }
//
//    }
//}
