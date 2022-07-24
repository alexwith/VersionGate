package net.versiongate.common.connection;

import java.util.List;
import net.versiongate.api.connection.IConnectionGate;
import net.versiongate.api.gate.IGate;
import net.versiongate.api.gate.gate.IPacketGate;
import net.versiongate.api.packet.IPacket;
import net.versiongate.common.gate.GateManager;
import org.eclipse.collections.impl.list.mutable.FastList;

public class ConnectionGate implements IConnectionGate {
    private final List<IGate> gates = FastList.newList();

    public ConnectionGate() {
        this.gates.addAll(GateManager.HANDSHAKING_GATES);
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
}
