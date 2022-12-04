package net.versiongate.standalone;

import net.versiongate.common.platform.Platform;

public class StandalonePlatform extends Platform {

    public StandalonePlatform() {
        super(null);
    }

    @Override
    public int getProtocolVersion() {
        return 47;
    }

    private void createConnection() {

    }
}