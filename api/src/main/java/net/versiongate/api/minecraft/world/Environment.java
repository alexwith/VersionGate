package net.versiongate.api.minecraft.world;

public enum Environment {

    NORMAL(0),
    NETHER(-1),
    END(1);

    private final int id;

    Environment(int id) {
        this.id = id;
    }

    public int getId() {
        return this.id;
    }

    public static Environment parse(int id) {
        for (final Environment environment : values()) {
            if (id == environment.getId()) {
                return environment;
            }
        }

        return NORMAL;
    }
}
