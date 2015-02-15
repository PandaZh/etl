import cc.changic.platform.etl.protocol.rmi.ETLMessageHeader;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.util.AttributeKey;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.util.Date;

/**
 * Created by Panda.Z on 2015/1/22.
 */
public class BuffTest {
    @Test
    public void test() {
        try {
            ByteBuf buf = Unpooled.buffer();
            System.out.println(Integer.MAX_VALUE);
            System.out.println(buf.isDirect());
            FileInputStream in = new FileInputStream(new File("E:\\logs\\sdk\\error.2014-12-26"));
            buf.writeBytes(in, in.available());
            FileOutputStream out = new FileOutputStream(new File("E:\\logs\\sdk\\panda"));
            buf.readBytes(out, buf.writerIndex());
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void RandomFileTest() {


    }


    @Test
    public void fileChannelTest() {
        try {
            RandomAccessFile inFile = new RandomAccessFile("E:\\logs\\sdk\\error.2014-12-26", "r");
            FileChannel inChannel = inFile.getChannel();
//            FileOutputStream outputStream = new FileOutputStream(file);
//            FileChannel localfileChannel = outputStream.getChannel();
//            ByteBuffer byteBuffer = buffer.nioBuffer();
//            int written = 0;
//            while (written < size) {
//                written += localfileChannel.write(byteBuffer);
//            }
//            buffer.readerIndex(buffer.readerIndex() + written);
//            localfileChannel.force(false);
//            localfileChannel.close();
//            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Test
    public void bufTest() {
        try {

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
