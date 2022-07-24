package net.versiongate.common.platform;

import net.versiongate.api.gate.IGateManager;
import net.versiongate.common.gate.GateManager;

public abstract class Platform {
    private final PlatformInjector injector;
    private final IGateManager gateManager;

    public Platform(PlatformInjector injector) {
        this.injector = injector;
        this.gateManager = new GateManager();
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
