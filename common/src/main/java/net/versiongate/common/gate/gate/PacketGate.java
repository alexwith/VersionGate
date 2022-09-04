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
    protected final Map<IPacketType, IPacketType> mappedPacketTypes = UnifiedMap.newMap();

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
        final IPacketType mappedPacketType = this.gateType.getMappedPacketType(packetType);
        this.packetConsumer(packetType, mappedPacketType, consumer);
    }

    @Override
    public void packetConsumer(IPacketType packetType, IPacketType mappedPacketType, IPacketConsumer consumer) {
        System.out.println("mapped to: " + packetType + " -> " + mappedPacketType);
        this.packetConsumers.put(packetType, consumer);

        if (mappedPacketType != null) {
            this.mappedPacketTypes.put(packetType, mappedPacketType);
        }
    }

    @Override
    public void translate(IPacket packet) {
        final IPacketConsumer consumer = this.packetConsumers.get(packet.getType());
        if (consumer == null) {
            return;
        }

        final IPacketType mapped = this.mappedPacketTypes.get(packet.getType());
        if (mapped != null) {
            packet.setType(mapped);
        }

        try {
            consumer.consume(packet);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
