package net.versiongate.common.gate;

import java.util.Map;
import java.util.Set;
import net.versiongate.api.enums.PacketBound;
import net.versiongate.api.gate.IGateType;
import net.versiongate.api.gate.version.ProtocolVersion;
import net.versiongate.api.packet.IPacketType;
import net.versiongate.common.gate.gate.ProtocolGate;
import net.versiongate.common.translation.protocolstate.ProtocolState;
import net.versiongate.common.translation.protocolstate.type.handshaking.InboundPacketHandshaking;
import net.versiongate.common.translation.protocolstate.type.login.InboundPacketLogin;
import net.versiongate.common.translation.protocolstate.type.login.OutboundPacketLogin;
import net.versiongate.common.translation.protocolstate.type.status.InboundPacketStatus;
import net.versiongate.common.translation.protocolstate.type.status.OutboundPacketStatus;
import net.versiongate.common.translation.version1_12.Version1_12;
import net.versiongate.common.translation.version1_12.type.InboundPacket1_12;
import net.versiongate.common.translation.version1_12.type.OutboundPacket1_12;
import net.versiongate.common.translation.version1_8.Version1_8;
import net.versiongate.common.translation.version1_8.type.InboundPacket1_8;
import net.versiongate.common.translation.version1_8.type.OutboundPacket1_8;
import org.eclipse.collections.impl.map.mutable.UnifiedMap;
import org.eclipse.collections.impl.set.mutable.UnifiedSet;

public enum GateType implements IGateType {

    PROTOCOL_STATE(
        null,
        new ProtocolState(),
        InboundPacketHandshaking.class,
        InboundPacketLogin.class,
        OutboundPacketLogin.class,
        InboundPacketStatus.class,
        OutboundPacketStatus.class
    ),
    VERSION1_8(
        ProtocolVersion.VERSION1_8,
        new Version1_8(),
        InboundPacket1_8.class,
        OutboundPacket1_8.class
    ),
    VERSION1_12(
        ProtocolVersion.VERSION1_12,
        new Version1_12(),
        InboundPacket1_12.class,
        OutboundPacket1_12.class
    );

    private final ProtocolVersion protocolVersion;
    private final ProtocolGate protocolGate;
    private final Set<IPacketType> inbound = UnifiedSet.newSet();
    private final Set<IPacketType> outbound = UnifiedSet.newSet();
    private final Map<IPacketType, IPacketType> mapped = UnifiedMap.newMap();

    @SafeVarargs
    GateType(ProtocolVersion protocolVersion, ProtocolGate protocolGate, Class<? extends IPacketType>... types) {
        this.protocolVersion = protocolVersion;
        this.protocolGate = protocolGate;

        this.computePacketTypes(types);
    }

    @Override
    public ProtocolVersion getProtocolVersion() {
        return this.protocolVersion;
    }

    @Override
    public ProtocolGate getProtocolGate() {
        return this.protocolGate;
    }

    @Override
    public Set<IPacketType> getInbound() {
        return this.inbound;
    }

    @Override
    public Set<IPacketType> getOutbound() {
        return this.outbound;
    }

    @Override
    public IPacketType getMappedPacketType(IPacketType packetType) {
        return this.mapped.get(packetType);
    }

    @Override
    public void mapPacketTypes() {
        final int index = this.ordinal() - 1;
        if (index < 0) {
            return;
        }

        final GateType previous = GateType.values()[index];
        if (previous.getProtocolVersion() == null) {
            return;
        }

        for (final IPacketType type : this.inbound) {
            for (final IPacketType previousType : previous.getInbound()) {
                if (!type.getName().equals(previousType.getName())) {
                    continue;
                }

                previous.mapped.put(previousType, type);
            }
        }

        for (final IPacketType type : this.outbound) {
            for (final IPacketType previousType : previous.getOutbound()) {
                if (!type.getName().equals(previousType.getName())) {
                    continue;
                }

                previous.mapped.put(previousType, type);
            }
        }
    }

    @SafeVarargs
    private final void computePacketTypes(Class<? extends IPacketType>... types) {
        for (final Class<? extends IPacketType> typeClass : types) {
            for (final IPacketType type : typeClass.getEnumConstants()) {
                (type.getPacketBound() == PacketBound.IN ? this.inbound : this.outbound).add(type);
            }
        }
    }
}
