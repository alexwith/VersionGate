package net.versiongate.bukkit;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class BukkitPlugin extends JavaPlugin {
    private final BukkitPlatform platform;

    public BukkitPlugin() {
        this.platform = new BukkitPlatform();
    }

    @Override
    public void onEnable() {
        // we need to mess around with NMS a 1 tick after enable to let things instantiate
        Bukkit.getScheduler().runTaskLater(this, this.platform::load, 1);
    }
}
