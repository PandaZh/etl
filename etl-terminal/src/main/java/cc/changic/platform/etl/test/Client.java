//package cc.changic.platform.etl.test;
//
//import cc.changic.platform.etl.file.proto.SimpleMessage;
//import io.netty.bootstrap.Bootstrap;
//import io.netty.channel.*;
//import io.netty.channel.nio.NioEventLoopGroup;
//import io.netty.channel.socket.SocketChannel;
//import io.netty.channel.socket.nio.NioSocketChannel;
//import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
//import io.netty.handler.codec.LengthFieldPrepender;
//import io.netty.handler.codec.protobuf.ProtobufDecoder;
//import io.netty.handler.codec.protobuf.ProtobufEncoder;
//import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
//import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;
//
///**
// * @author Panda.Z
// */
//public class Client {
//
//    public static void main(String[] args) {
//        try {
//            Client client = new Client();
//            client.connect();
//        } catch (Exception e) {
//           e.printStackTrace();
//        }
//    }
//
//    public void connect() throws Exception {
//        EventLoopGroup group = new NioEventLoopGroup();
//        try {
//            Bootstrap b = new Bootstrap();
//            b.group(group)
//                    .channel(NioSocketChannel.class)
//                    .handler(new ChannelInitializer<SocketChannel>() {
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
//                                    System.out.println("client" + msg.getId() + ":" + msg.getName());
//                                }
//                            });
//                        }
//                    });
////            for (int i = 0; i < 100; i++) {
//            // Make a new connection.
//            ChannelFuture f = b.connect("127.0.0.1", 5555).sync();
//
//            Channel channel = f.channel();
//            SimpleMessage.Message.Builder builder = SimpleMessage.Message.newBuilder();
//            SimpleMessage.Message message = builder.setId(1).setName("panda").build();
//            channel.writeAndFlush(message);
////            }
//
//        } finally {
////            group.shutdownGracefully();
//        }
//    }
//}
