package net.versiongate.common.platform;

public abstract class Platform {
    private final PlatformInjector injector;

    public Platform(PlatformInjector injector) {
        this.injector = injector;
    }

    public void load() {
        try {
            this.injector.inject();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
