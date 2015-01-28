package cc.changic.platform.etl.protocol.codec;

import cc.changic.platform.etl.protocol.dispatcher.MessageDispatcher;
import cc.changic.platform.etl.protocol.exception.ETLException;
import cc.changic.platform.etl.protocol.message.DuplexMessage;
import cc.changic.platform.etl.protocol.rmi.ETLMessage;
import cc.changic.platform.etl.protocol.rmi.ETLMessageHeader;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static cc.changic.platform.etl.protocol.rmi.ETLMessageType.REQUEST;
import static cc.changic.platform.etl.protocol.rmi.ETLMessageType.RESPONSE;

/**
 * Created by Panda.Z on 2015/1/22.
 */
@Component
@ChannelHandler.Sharable
public class ETLMessageHandler extends SimpleChannelInboundHandler<ETLMessage> {

    @Autowired
    private MessageDispatcher dispatcher;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ETLMessage message) throws Exception {
        if (null == message) {
            throw new NullPointerException("Received message is null");
        }
        if (null == message.getHeader()) {
            throw new NullPointerException("Received message's header is null");
        }
        if (null == message.getBody() && null == message.getAttachment()) {
            throw new NullPointerException("Received message's body or attachment is null");
        }
        ETLMessageHeader header = message.getHeader();
        if (header.getMessageType() == REQUEST.type()) {
            DuplexMessage handlerMessage = dispatcher.getMessage(header.getToken());
            handlerMessage.read(ctx, message);
            handlerMessage.write(ctx);
        } else if (header.getMessageType() == RESPONSE.type()) {
            DuplexMessage handlerMessage = getHandlerMessage(ctx, header.getSessionID());
            handlerMessage.read(ctx, message);
        } else {
            throw new ETLException("Not support message type, typeValue=" + header.getMessageType());
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
        ctx.close();
    }

    private DuplexMessage getHandlerMessage(ChannelHandlerContext ctx, Long sessionID){
        AttributeKey<DuplexMessage> attributeKey = AttributeKey.valueOf(sessionID.toString());
        Attribute<DuplexMessage> attribute = ctx.attr(attributeKey);
        DuplexMessage duplexMessage = attribute.get();
        return duplexMessage;
    }
}
