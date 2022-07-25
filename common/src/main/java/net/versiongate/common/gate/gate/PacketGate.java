package net.versiongate.common.gate.gate;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import net.versiongate.api.gate.IGateManager;
import net.versiongate.api.gate.application.IPacketConsumer;
import net.versiongate.api.gate.gate.IPacketGate;
import net.versiongate.api.packet.IPacket;
import net.versiongate.api.packet.IPacketType;
import net.versiongate.common.platform.Platform;

public abstract class PacketGate implements IPacketGate {
    protected final IGateManager gateManager;

    private final Map<IPacketType, IPacketConsumer> packetConsumers = new ConcurrentHashMap<>();

    public PacketGate() {
        this.gateManager = Platform.get().getGateManager();
    }

    public void packetConsumer(IPacketType packetType, IPacketConsumer consumer) {
        this.packetConsumers.put(packetType, consumer);
    }

    @Override
    public void translate(IPacket packet) {
        final IPacketConsumer consumer = this.packetConsumers.get(packet.getType());
        if (consumer == null) {
            return;
        }

        try {
            consumer.consume(packet);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
