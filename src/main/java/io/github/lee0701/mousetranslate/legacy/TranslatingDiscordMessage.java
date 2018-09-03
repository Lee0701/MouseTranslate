package io.github.lee0701.mousetranslate.legacy;

import io.github.ranolp.rattranslate.Locale;
import net.dv8tion.jda.core.entities.TextChannel;

public class TranslatingDiscordMessage extends DiscordMessage {

    private Locale fromLocale;

    public TranslatingDiscordMessage(TextChannel textChannel, String name, Locale fromLocale, String message) {
        super(textChannel, name, message);
        this.fromLocale = fromLocale;
    }

    public Locale getFromLocale() {
        return fromLocale;
    }

    public void setFromLocale(Locale fromLocale) {
        this.fromLocale = fromLocale;
    }

}
