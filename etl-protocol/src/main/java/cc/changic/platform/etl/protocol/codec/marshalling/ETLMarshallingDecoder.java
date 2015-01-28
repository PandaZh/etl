package cc.changic.platform.etl.protocol.codec.marshalling;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.marshalling.MarshallingDecoder;
import io.netty.handler.codec.marshalling.UnmarshallerProvider;

/**
 * Created by Panda.Z on 2015/1/17.
 */
public class ETLMarshallingDecoder extends MarshallingDecoder {

    public ETLMarshallingDecoder(UnmarshallerProvider provider) {
        super(provider);
    }

    public ETLMarshallingDecoder(UnmarshallerProvider provider, int maxObjectSize) {
        super(provider, maxObjectSize);
    }

    public Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
        return super.decode(ctx, in);
    }
}
