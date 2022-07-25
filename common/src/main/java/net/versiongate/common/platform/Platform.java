package net.versiongate.common.platform;

import net.versiongate.api.gate.IGateManager;
import net.versiongate.common.gate.GateManager;

public abstract class Platform {
    private final PlatformInjector injector;
    private final IGateManager gateManager;

    private static Platform INSTANCE;

    public Platform(PlatformInjector injector) {
        INSTANCE = this;

        this.injector = injector;
        this.gateManager = new GateManager();

        this.gateManager.initialLoad();
    }

    public static Platform get() {
        return INSTANCE;
    }

    public PlatformInjector getInjector() {
        return this.injector;
    }

    public IGateManager getGateManager() {
        return this.gateManager;
    }

    public void load() {
        try {
            this.injector.inject();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
