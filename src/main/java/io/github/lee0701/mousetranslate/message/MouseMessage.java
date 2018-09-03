package io.github.lee0701.mousetranslate.message;

import net.dv8tion.jda.core.entities.TextChannel;

public abstract class MouseMessage {
    private final TextChannel channel;

    public MouseMessage(TextChannel channel) {
        this.channel = channel;
    }

    public final TextChannel getChannel() {
        return channel;
    }

    public abstract void send();
}
