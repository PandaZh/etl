import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import org.junit.Test;

/**
 * Created by Panda.Z on 2015/1/19.
 */
public class BuffTest {

    @Test
    public void test(){
        ByteBuf buf = ByteBufAllocator.DEFAULT.directBuffer(4, 16);
        buf.writeInt(1);
        buf.writeInt(2);
        buf.writeInt(3);
        buf.writeInt(4);
//        buf.writeInt(5);
        while ((buf.isReadable())){
            System.out.println(buf.readInt());
        }

        System.out.println(Integer.MAX_VALUE);
        System.out.println(Integer.MAX_VALUE / 1024);
        System.out.println(Integer.MAX_VALUE / (1024 * 1024));

        System.out.println(Integer.MAX_VALUE / 1024);
    }
}
