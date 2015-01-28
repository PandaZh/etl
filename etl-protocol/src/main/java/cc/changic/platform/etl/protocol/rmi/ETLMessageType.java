package cc.changic.platform.etl.protocol.rmi;


/**
 * 消息类型
 * Created by Panda.Z on 2015/1/26.
 */
public enum ETLMessageType {

    /**
     * 请求消息
     */
    REQUEST((byte) 0),

    /**
     * 响应消息
     */
    RESPONSE((byte) 1);

    private final byte type;

    ETLMessageType(byte type) {
        this.type = type;
    }

    public byte type() {
        return type;
    }

    public static boolean containsType(byte typeValue) {
        ETLMessageType[] values = ETLMessageType.values();
        for (ETLMessageType type : values) {
            if (type.type() == typeValue)
                return true;
        }
        return false;
    }
}
