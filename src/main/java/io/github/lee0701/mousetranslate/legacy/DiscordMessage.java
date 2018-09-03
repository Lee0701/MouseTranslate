package io.github.lee0701.mousetranslate.legacy;

import net.dv8tion.jda.core.entities.TextChannel;

public class DiscordMessage {

    private TextChannel textChannel;
    private String name;
    private String message;

    public DiscordMessage(TextChannel textChannel, String name, String message) {
        this.textChannel = textChannel;
        this.name = name;
        this.message = message;
    }

    public TextChannel getTextChannel() {
        return textChannel;
    }

    public String getName() {
        return name;
    }

    public String getMessage() {
        return message;
    }
}
