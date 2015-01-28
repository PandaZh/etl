package cc.changic.platform.etl.protocol.rmi;

/**
 * Created by Panda.Z on 2015/1/26.
 */
public class ETLMessageAttachment {

    private byte type;
    private int index;
    private Object data;

    public ETLMessageAttachment() {
    }

    public ETLMessageAttachment(byte type, Object data) {
        this.type = type;
        this.data = data;
    }

    public ETLMessageAttachment(byte type, int index, Object data) {
        this.type = type;
        this.index = index;
        this.data = data;
    }

    public byte getType() {
        return type;
    }

    public void setType(byte type) {
        this.type = type;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    /**
     * 附件类型
     */
    public static enum AttachType {

        /**
         * 文件附件
         */
        FILE((byte) 1),

        /**
         * SQL数据附件
         */
        SQL_DATA((byte) 2);

        private final byte type;

        AttachType(byte type) {
            this.type = type;
        }

        public byte type() {
            return type;
        }

        public static boolean containsType(byte typeValue) {
            AttachType[] values = AttachType.values();
            for (AttachType type : values) {
                if (type.type() == typeValue)
                    return true;
            }
            return false;
        }
    }
}
