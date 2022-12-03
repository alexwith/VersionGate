package net.versiongate.bukkit;

import net.versiongate.bukkit.injector.BukkitInjector;
import net.versiongate.bukkit.util.NMSUtil;
import net.versiongate.common.platform.Platform;

public class BukkitPlatform extends Platform {

    public BukkitPlatform() {
        super(new BukkitInjector());
    }

    @Override
    public void load() {
        super.load();
    }

    @Override
    public int getProtocolVersion() {
        try {
            return NMSUtil.getProtocolVersion();
        } catch (Exception e) {
            throw new RuntimeException("Failed to get server", e);
        }
    }
}
