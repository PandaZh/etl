package cc.changic.platform.etl;

/**
 * Created by Panda.Z on 2015/1/17.
 */
public class NettyMessage {
    private Header header;
    private Object body;

    public NettyMessage() {

    }

    public NettyMessage(Header header, Object body) {
        this.header = header;
        this.body = body;
    }

    public Header getHeader() {
        return header;
    }

    public void setHeader(Header header) {
        this.header = header;
    }

    public Object getBody() {
        return body;
    }

    public void setBody(Object body) {
        this.body = body;
    }
}
