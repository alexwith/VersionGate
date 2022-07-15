package net.versiongate.standalone.packet;

import java.util.function.Supplier;
import net.versiongate.standalone.enums.PacketBound;
import org.eclipse.collections.impl.map.mutable.primitive.IntObjectHashMap;
import org.eclipse.collections.impl.map.mutable.primitive.ObjectIntHashMap;

public class PacketRegistry {
    public static final PacketRegistry HANDSHAKE = handshake();
    public static final PacketRegistry STATUS = status();
    public static final PacketRegistry LOGIN = login();
    public static final PacketRegistry PLAY = play();

    private final ObjectIntHashMap<String> incomingByName = new ObjectIntHashMap<>();
    private final IntObjectHashMap<String> incomingById = new IntObjectHashMap<>();
    private final IntObjectHashMap<Supplier<Packet>> incomingSupplierMap = new IntObjectHashMap<>();

    private final ObjectIntHashMap<String> outgoingByName = new ObjectIntHashMap<>();
    private final IntObjectHashMap<String> outgoingById = new IntObjectHashMap<>();
    private final IntObjectHashMap<Supplier<Packet>> outgoingSupplierMap = new IntObjectHashMap<>();

    public String getPacketName(PacketBound bound, int id) {
        return switch (bound) {
            case IN -> this.incomingById.get(id);
            case OUT -> this.outgoingById.get(id);
        };
    }

    public int getPacketId(PacketBound bound, String name) {
        return switch (bound) {
            case IN -> this.incomingByName.get(name);
            case OUT -> this.outgoingByName.get(name);
        };
    }

    public Packet getPacket(PacketBound bound, int id) {
        Supplier<Packet> supplier = switch (bound) {
            case IN -> this.incomingSupplierMap.get(id);
            case OUT -> this.outgoingSupplierMap.get(id);
        };
        return supplier != null ? supplier.get() : null;
    }

    protected void registerIncoming(String name, int id, Supplier<Packet> supplier) {
        this.incomingByName.put(name, id);
        this.incomingById.put(id, name);
        this.incomingSupplierMap.put(id, supplier);
    }

    protected void registerOutgoing(String name, int id, Supplier<Packet> supplier) {
        this.outgoingByName.put(name, id);
        this.outgoingById.put(id, name);
        this.outgoingSupplierMap.put(id, supplier);
    }

    private static PacketRegistry handshake() {
        final PacketRegistry registry = new PacketRegistry();
        registry.registerIncoming("handshake", 0x00, null);
        return registry;
    }

    private static PacketRegistry status() {
        final PacketRegistry registry = new PacketRegistry();
        registry.registerIncoming("request", 0x00, null);
        registry.registerIncoming("ping", 0x01, null);

        registry.registerOutgoing("response", 0x00, null);
        registry.registerOutgoing("pong", 0x01, null);
        return registry;
    }

    private static PacketRegistry login() {
        final PacketRegistry registry = new PacketRegistry();

        registry.registerIncoming("login-start", 0x00, null);
        registry.registerIncoming("encryption-response", 0x01, null);
        registry.registerIncoming("login-plugin-response", 0x02, null);

        registry.registerOutgoing("disconnect", 0x00, null);
        registry.registerOutgoing("encryption-request", 0x01, null);
        registry.registerOutgoing("login-success", 0x02, null);
        return registry;
    }

    private static PacketRegistry play() {
        final PacketRegistry registry = new PacketRegistry();
        //registry.registerOutgoing("chat-message", 0x0F, OutChatMessagePacket::new);
        return registry;
    }
}
