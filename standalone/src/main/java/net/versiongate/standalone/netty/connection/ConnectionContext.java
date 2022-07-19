package net.versiongate.standalone.netty.connection;

import io.netty.channel.Channel;
import net.versiongate.standalone.netty.enums.State;

public class ConnectionContext {
    private Channel target;
    private State state = State.HANDSHAKE;

    public Channel getTarget() {
        return this.target;
    }

    public void setTarget(Channel target) {
        this.target = target;
    }

    public State getState() {
        return this.state;
    }

    public void setState(State state) {
        this.state = state;
    }
}
