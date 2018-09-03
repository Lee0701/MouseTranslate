package io.github.lee0701.mousetranslate.message;

import io.github.lee0701.mousetranslate.MouseTranslate;
import io.github.ranolp.rattranslate.Locale;
import io.github.ranolp.rattranslate.RatTranslate;
import io.github.ranolp.rattranslate.translator.Translator;
import net.dv8tion.jda.core.entities.TextChannel;

import java.util.stream.Collectors;

public class MinecraftMessage extends WebhookMessage {
    private final String nickname;
    private final String message;
    private final Locale fromLocale;

    public MinecraftMessage(TextChannel channel, String nickname, String message, Locale fromLocale) {
        super(channel, nickname);
        this.nickname = nickname;
        this.message = message;
        this.fromLocale = fromLocale;
    }

    @Override
    public void send() {
        // todo: use webhook instead
        Translator translator = RatTranslate.getInstance().getTranslator();
        boolean auto = true;
        String translatedMessage = MouseTranslate.getInstance()
                .getLanguages()
                .stream()
                .map(Locale::getByCode)
                .map(toLocale -> auto
                                 ? translator.translateAuto(message, toLocale)
                                 : translator.translate(message, fromLocale, toLocale))
                .collect(Collectors.joining("\n"));
        getChannel().sendMessage(nickname + ":\n" + translatedMessage).complete();
    }
}
