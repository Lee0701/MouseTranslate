package io.github.lee0701.mousetranslate.message;

import net.dv8tion.jda.core.entities.TextChannel;

public class SimpleMessage extends MouseMessage {
    private final net.dv8tion.jda.core.entities.Message message;

    public SimpleMessage(TextChannel channel, net.dv8tion.jda.core.entities.Message message) {
        super(channel);
        this.message = message;
    }

    @Override
    public void send() {
        getChannel().sendMessage(message).complete();
    }
}
