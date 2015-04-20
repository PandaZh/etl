package cc.changic.platform.etl.protocol.codec;

import cc.changic.platform.etl.protocol.dispatcher.MessageDispatcher;
import cc.changic.platform.etl.protocol.exception.ETLException;
import cc.changic.platform.etl.protocol.message.DuplexMessage;
import cc.changic.platform.etl.protocol.rmi.ETLMessage;
import cc.changic.platform.etl.protocol.rmi.ETLMessageHeader;
import cc.changic.platform.etl.protocol.rmi.ETLMessageType;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static cc.changic.platform.etl.protocol.rmi.ETLMessageType.REQUEST;
import static cc.changic.platform.etl.protocol.rmi.ETLMessageType.RESPONSE;


@Component
@ChannelHandler.Sharable
public class ETLMessageHandler extends SimpleChannelInboundHandler<ETLMessage> {

    private Logger logger = LoggerFactory.getLogger(ETLMessageHandler.class);

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
            message.getHeader().setMessageType(ETLMessageType.RESPONSE.type());
            handlerMessage.write(ctx);
        } else if (header.getMessageType() == RESPONSE.type()) {
            DuplexMessage handlerMessage = getHandlerMessage(ctx, header.getSessionID());
            if (null != handlerMessage) {
                handlerMessage.read(ctx, message);
                if (message.getHeader().isLastPackage())
                    ctx.close().sync();
            } else {
                throw new ETLException("Not found handler message in a response message sessionID=" + header.getSessionID());
            }
        } else {
            throw new ETLException("Not support message type, typeValue=" + header.getMessageType());
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close().sync();
        logger.error("Netty exception: {}", cause.getMessage(), cause);
        DuplexMessage handlerMessage = getHandlerMessage(ctx, null);
        if (null != handlerMessage){
            handlerMessage.handlerNettyException(cause.getMessage());
        }else{
            logger.error("Caught netty exception, but no handler message found");
        }
    }

    private DuplexMessage getHandlerMessage(ChannelHandlerContext ctx, Long sessionID) {
        AttributeKey<DuplexMessage> attributeKey = AttributeKey.valueOf("handlerMessage");
//        AttributeKey<DuplexMessage> attributeKey = AttributeKey.valueOf(sessionID.toString());
        Attribute<DuplexMessage> attribute = ctx.attr(attributeKey);
        return attribute.get();
    }
}
