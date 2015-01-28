package cc.changic.platform.etl.protocol.stream;

/**
 * Created by Panda.Z on 2015/1/28.
 */
public class ChunkDataConfiguration {

    private int chunkSize = 1024 * 1024;

    public int getChunkSize() {
        return chunkSize;
    }

    public void setChunkSize(int chunkSize) {
        this.chunkSize = chunkSize;
    }
}
