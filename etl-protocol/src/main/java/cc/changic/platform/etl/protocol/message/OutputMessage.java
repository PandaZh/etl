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
     * 设置写出的消息
     * @param message 需要写出的消息
     */
    void setMessage(ETLMessage message);

    /**
     * 构造分片附件
     * @param chunkHeader
     * @return
     */
    ChunkedInput getChunkAttach(ByteBuf chunkHeader);

    /**
     * 写消息
     * @param ctx
     * @throws Exception
     */
    void write(ChannelHandlerContext ctx) throws Exception;

}
