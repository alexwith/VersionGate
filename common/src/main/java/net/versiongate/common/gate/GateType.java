package net.versiongate.common.gate;

import java.util.Set;
import net.versiongate.api.enums.PacketBound;
import net.versiongate.api.gate.version.ProtocolVersion;
import net.versiongate.api.packet.IPacketType;
import net.versiongate.common.translation.protocolstate.type.handshaking.InboundPacketHandshaking;
import net.versiongate.common.translation.protocolstate.type.login.InboundPacketLogin;
import net.versiongate.common.translation.protocolstate.type.login.OutboundPacketLogin;
import net.versiongate.common.translation.protocolstate.type.status.InboundPacketStatus;
import net.versiongate.common.translation.protocolstate.type.status.OutboundPacketStatus;
import net.versiongate.common.translation.version1_8.type.InboundPacket1_8;
import net.versiongate.common.translation.version1_8.type.OutboundPacket1_8;
import net.versiongate.common.translation.version1_9.type.InboundPacket1_9;
import net.versiongate.common.translation.version1_9.type.OutboundPacket1_9;
import org.eclipse.collections.impl.set.mutable.UnifiedSet;

public enum GateType {

    COMMON(
        null,
        InboundPacketHandshaking.class,
        InboundPacketLogin.class,
        OutboundPacketLogin.class,
        InboundPacketStatus.class,
        OutboundPacketStatus.class
    ),
    VERSION1_8(
        ProtocolVersion.VERSION1_8,
        InboundPacket1_8.class,
        OutboundPacket1_8.class
    ),
    VERSION1_9(
        ProtocolVersion.VERSION1_9,
        InboundPacket1_9.class,
        OutboundPacket1_9.class
    );

    private final ProtocolVersion protocolVersion;
    private final Set<IPacketType> inbound = UnifiedSet.newSet();
    private final Set<IPacketType> outbound = UnifiedSet.newSet();

    @SafeVarargs
    GateType(ProtocolVersion protocolVersion, Class<? extends IPacketType>... types) {
        this.protocolVersion = protocolVersion;

        this.computeTypes(types);
    }

    public ProtocolVersion getProtocolVersion() {
        return this.protocolVersion;
    }

    public Set<IPacketType> getInbound() {
        return this.inbound;
    }

    public Set<IPacketType> getOutbound() {
        return this.outbound;
    }

    @SafeVarargs
    private final void computeTypes(Class<? extends IPacketType>... types) {
        for (final Class<? extends IPacketType> typeClass : types) {
            for (final IPacketType type : typeClass.getEnumConstants()) {
                (type.getPacketBound() == PacketBound.IN ? this.inbound : this.outbound).add(type);
            }
        }
    }
}
