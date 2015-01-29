package cc.changic.platform.etl.protocol.stream;

/**
 * Created by Panda.Z on 2015/1/28.
 */
public class ChunkDataConfiguration {

    private static int CHUNK_SIZE = 1024 * 1024;

    public static int getChunkSize() {
        return CHUNK_SIZE;
    }

    public void setChunkSize(int chunkSize) {
        CHUNK_SIZE = chunkSize;
    }
}
