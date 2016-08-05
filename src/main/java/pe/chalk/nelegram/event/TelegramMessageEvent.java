package pe.chalk.nelegram.event;

import cn.nukkit.event.HandlerList;
import cn.nukkit.event.plugin.PluginEvent;
import org.telegram.telegrambots.api.objects.Message;
import pe.chalk.nelegram.Nelegram;

/**
 * @author ChalkPE <chalk@chalk.pe>
 * @since 2016-08-05
 */
public class TelegramMessageEvent extends PluginEvent {
    private Message message;

    public TelegramMessageEvent(Message message) {
        super(Nelegram.getInstance());
        this.message = message;
    }

    public Message getMessage() {
        return this.message;
    }

    @SuppressWarnings("unused")
    public void setMessage(Message message) {
        this.message = message;
    }

    private static final HandlerList handlers = new HandlerList();

    @SuppressWarnings("unused")
    public static HandlerList getHandlers() {
        return TelegramMessageEvent.handlers;
    }
}
