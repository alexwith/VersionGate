package net.versiongate.common.connection;

import java.util.List;
import java.util.Set;
import net.versiongate.api.connection.IConnectionGate;
import net.versiongate.api.gate.IGate;
import net.versiongate.api.gate.IGateManager;
import net.versiongate.api.gate.gate.IPacketGate;
import net.versiongate.api.gate.version.ProtocolVersion;
import net.versiongate.api.packet.IPacket;
import net.versiongate.api.packet.IPacketType;
import net.versiongate.common.gate.GateType;
import net.versiongate.common.platform.Platform;
import org.eclipse.collections.impl.list.mutable.FastList;

public class ConnectionGate implements IConnectionGate {
    private final List<IGate> gates = FastList.newList();

    public ConnectionGate() {
        this.loadRelevantGates(null);
    }

    @Override
    public void translatePacket(IPacket packet) {
        for (final IGate gate : this.gates) {
            if (!(gate instanceof IPacketGate)) {
                continue;
            }

            final IPacketGate packetGate = (IPacketGate) gate;
            packetGate.translate(packet);
        }

        final IPacketType mappedPacketType = GateType.VERSION1_8.getMappedPacketType(packet.getType()); // TODO figure out how we're gonna do this properly
        if (mappedPacketType != null) {
            packet.setType(mappedPacketType);
        }
    }

    @Override
    public void onSetProtocolVersion(ProtocolVersion protocolVersion) {
        this.gates.clear();

        this.loadRelevantGates(ProtocolVersion.VERSION1_8); // TODO figure out how we're gonna do this properly
    }

    private void loadRelevantGates(ProtocolVersion protocolVersion) {
        final IGateManager gateManager = Platform.get().getGateManager();
        this.gates.addAll(gateManager.getHandshakingGates());
        if (protocolVersion == null) {
            return;
        }

        final Set<IGate> versionGates = gateManager.getVersionGates(protocolVersion);
        if (versionGates == null) {
            throw new IllegalStateException("Something has gone very wrong, as the version gates are null");
        }

        this.gates.addAll(versionGates);
    }
}
