package test.file;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

/**
 * Created by Panda.Z on 2015/1/24.
 */
public class FileClientHandler extends ByteToMessageDecoder {

    static FileOutputStream outout = null;

    int i = 0;
    AttributeKey<FileClient> attributeKey = AttributeKey.valueOf("test");
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
//        ctx.write("E:\\logs\\sdk\\error.2014-12-2");
        if(i == 0){
            i++;
            Attribute<FileClient> attribute = ctx.attr(attributeKey);
            attribute.set(new FileClient());
        }
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {

        System.out.println("第" + ctx.attr(attributeKey) + "次请求:");
        if(null == outout){
            outout = new FileOutputStream(new File("E:\\logs\\sdk\\eclipse-java-indigo-SR2-win32-x86_64.zip"));
        }
        in.readBytes(outout, in.writerIndex());
        outout.flush();
    }

}
