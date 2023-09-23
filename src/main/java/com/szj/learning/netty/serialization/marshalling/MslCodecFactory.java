package com.szj.learning.netty.serialization.marshalling;

import io.netty.handler.codec.marshalling.DefaultMarshallerProvider;
import io.netty.handler.codec.marshalling.DefaultUnmarshallerProvider;
import io.netty.handler.codec.marshalling.MarshallingDecoder;
import io.netty.handler.codec.marshalling.MarshallingEncoder;
import org.jboss.marshalling.MarshallerFactory;
import org.jboss.marshalling.Marshalling;
import org.jboss.marshalling.MarshallingConfiguration;

/**
 * @author shenzhuojun
 * @version 1.0 2023/9/12 2:03 下午
 * @Description
 */
public class MslCodecFactory {

    // Marshalling 解码器，用于在 Netty 中解码二进制数据并将其反序列化为对象
    public static MarshallingDecoder buildMslDecoder() {
        // mslFactory 用于创建和管理 Marshaller 和 Unmarshaller 的实例。serial 实现通常是基于二进制流的，通常情况下是默认选择，适用于大多数应用。
        MarshallerFactory mslFactory = Marshalling.getProvidedMarshallerFactory("serial");
        // 用于配置 Marshalling 的行为，例如设置序列化和反序列化的版本号。在这里，将版本号设置为 5，以确保序列化和反序列化的一致性。
        MarshallingConfiguration config = new MarshallingConfiguration();
        config.setVersion(5);
        // 提供用于反序列化的 Unmarshaller 实例。它需要知道要使用的 mslFactory 和 config。
        DefaultUnmarshallerProvider provider = new DefaultUnmarshallerProvider(mslFactory, config);
        // 解码器
        return new MarshallingDecoder(provider, 1024);
    }

    public static MarshallingEncoder buildMslEncoder() {
        MarshallerFactory mslFactory = Marshalling.getProvidedMarshallerFactory("serial");
        MarshallingConfiguration config = new MarshallingConfiguration();
        config.setVersion(5);
        // // 提供用于序列化的 Marshaller 实例。
        DefaultMarshallerProvider provider = new DefaultMarshallerProvider(mslFactory, config);
        return new MarshallingEncoder(provider);
    }

}
