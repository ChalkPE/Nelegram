package pe.chalk.nelegram;

import cn.nukkit.Server;
import cn.nukkit.utils.Config;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import pe.chalk.nelegram.event.TelegramMessageEvent;

import java.util.Objects;

/**
 * @author ChalkPE <chalk@chalk.pe>
 * @since 2016-08-04
 */
public class TelegramBot extends TelegramLongPollingBot {
    private final Config config;

    public TelegramBot(Config config){
        this.config = config;
    }

    @Override
    public String getBotToken() {
        return this.config.getString("token");
    }

    @Override
    public String getBotUsername() {
        return this.config.getString("username");
    }

    @Override
    public void onUpdateReceived(Update update) {
        if(!update.hasMessage()) return;
        final Message message = update.getMessage();

        if(!message.hasText() || Objects.isNull(message.getFrom())) return;
        Server.getInstance().getPluginManager().callEvent(new TelegramMessageEvent(message));
    }
}
