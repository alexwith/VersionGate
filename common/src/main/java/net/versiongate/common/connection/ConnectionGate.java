package net.versiongate.common.connection;

import java.util.List;
import net.versiongate.api.connection.IConnection;
import net.versiongate.api.connection.IConnectionGate;
import net.versiongate.api.gate.IGate;
import net.versiongate.api.gate.IGateManager;
import net.versiongate.api.gate.gate.IPacketGate;
import net.versiongate.api.gate.version.ProtocolVersion;
import net.versiongate.api.packet.IPacket;
import net.versiongate.common.platform.Platform;
import org.eclipse.collections.impl.list.mutable.FastList;

public class ConnectionGate implements IConnectionGate {
    private final IConnection connection;
    private final List<IGate> gates = FastList.newList();

    public ConnectionGate(IConnection connection) {
        this.connection = connection;

        this.loadRelevantGates();
    }

    @Override
    public void addGate(IGate gate) {
        this.gates.add(gate);
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
    }

    private void loadRelevantGates() {
        final IGateManager gateManager = Platform.get().getGateManager();
        this.gates.addAll(gateManager.getHandshakingGates());

        final ProtocolVersion protocolVersion = this.connection.getProtocolVersion();
        System.out.println("bob: " + protocolVersion);
    }
}
