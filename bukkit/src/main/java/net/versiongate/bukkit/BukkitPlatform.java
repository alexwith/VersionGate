package net.versiongate.bukkit;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.server.v1_8_R3.ServerConnection;
import net.versiongate.bukkit.handler.BukkitChannelInitializer;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_8_R3.CraftServer;
import org.bukkit.plugin.java.JavaPlugin;

//gradle clean shadowJar -Dorg.gradle.java.home="/Library/Java/JavaVirtualMachines/adoptopenjdk-8.jdk/Contents/Home"
public class BukkitPlatform extends JavaPlugin {

    @Override
    public void onEnable() {
        try {
            this.inject();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ServerConnection getServerConnection() {
        return ((CraftServer) Bukkit.getServer()).getServer().getServerConnection();
    }

    public void inject() throws Exception {
        final ServerConnection connection = this.getServerConnection();

        for (final Field field : connection.getClass().getDeclaredFields()) {
            if (!List.class.isAssignableFrom(field.getType()) || !field.getGenericType().getTypeName().contains(ChannelFuture.class.getName())) {
                continue;
            }

            field.setAccessible(true);
            final List<ChannelFuture> list = (List<ChannelFuture>) field.get(connection);
            final List<ChannelFuture> otherList = new ArrayList<ChannelFuture>(list) {

                @Override
                public boolean add(ChannelFuture future) {
                    try {
                        injectChannelFuture(future);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return super.add(future);
                }
            };

            synchronized (list) {
                for (final ChannelFuture future : list) {
                    this.injectChannelFuture(future);
                }

                field.set(connection, otherList);
            }
        }
    }

    private void injectChannelFuture(ChannelFuture future) throws Exception {
        final List<String> names = future.channel().pipeline().names();
        ChannelHandler bootstrapAcceptor = null;
        for (final String name : names) {
            final ChannelHandler handler = future.channel().pipeline().get(name);
            try {
                final Field childHandler = handler.getClass().getDeclaredField("childHandler");
                childHandler.setAccessible(true);
                bootstrapAcceptor = handler;
                break;
            } catch (ReflectiveOperationException ignored) {

            }
        }

        if (bootstrapAcceptor == null) {
            bootstrapAcceptor = future.channel().pipeline().first();
        }

        final Field childHandler = bootstrapAcceptor.getClass().getDeclaredField("childHandler");
        childHandler.setAccessible(true);
        ChannelInitializer<Channel> oldInitializer = ChannelInitializer.class.cast(childHandler.get(bootstrapAcceptor));

        childHandler.set(bootstrapAcceptor, new BukkitChannelInitializer(oldInitializer));
    }
}
