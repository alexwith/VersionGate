package net.versiongate.api.gate.version;

import java.util.TreeMap;

public enum ProtocolVersion {

    VERSION1_8(47, "1.8"),
    VERSION1_9(107, "1.9");

    private final int id;
    private final String name;

    private static final TreeMap<Integer, ProtocolVersion> TREED_VERSIONS = new TreeMap<Integer, ProtocolVersion>() {{
        for (final ProtocolVersion version : ProtocolVersion.values()) {
            this.put(version.getId(), version);
        }
    }};

    ProtocolVersion(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public static ProtocolVersion getClosest(int id) {
        return TREED_VERSIONS.floorEntry(id).getValue();
    }

    public int getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }
}
