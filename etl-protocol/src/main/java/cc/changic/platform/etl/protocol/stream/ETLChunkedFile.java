package cc.changic.platform.etl.protocol.stream;

import cc.changic.platform.etl.protocol.rmi.ETLMessageAttachment;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.stream.ChunkedInput;

import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Not thread safe
 * Created by Panda.Z on 2015/1/26.
 */
public class ETLChunkedFile implements ChunkedInput<ByteBuf> {

    private final ByteBuf headerBuf;
    private final RandomAccessFile file;
    private final long startOffset;
    private final long endOffset;
    private final int chunkSize;
    private long offset;
    private int index;

    /**
     * @param headerBuf 附件消息报头16字节
     * @param file      附件文件
     * @throws IOException
     */
    public ETLChunkedFile(ByteBuf headerBuf, RandomAccessFile file) throws IOException {
        this(headerBuf, file, 0, file.length());
    }

    /**
     * @param headerBuf 附件消息报头16字节
     * @param file      附件文件
     * @param offset    偏移量
     * @throws IOException
     */
    public ETLChunkedFile(ByteBuf headerBuf, RandomAccessFile file, long offset, long endOffset) throws IOException {
        if (null == headerBuf) {
            throw new NullPointerException("headerBuf");
        }
        if (file == null) {
            throw new NullPointerException("file");
        }
        if (offset < 0) {
            throw new IllegalArgumentException("offset: " + offset + " (expected: 0 or greater)");
        }
        if (endOffset < 0) {
            throw new IllegalArgumentException("endOffset: " + endOffset + " (expected: 0 or greater)");
        }
        if (offset > endOffset) {
            throw new IllegalArgumentException("endOffset: " + endOffset + " must greater than offset: " + offset);
        }
        chunkSize = ChunkDataConfiguration.getChunkSize();
        this.headerBuf = headerBuf;
        this.file = file;
        this.offset = startOffset = offset;
        this.endOffset = endOffset;
        file.seek(offset);
    }

    @Override
    public boolean isEndOfInput() throws Exception {
        return !(offset < endOffset && file.getChannel().isOpen());
    }

    @Override
    public void close() throws Exception {
        file.close();
    }


    @Override
    public ByteBuf readChunk(ChannelHandlerContext ctx) throws Exception {
        long offset = this.offset;
        if (offset >= endOffset) {
            return null;
        }
        // 构建用于附件传输的报头
        ByteBuf sendHeader = buildSendHeader(ctx);
        // 计算分片实际长度
        int chunkSize = (int) Math.min(this.chunkSize - sendHeader.writerIndex(), endOffset - offset);
        // 根据分片实际长度分配写出Buff大小
        ByteBuf out = ctx.alloc().heapBuffer(chunkSize + sendHeader.writerIndex());
        // 分配文件分片大小的Buff
        ByteBuf chunkBuf = ctx.alloc().heapBuffer(chunkSize);
        boolean release = true;
        try {
            if (chunkSize < this.chunkSize - sendHeader.writerIndex()) {
                // 设置是最后一个包
                sendHeader.setBoolean(11, true);
            }
            sendHeader.setInt(21, chunkSize);
            out.writeBytes(sendHeader);

            file.readFully(chunkBuf.array(), chunkBuf.arrayOffset(), chunkSize);
            chunkBuf.writerIndex(chunkSize);
            this.offset = offset + chunkSize;
            out.writeBytes(chunkBuf);
            release = false;
            return out;
        } finally {
            if (release) {
                out.release();
            }
            chunkBuf.release();
            sendHeader.release();
        }
    }

    @Override
    public long length() {
        return endOffset - startOffset;
    }

    @Override
    public long progress() {
        return offset - startOffset;
    }

    /**
     * 构建用于附件传输的报头
     */
    private ByteBuf buildSendHeader(ChannelHandlerContext ctx) {
        // 用于写出的报头
        ByteBuf tmpHeadBuf = ctx.alloc().buffer(25);
        // 消息头
        tmpHeadBuf.writeBytes(headerBuf.copy());
        // 附件头
        // 附件类型
        tmpHeadBuf.writeByte(ETLMessageAttachment.AttachType.FILE.type());
        // 附件序号
        tmpHeadBuf.writeInt(index++);
        // 附件长度,占位
        tmpHeadBuf.writeInt(0);
        return tmpHeadBuf;
    }
}
