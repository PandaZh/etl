package cc.changic.platform.etl.protocol.handler;

import cc.changic.platform.etl.protocol.codec.ETLMessageHandler;
import cc.changic.platform.etl.protocol.codec.SimpleETLProtocolDecoder;
import cc.changic.platform.etl.protocol.codec.SimpleETLProtocolEncoder;
import cc.changic.platform.etl.protocol.codec.marshalling.MarshallingCodecFactory;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by Panda.Z on 2015/1/20.
 */
@Component
public class ETLMessageHandlerInitializer extends ChannelInitializer<SocketChannel> {

    @Autowired
    private MarshallingCodecFactory marshallingCodecFactory;
    @Autowired
    private ETLMessageHandler messageHandler;
//    @Autowired
//    private LoggingHandler loggingHandler;
    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();

//        pipeline.addLast(loggingHandler);

        pipeline.addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4, 0, 4));
        pipeline.addLast(new LengthFieldPrepender(4));

        pipeline.addLast(new ChunkedWriteHandler());

        pipeline.addLast(new SimpleETLProtocolDecoder(marshallingCodecFactory.buildMarshallingDecoder()));
        pipeline.addLast(new SimpleETLProtocolEncoder(marshallingCodecFactory.buildMarshallingEncoder()));

        pipeline.addLast(messageHandler);
    }
}
