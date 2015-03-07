package cc.changic.platform.etl.protocol.message;

/**
 * 继承该类的子类应当考虑自身的线程安全性,建议不使用单例模式<br>
 * Created by Panda.Z on 2015/1/19.
 */
public abstract class DuplexMessage implements InputMessage, OutputMessage {
    public abstract void handlerNettyException();
}
