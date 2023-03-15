package engine.server.network.packet;

import engine.registry.Registrable;

import java.lang.reflect.InvocationTargetException;

public class PacketProvider extends Registrable.Impl<PacketProvider> {
    private final Class<? extends Packet> packetType;
    private final PacketFactory factory;

    public PacketProvider(Class<? extends Packet> type, PacketFactory factory) {
        this.packetType = type;
        this.factory = factory;
    }

    public Class<? extends Packet> getPacketType() {
        return packetType;
    }

    public Packet create() {
        return factory.create();
    }

    public static final class Builder {
        private Class<? extends Packet> packetType;
        private PacketFactory factory;
        private String registeredName;

        public Builder type(Class<? extends Packet> packetType) {
            this.packetType = packetType;
            return this;
        }

        public Builder factory(PacketFactory factory) {
            this.factory = factory;
            return this;
        }

        public Builder name(String registeredName) {
            this.registeredName = registeredName;
            return this;
        }

        public PacketProvider build() {
            if (factory == null) {
                factory = () -> {
                    try {
                        return packetType.getDeclaredConstructor().newInstance();
                    } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                             NoSuchMethodException e) {
                        return null;
                    }
                };
            }
            return new PacketProvider(packetType, factory).name(registeredName);
        }
    }
}
