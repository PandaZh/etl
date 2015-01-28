package cc.changic.platform.etl.protocol.codec;

import cc.changic.platform.etl.protocol.codec.marshalling.ETLMarshallingDecoder;
import cc.changic.platform.etl.protocol.dispatcher.MessageDispatcher;
import cc.changic.platform.etl.protocol.exception.ETLException;
import cc.changic.platform.etl.protocol.rmi.ETLMessage;
import cc.changic.platform.etl.protocol.rmi.ETLMessageAttachment;
import cc.changic.platform.etl.protocol.rmi.ETLMessageHeader;
import cc.changic.platform.etl.protocol.rmi.ETLMessageType;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.EmptyByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Created by Panda.Z on 2015/1/21.
 */
public class SimpleETLProtocolDecoder extends ByteToMessageDecoder {

    private Logger logger = LoggerFactory.getLogger(SimpleETLProtocolDecoder.class);

    private ETLMarshallingDecoder marshallingDecoder;

    public SimpleETLProtocolDecoder(ETLMarshallingDecoder marshallingDecoder) {
        this.marshallingDecoder = marshallingDecoder;
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        try {
            if (in instanceof EmptyByteBuf)
                return;
            // 创建指针副本用于解码,当解码成功之后将in的writerIndex置为副本的writerIndex
            ByteBuf slice = in.slice();
            ETLMessage message = new ETLMessage();
            // 获取消息令牌,如果未匹配对应的处理类则认为是无效消息
            short token = slice.readShort();
            if (!MessageDispatcher.containsToken(token)) {
                throw new ETLException("Not support message handler token=" + token);
            }
            // 获取会话ID
            long sessionID = slice.readLong();
            // 获取消息类型,如果未匹配消息类型则认为是无效消息
            byte messageType = slice.readByte();
            if (!ETLMessageType.containsType(messageType)) {
                throw new ETLException("Not support message type typeValue=" + messageType);
            }
            // 获取是否最后一个包
            boolean isLastPackage = slice.readBoolean();
            // 构造包头
            ETLMessageHeader header = new ETLMessageHeader(token, sessionID, messageType, isLastPackage);
            message.setHeader(header);

            // decode body
            int hasBody = slice.readInt();
            if (hasBody == ETLMessageHeader.HAS_BODY) {
                Object body = marshallingDecoder.decode(ctx, slice);
                message.setBody(body);
            }

            // decode attachment
            if (slice.isReadable()) {
                byte attachType = slice.readByte();
                if (!ETLMessageAttachment.AttachType.containsType(attachType)) {
                    throw new ETLException("Not support attachment type: " + attachType);
                }
                int attachIndex = slice.readInt();
                int attachLength = slice.readInt();
                ByteBuf attachBuf = ctx.alloc().buffer(attachLength);
                slice.readBytes(attachBuf, attachLength);
                ETLMessageAttachment attachment = new ETLMessageAttachment(attachType, attachIndex, attachBuf);
                message.setAttachment(attachment);
            }
            out.add(message);
            in.readerIndex(slice.readerIndex());
        } catch (Exception e) {
            logger.error("Decode error:{}", e.getMessage(), e);
            throw e;
        }
    }
}
