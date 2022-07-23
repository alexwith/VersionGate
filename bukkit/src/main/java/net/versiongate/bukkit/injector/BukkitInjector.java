package net.versiongate.bukkit.injector;

import io.netty.channel.ChannelFuture;
import java.lang.reflect.Field;
import java.util.List;
import net.minecraft.server.v1_8_R3.ServerConnection;
import net.versiongate.bukkit.handler.BukkitChannelInitializer;
import net.versiongate.common.platform.PlatformInjector;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_8_R3.CraftServer;

public class BukkitInjector implements PlatformInjector {

    @Override
    @SuppressWarnings("unchecked")
    public void inject() throws Exception {
        final ServerConnection connection = ((CraftServer) Bukkit.getServer()).getServer().getServerConnection();
        final ChannelInitializerCreator creator = BukkitChannelInitializer::new;

        for (final Field field : connection.getClass().getDeclaredFields()) {
            if (!List.class.isAssignableFrom(field.getType()) || !field.getGenericType().getTypeName().contains(ChannelFuture.class.getName())) {
                continue;
            }

            field.setAccessible(true);

            final List<ChannelFuture> list = (List<ChannelFuture>) field.get(connection);
            final List<ChannelFuture> otherList = this.createInjectorList(list, creator);
            for (final ChannelFuture future : list) {
                this.injectChannelFuture(future, creator);
            }

            field.set(connection, otherList);
        }
    }
}
