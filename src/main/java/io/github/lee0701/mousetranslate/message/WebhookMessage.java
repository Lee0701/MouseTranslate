package io.github.lee0701.mousetranslate.message;

import net.dv8tion.jda.core.entities.Icon;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.Webhook;

public class WebhookMessage extends MouseMessage {
    private final String nickname;
    private Icon avatar;
    private Message message;

    public WebhookMessage(TextChannel channel, String nickname) {
        super(channel);
        this.nickname = nickname;
    }

    protected void setAvatar(Icon avatar) {
        this.avatar = avatar;
    }

    protected void setMessage(Message message) {
        this.message = message;
    }

    @Override
    public void send() {
        if (message == null) {
            return;
        }
        Webhook webhook = getChannel().createWebhook(nickname).setAvatar(avatar).complete();
        webhook.newClient().build().send(message).join();
        webhook.delete().complete();
    }
}
