package net.versiongate.bukkit;

import org.bukkit.plugin.java.JavaPlugin;

public class BukkitPlugin extends JavaPlugin {
    private final BukkitPlatform platform;

    public BukkitPlugin() {
        this.platform = new BukkitPlatform();
    }

    @Override
    public void onEnable() {
        this.platform.load();
    }
}
