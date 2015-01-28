package cc.changic.platform.etl.protocol.message;

import cc.changic.platform.etl.protocol.rmi.ETLMessage;
import io.netty.channel.ChannelHandlerContext;

/**
 * Created by Panda.Z on 2015/1/19.
 */
public interface InputMessage extends IoMessage {

    /**
     * 读取消息,有必要考虑在处理完成之后释放相关资源
     *
     * @param ctx
     * @param message
     * @throws Exception
     */
    void read(ChannelHandlerContext ctx, ETLMessage message) throws Exception;

}
