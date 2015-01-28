package cc.changic.platform.etl.protocol.codec.marshalling;

import io.netty.handler.codec.marshalling.DefaultMarshallerProvider;
import io.netty.handler.codec.marshalling.DefaultUnmarshallerProvider;
import io.netty.handler.codec.marshalling.MarshallerProvider;
import io.netty.handler.codec.marshalling.UnmarshallerProvider;
import org.jboss.marshalling.MarshallerFactory;
import org.jboss.marshalling.Marshalling;
import org.jboss.marshalling.MarshallingConfiguration;
import org.springframework.stereotype.Component;

/**
 * Created by Panda.Z on 2015/1/17.
 */
public class MarshallingCodecFactory {

    private String factoryName;
    private int version;
    private int maxObjectSize;

    public MarshallingCodecFactory(String factoryName, int version, int maxObjectSize) {
        this.factoryName = factoryName;
        this.version = version;
        this.maxObjectSize = maxObjectSize;
    }

    public ETLMarshallingDecoder buildMarshallingDecoder() {
        MarshallerFactory marshallerFactory = Marshalling.getProvidedMarshallerFactory(factoryName);
        MarshallingConfiguration configuration = new MarshallingConfiguration();
        configuration.setVersion(version);
        UnmarshallerProvider provider = new DefaultUnmarshallerProvider(marshallerFactory, configuration);
        ETLMarshallingDecoder decoder = new ETLMarshallingDecoder(provider, maxObjectSize);
        return decoder;
    }

    public ETLMarshallingEncoder buildMarshallingEncoder() {
        MarshallerFactory marshallerFactory = Marshalling.getProvidedMarshallerFactory(factoryName);
        MarshallingConfiguration configuration = new MarshallingConfiguration();
        configuration.setVersion(version);
        MarshallerProvider provider = new DefaultMarshallerProvider(marshallerFactory, configuration);
        ETLMarshallingEncoder encoder = new ETLMarshallingEncoder(provider);
        return encoder;
    }
}
