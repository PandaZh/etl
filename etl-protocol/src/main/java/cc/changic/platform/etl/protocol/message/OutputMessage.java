package cc.changic.platform.etl.protocol.message;

import cc.changic.platform.etl.protocol.rmi.ETLMessage;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.stream.ChunkedInput;

/**
 * Created by Panda.Z on 2015/1/19.
 */
public interface OutputMessage extends IoMessage {

    /**
     * 获取写出消息
     * @return
     */
    ETLMessage getMessage();

    /**
     * 构造分片附件
     * @param chunkHeader
     * @param <T>
     * @return
     */
    <T extends ChunkedInput> T getAttach(ByteBuf chunkHeader);

    /**
     * 写消息
     * @param ctx
     * @throws Exception
     */
    void write(ChannelHandlerContext ctx) throws Exception;

}
