package cc.changic.platform.etl.protocol.dispatcher;

import cc.changic.platform.etl.protocol.anotation.MessageToken;
import cc.changic.platform.etl.protocol.message.DuplexMessage;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import java.util.Map;

/**
 * Created by Panda.Z on 2015/1/19.
 */
public class MessageDispatcher {

    private Logger logger = LoggerFactory.getLogger(MessageDispatcher.class);

    private final static Map<Short, Class<? extends DuplexMessage>> MESSAGES = Maps.newHashMap();

    @Autowired
    private ApplicationContext applicationContext;

    public void init() {
        try {
            logger.info("===========================    Loading messages    =============================");
            Map<String, Object> beans = applicationContext.getBeansWithAnnotation(MessageToken.class);
            for (Object bean : beans.values()) {
                if (!DuplexMessage.class.isAssignableFrom(bean.getClass())) {
                    StringBuffer error = new StringBuffer("Load messages error:").append(bean.getClass());
                    error.append(" annotated ").append(MessageToken.class);
                    error.append(" but not extends ").append(DuplexMessage.class);
                    error.append(" or ").append(DuplexMessage.class).append(" child class");
                    throw new RuntimeException(error.toString());
                }

                DuplexMessage message = (DuplexMessage) bean;
                MessageToken messageToken = message.getClass().getAnnotation(MessageToken.class);
                if (MESSAGES.containsKey(messageToken.id())) {
                    Class<? extends DuplexMessage> mappedClass = MESSAGES.get(messageToken.id());
                    StringBuffer error = new StringBuffer("Load messages error");
                    error.append(" duplicate messages found [id=0x").append(Integer.toHexString(messageToken.id()));
                    error.append(", class=").append(message.getClass()).append("]");
                    error.append(" and [id=0x").append(Integer.toHexString(messageToken.id()));
                    error.append(", class=").append(mappedClass).append("]");
                    throw new RuntimeException(error.toString());
                }
                MESSAGES.put(messageToken.id(), message.getClass());
                logger.info("Mapping message [id=0x{}] ==> {}", Integer.toHexString(messageToken.id()), message.getClass());
            }
            logger.info("=========================== Load messages finished =============================");
        } catch (Exception e) {
            logger.info("Load messages error:{}", e.getMessage(), e);
            throw e;
        }
    }

    public void cleanup() {
        MESSAGES.clear();
        logger.info("Message dispatcher is destroyed");
    }

    public DuplexMessage getMessage(Short token) {
        Class<? extends DuplexMessage> clazz = MESSAGES.get(token);
        if (null == clazz)
            return null;
        return applicationContext.getBean(clazz);
    }

    public static boolean containsToken(Short token){
        return MESSAGES.containsKey(token);
    }
}
