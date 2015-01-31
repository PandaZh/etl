package cc.changic.platform.etl.protocol.message;

import cc.changic.platform.etl.protocol.anotation.MessageToken;
import cc.changic.platform.etl.protocol.rmi.ETLMessage;
import cc.changic.platform.etl.protocol.rmi.ETLMessageAttachment;
import cc.changic.platform.etl.protocol.rmi.ETLMessageHeader;
import cc.changic.platform.etl.protocol.stream.ChunkDataConfiguration;
import cc.changic.platform.etl.protocol.stream.ETLChunkedFile;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.stream.ChunkedInput;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;

import static cc.changic.platform.etl.protocol.rmi.ETLMessageType.REQUEST;
import static cc.changic.platform.etl.protocol.rmi.ETLMessageType.RESPONSE;

/**
 * Created by Panda.Z on 2015/1/26.
 */
//@Component
//@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
//@MessageToken(id = 0x0001)
public class ExampleMessageHandler extends DuplexMessage {

    private ETLMessage message;
    private RandomAccessFile file;
    public String fileName;

//    @Autowired(required = false)
//    private ChunkDataConfiguration chunkDataConfiguration;

    @Override
    public ETLMessage getMessage() {
        return message;
    }

    @Override
    public ChunkedInput getChunkAttach(ByteBuf chunkedHead) {
        RandomAccessFile file = (RandomAccessFile) message.getAttachment().getData();
        try {
            return  new ETLChunkedFile(chunkedHead, file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void read(ChannelHandlerContext ctx, ETLMessage message) throws Exception {
        this.message = message;
        if (null != message.getBody())
            System.out.println(message.getBody().toString());
        ETLMessageHeader header = message.getHeader();
        if (header.getMessageType() == REQUEST.type()) {
            header.setMessageType(RESPONSE.type());
            if (message.getBody() instanceof String) {
                String path = (String) message.getBody();
                RandomAccessFile file = new RandomAccessFile(path, "r");
                ETLMessageAttachment attachment = new ETLMessageAttachment(ETLMessageAttachment.AttachType.FILE.type(), file);
                message.setAttachment(attachment);
            }
//            write(ctx);
        } else {
            if (null != message.getAttachment()) {
                ByteBuf buf = null;
                try {
                    buf = (ByteBuf) message.getAttachment().getData();
                    if (null == file) {
                        file = new RandomAccessFile(fileName, "rw");
                    }
                    FileChannel channel = file.getChannel();
                    channel.write(buf.nioBuffers());
                    if (header.isLastPackage()) {
                        file.close();
                    }
                } finally {
                    if (null != buf)
                        buf.release();
                }
            }
        }
    }

    @Override
    public void write(ChannelHandlerContext ctx) throws Exception {
//        ByteBuf chunkedHead = ctx.alloc().buffer(16);
//        chunkedHead.writeShort(message.getHeader().getToken());
//        chunkedHead.writeLong(message.getHeader().getSessionID());
//        chunkedHead.writeByte(message.getHeader().getMessageType());
//        // 是否是最后一个包
//        chunkedHead.writeBoolean(false);
//        // 是否有body
//        chunkedHead.writeInt(ETLMessageHeader.NO_BODY);
//        RandomAccessFile file = (RandomAccessFile) message.getAttachment().getData();
//        ctx.writeAndFlush(new ETLChunkedFile(chunkedHead,file));
        ctx.writeAndFlush(this);
    }

    public void setMessage(ETLMessage message) {
        this.message = message;
    }
}
