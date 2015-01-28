package cc.changic.platform.etl.protocol.rmi;

/**
 * Created by Panda.Z on 2015/1/20.
 */
public class ETLMessageHeader {

    public final static int HAS_BODY = 1;
    public final static int NO_BODY = 0;

    private short Token;
    private long sessionID;
    private byte messageType;
    private boolean lastPackage;

    public ETLMessageHeader() {
    }

    public ETLMessageHeader(short token, long sessionID, byte messageType, boolean lastPackage) {
        Token = token;
        this.sessionID = sessionID;
        this.messageType = messageType;
        this.lastPackage = lastPackage;
    }

    public short getToken() {
        return Token;
    }

    public void setToken(short token) {
        Token = token;
    }

    public long getSessionID() {
        return sessionID;
    }

    public void setSessionID(long sessionID) {
        this.sessionID = sessionID;
    }

    public byte getMessageType() {
        return messageType;
    }

    public void setMessageType(byte messageType) {
        this.messageType = messageType;
    }

    public boolean isLastPackage() {
        return lastPackage;
    }

    public void setLastPackage(boolean lastPackage) {
        this.lastPackage = lastPackage;
    }
}
