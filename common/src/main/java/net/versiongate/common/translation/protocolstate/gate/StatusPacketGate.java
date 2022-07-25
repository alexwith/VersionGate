package net.versiongate.common.translation.protocolstate.gate;

import com.google.gson.JsonObject;
import net.versiongate.api.buffer.BufferType;
import net.versiongate.api.connection.IConnection;
import net.versiongate.api.gate.version.ProtocolVersion;
import net.versiongate.common.gate.gate.PacketGate;
import net.versiongate.common.translation.protocolstate.type.status.OutboundPacketStatus;

public class StatusPacketGate extends PacketGate {

    @Override
    public void load() {
        this.packetConsumer(OutboundPacketStatus.STATUS_RESPONSE, (packet) -> {
            packet.schema(
                BufferType.JSON
            );

            final IConnection connection = packet.getConnection();
            final JsonObject response = packet.getField(0);
            final JsonObject version = response.getAsJsonObject("version");
            final int serverProtocol = version.get("protocol").getAsInt();
            final int connectionProtocol = connection.getProtocolVersion().getId();

            if (!this.gateManager.hasProtocolVersion()) {
                this.gateManager.setProtocolVersion(ProtocolVersion.getClosest(serverProtocol));
            }

            if (serverProtocol == connectionProtocol) {
                return;
            }

            version.addProperty("protocol", connection.getProtocolVersion().getId());

            packet.setField(0, response);
        });
    }
}
