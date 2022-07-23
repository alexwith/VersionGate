package net.versiongate.common.packet;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import net.versiongate.api.packet.IPacket;
import net.versiongate.api.packet.IPacketGate;
import net.versiongate.api.translation.IPacketType;

public abstract class PacketGate implements IPacketGate {
    private final Map<IPacketType, Consumer<IPacket>> packetTranslators = new ConcurrentHashMap<>();

    public void packetTranslation(IPacketType packetType, Consumer<IPacket> consumer) {
        this.packetTranslators.put(packetType, consumer);
    }

    public void translate(IPacketType packetType, IPacket packet) {
        final Consumer<IPacket> translator = this.packetTranslators.get(packetType);
        if (translator == null) {
            return;
        }

        translator.accept(packet);
    }
}
