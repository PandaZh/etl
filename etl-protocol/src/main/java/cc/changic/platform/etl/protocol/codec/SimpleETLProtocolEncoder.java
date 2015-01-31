package cc.changic.platform.etl.protocol.codec;

import cc.changic.platform.etl.protocol.anotation.MessageToken;
import cc.changic.platform.etl.protocol.codec.marshalling.ETLMarshallingEncoder;
import cc.changic.platform.etl.protocol.message.DuplexMessage;
import cc.changic.platform.etl.protocol.message.OutputMessage;
import cc.changic.platform.etl.protocol.rmi.ETLMessage;
import cc.changic.platform.etl.protocol.rmi.ETLMessageHeader;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import io.netty.handler.stream.ChunkedInput;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static cc.changic.platform.etl.protocol.rmi.ETLMessageType.REQUEST;

/**
 * Created by Panda.Z on 2015/1/21.
 */
public class SimpleETLProtocolEncoder extends MessageToMessageEncoder<DuplexMessage> {

    private Logger logger = LoggerFactory.getLogger(SimpleETLProtocolEncoder.class);

    private ETLMarshallingEncoder marshallingEncoder;

    public SimpleETLProtocolEncoder(ETLMarshallingEncoder marshallingEncoder) {
        this.marshallingEncoder = marshallingEncoder;
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, DuplexMessage msg, List<Object> out) throws Exception {
        if (null == msg)
            throw new NullPointerException("OutputMessage is null");
        if (null == msg.getMessage())
            throw new NullPointerException("ETLMessage is null");
        if (null == msg.getMessage().getHeader())
            msg.getMessage().setHeader(new ETLMessageHeader());

        ETLMessage message = msg.getMessage();
        ETLMessageHeader header = message.getHeader();

        // 如果是第一次请求消息,构造消息头
        if (header.getMessageType() == REQUEST.type()) {
            MessageToken messageToken = lookupMessageToken(msg);
            header.setToken(messageToken.id());
            Long sessionID = getSessionID(ctx, msg);
            header.setSessionID(sessionID);
        }
        ByteBuf outBuf = null;
        try {
            if (null != message.getBody()) {
                // 消息头
                outBuf = ctx.alloc().buffer(2048);
                outBuf.writeShort(header.getToken());
                outBuf.writeLong(header.getSessionID());
                outBuf.writeByte(header.getMessageType());
                // 是否是最后一个包,如果有附件,至少会分成两个包发送
                if (null == message.getAttachment())
                    outBuf.writeBoolean(true);
                else
                    outBuf.writeBoolean(false);

                // 消息体
                if (null != message.getBody()) {
                    outBuf.writeInt(ETLMessageHeader.HAS_BODY);
                    marshallingEncoder.encode(ctx, message.getBody(), outBuf);
                } else {
                    outBuf.writeInt(ETLMessageHeader.NO_BODY);
                }
                out.add(outBuf);
            }
        } catch (Exception e) {
            logger.error("Encode body error:{}", e.getMessage(), e);
            throw e;
        }
        ByteBuf chunkHeader = null;
        try {
            chunkHeader = ctx.alloc().buffer(16);
            chunkHeader.writeShort(header.getToken());
            chunkHeader.writeLong(header.getSessionID());
            chunkHeader.writeByte(header.getMessageType());
            // 是否是最后一个包
            chunkHeader.writeBoolean(false);
            // 是否有body,分片附件中不包含消息体,只包含消息头
            chunkHeader.writeInt(ETLMessageHeader.NO_BODY);
            // 构造分片附件
            ChunkedInput chunkAttach = msg.getChunkAttach(chunkHeader);
            if (null != chunkAttach) {
                out.add(chunkAttach);
            } else {
                chunkHeader.release();
            }
        } catch (Exception e) {
            if (null != chunkHeader)
                chunkHeader.release();
            if (null != outBuf)
                outBuf.setBoolean(11, true);
            logger.error("Encode attachment error:{}", e.getMessage(), e);
            throw e;
        }
    }

    private long getSessionID(ChannelHandlerContext ctx, DuplexMessage msg) {
        Long sessionID;
        while (true) {
            sessionID = System.currentTimeMillis();
            AttributeKey<DuplexMessage> attributeKey = AttributeKey.valueOf(sessionID.toString());
            Attribute<DuplexMessage> attribute = ctx.attr(attributeKey);
            if (null == attribute.get()) {
                attribute.set(msg);
                break;
            }
        }
        return sessionID;
    }

    private MessageToken lookupMessageToken(OutputMessage message) {
        MessageToken messageToken = message.getClass().getAnnotation(MessageToken.class);
        if (null == messageToken)
            throw new IllegalArgumentException("Not found @MessageToken on class[" + message.getClass() + "]");
        return messageToken;
    }
}
