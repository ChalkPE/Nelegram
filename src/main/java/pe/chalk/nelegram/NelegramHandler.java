package pe.chalk.nelegram;

import cn.nukkit.Server;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.ConsoleCommandSender;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.utils.TextFormat;
import org.telegram.telegrambots.api.objects.Message;
import pe.chalk.nelegram.event.TelegramMessageEvent;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author ChalkPE <chalk@chalk.pe>
 * @since 2016-08-04
 */
public class NelegramHandler extends ConsoleCommandSender implements Listener {
    @Override
    public String getName() {
        return Nelegram.getInstance().getBot().getBotUsername();
    }

    @Override
    public void sendMessage(String message) {
        final String text = TextFormat.clean(this.getServer().getLanguage().translateString(message).trim());
        for (String line: text.split("\n")) Nelegram.getInstance().getBot().sendMessageToTarget(line);
    }

    @EventHandler
    @SuppressWarnings("unused")
    public void onTelegramMessage(TelegramMessageEvent event) {
        final Message message = event.getMessage();
        if (Objects.isNull(message)) return;

        final String text = this.getServer().getLanguage().translateString("chat.type.text", new String[]{ message.getFrom().getUserName(), message.getText() });
        final List<CommandSender> recipients = this.getServer().getPluginManager().getPermissionSubscriptions(Server.BROADCAST_CHANNEL_USERS).stream().filter(v -> v instanceof CommandSender && !(v instanceof NelegramHandler)).map(CommandSender.class::cast).collect(Collectors.toList());

        this.getServer().broadcastMessage(text, recipients);
    }
}