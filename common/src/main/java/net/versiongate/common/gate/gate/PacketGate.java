package net.versiongate.common.gate.gate;

import java.util.Map;
import net.versiongate.api.gate.IGateManager;
import net.versiongate.api.gate.IGateType;
import net.versiongate.api.gate.application.IPacketConsumer;
import net.versiongate.api.gate.gate.IPacketGate;
import net.versiongate.api.packet.IPacket;
import net.versiongate.api.packet.IPacketType;
import net.versiongate.common.platform.Platform;
import org.eclipse.collections.impl.map.mutable.UnifiedMap;

public abstract class PacketGate implements IPacketGate {
    protected final IGateManager gateManager;
    protected final Map<IPacketType, IPacketConsumer> packetConsumers = UnifiedMap.newMap();

    protected IGateType gateType;

    public PacketGate() {
        this.gateManager = Platform.get().getGateManager();
    }

    @Override
    public IGateType getGateType() {
        return this.gateType;
    }

    @Override
    public void setGateType(IGateType gateType) {
        this.gateType = gateType;
    }

    @Override
    public void packetConsumer(IPacketType packetType, IPacketConsumer consumer) {
        this.packetConsumers.put(packetType, consumer);
    }

    @Override
    public void translate(IPacket packet) {
        final IPacketType packetType = packet.getType();
        final IPacketType mappedPacketType = this.gateType.getMappedPacketType(packetType);
        System.out.println("find mapped packet: " + packetType + " -> " + mappedPacketType + " -> " + this.gateType.getClass());
        if (mappedPacketType != null) {
            System.out.println("found: " + mappedPacketType.getId());
            packet.setType(mappedPacketType);
        }

        final IPacketConsumer consumer = this.packetConsumers.get(packetType);
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
