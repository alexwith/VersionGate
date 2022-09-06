package net.versiongate.bukkit.util;

import java.lang.reflect.Field;
import org.bukkit.Bukkit;

public class NMSUtil {
    private static final String BASE = Bukkit.getServer().getClass().getPackage().getName();
    private static final String NMS = BASE.replace("org.bukkit.craftbukkit", "net.minecraft.server");

    public static Class<?> nms(String className, String fallbackFullClassName) throws ClassNotFoundException {
        try {
            return Class.forName(NMS + "." + className);
        } catch (ClassNotFoundException ignored) {
            return Class.forName(fallbackFullClassName);
        }
    }

    public static int getProtocolVersion() throws Exception {
        final Class<?> minecraftServerClass = NMSUtil.nms(
            "MinecraftServer",
            "net.minecraft.server.MinecraftServer"
        );
        final Object server = minecraftServerClass.getDeclaredMethod("getServer").invoke(null);

        final Class<?> serverPingClass = NMSUtil.nms(
            "ServerPing",
            "net.minecraft.network.protocol.status.ServerPing"
        );
        Object ping = minecraftServerClass.getDeclaredFields();
        for (final Field field : minecraftServerClass.getDeclaredFields()) {
            if (field.getType() == serverPingClass) {
                field.setAccessible(true);
                ping = field.get(server);
                break;
            }
        }

        final Class<?> serverDataClass = NMSUtil.nms(
            "ServerPing$ServerData",
            "net.minecraft.network.protocol.status.ServerPing$ServerData"
        );
        Object serverData = null;
        for (final Field field : serverPingClass.getDeclaredFields()) {
            if (field.getType() == serverDataClass) {
                field.setAccessible(true);
                serverData = field.get(ping);
                break;
            }
        }

        for (final Field field : serverDataClass.getDeclaredFields()) {
            if (field.getType() != int.class) {
                continue;
            }

            field.setAccessible(true);
            final int protocolVersion = (int) field.get(serverData);
            if (protocolVersion == -1) {
                continue;
            }

            return protocolVersion;
        }

        throw new Exception();
    }
}