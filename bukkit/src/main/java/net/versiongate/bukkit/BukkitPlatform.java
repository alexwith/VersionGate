package net.versiongate.bukkit;

import net.versiongate.bukkit.injector.BukkitInjector;
import net.versiongate.common.platform.Platform;

public class BukkitPlatform extends Platform {

    public BukkitPlatform() {
        super(new BukkitInjector());
    }

    @Override
    public void load() {
        super.load();

    }
}
