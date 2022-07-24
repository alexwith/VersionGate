package net.versiongate.common.gate.gate;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import net.versiongate.api.gate.gate.IPacketGate;
import net.versiongate.api.gate.application.IPacketConsumer;
import net.versiongate.api.packet.IPacket;
import net.versiongate.api.packet.IPacketType;

public abstract class PacketGate implements IPacketGate {
    private final Map<IPacketType, IPacketConsumer> packetConsumers = new ConcurrentHashMap<>();

    public void packetConsumer(IPacketType packetType, IPacketConsumer consumer) {
        this.packetConsumers.put(packetType, consumer);
    }

    @Override
    public void translate(IPacket packet) {
        final IPacketConsumer translator = this.packetConsumers.get(packet.getType());
        if (translator == null) {
            return;
        }

        try {
            translator.consume(packet);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
