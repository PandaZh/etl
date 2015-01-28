package cc.changic.platform.etl.protocol.rmi;

/**
 * Created by Panda.Z on 2015/1/22.
 */
public class ETLMessage {

    private ETLMessageHeader header;

    private Object body;

    private ETLMessageAttachment attachment;

    public ETLMessageHeader getHeader() {
        return header;
    }

    public void setHeader(ETLMessageHeader header) {
        this.header = header;
    }

    public Object getBody() {
        return body;
    }

    public void setBody(Object body) {
        this.body = body;
    }

    public ETLMessageAttachment getAttachment() {
        return attachment;
    }

    public void setAttachment(ETLMessageAttachment attachment) {
        this.attachment = attachment;
    }
}
