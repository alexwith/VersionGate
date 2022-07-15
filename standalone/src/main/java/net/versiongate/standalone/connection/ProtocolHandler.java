package net.versiongate.standalone.connection;

import java.nio.ByteBuffer;
import net.versiongate.standalone.enums.State;
import net.versiongate.standalone.util.ProtocolUtils;

public interface ProtocolHandler {

    ProtocolHandler CLIENT = (context, packetId, payload) -> {
        final var state = context.getState();
        if (state == State.HANDSHAKE && packetId == 0) {
            // we need to read the whole handshake to get the correct values
            final int protocol = ProtocolUtils.readVarInt(payload);
            final String address = ProtocolUtils.readString(payload, 255);
            final short port = payload.getShort();
            final int stateId = ProtocolUtils.readVarInt(payload);
            final State nextState = switch (stateId) {
                case 1 -> State.STATUS;
                case 2 -> State.LOGIN;
                default -> throw new IllegalStateException("Unexpected value: " + stateId);
            };
            context.setState(nextState);
            context.getTargetContext().setState(nextState);
        } else if (state == State.LOGIN && packetId == 0) {
            context.setState(State.PLAY);
        }
    };
    ProtocolHandler SERVER = (context, packetId, payload) -> {
        final State state = context.getState();
        if (state == State.LOGIN) {
            if (packetId == 2) {
                context.setState(State.PLAY);
            } else if (packetId == 3) {
                final int threshold = ProtocolUtils.readVarInt(payload);
                context.setCompression(threshold);
                context.getTargetContext().setCompression(threshold);
            }
            context.setState(State.PLAY);
        }
    };

    void process(ConnectionContext context, int packetId, ByteBuffer payload);
}
