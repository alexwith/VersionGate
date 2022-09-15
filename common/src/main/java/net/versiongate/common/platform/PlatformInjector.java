package net.versiongate.common.platform;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public abstract class PlatformInjector {
    private PlatformChannelInitializer channelInitializer;

    /**
     * Called on all platforms to inject a potential "Listener"
     */
    public abstract void inject() throws Exception;

    public PlatformChannelInitializer getChannelInitializer() {
        return this.channelInitializer;
    }

    public ChannelHandler findChannelHandler(ChannelPipeline pipeline) {
        for (final String name : pipeline.names()) {
            final ChannelHandler handler = pipeline.get(name);

            try {
                final Field childHandler = handler.getClass().getDeclaredField("childHandler");
                childHandler.setAccessible(true);
                return handler;
            } catch (ReflectiveOperationException ignored) {

            }
        }

        return pipeline.first();
    }

    public List<ChannelFuture> createInjectorList(List<ChannelFuture> origin, ChannelInitializerCreator channelInitializerCreator) {
        return new ArrayList<ChannelFuture>(origin) {

            @Override
            public boolean add(ChannelFuture future) {
                try {
                    PlatformInjector.this.injectChannelFuture(future, channelInitializerCreator);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                return super.add(future);
            }
        };
    }

    @SuppressWarnings("unchecked")
    public void injectChannelFuture(ChannelFuture future, ChannelInitializerCreator channelInitializerCreator) throws Exception {
        final ChannelPipeline pipeline = future.channel().pipeline();
        final ChannelHandler handler = this.findChannelHandler(pipeline);

        final Field childHandler = handler.getClass().getDeclaredField("childHandler");
        childHandler.setAccessible(true);

        final ChannelInitializer<Channel> oldInitializer = (ChannelInitializer<Channel>) childHandler.get(handler);
        this.channelInitializer = channelInitializerCreator.create(oldInitializer);

        childHandler.set(handler, this.channelInitializer);
    }

    public interface ChannelInitializerCreator {

        /**
         * Create the {@link PlatformChannelInitializer}
         *
         * @param origin the {@link ChannelInitializer} we're replacing
         * @return the new {@link PlatformChannelInitializer}
         */
        PlatformChannelInitializer create(ChannelInitializer<Channel> origin);
    }
}
