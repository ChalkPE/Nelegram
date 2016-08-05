package pe.chalk.nelegram;

import cn.nukkit.Server;
import cn.nukkit.plugin.PluginBase;
import org.telegram.telegrambots.TelegramBotsApi;
import pe.chalk.nelegram.util.UnsafeRunnable;

/**
 * @author ChalkPE <chalk@chalk.pe>
 * @since 2016-08-04
 */
public class Nelegram extends PluginBase {
    private static Nelegram instance;

    private TelegramBotsApi api;
    private TelegramBot bot;

    private NelegramHandler handler;

    @Override
    public void onLoad() {
        Nelegram.instance = this;
    }

    @Override
    public void onEnable() {
        this.saveDefaultConfig();

        this.api = new TelegramBotsApi();
        this.bot = new TelegramBot(this.getConfig());
        UnsafeRunnable.start(() -> this.api.registerBot(bot));

        this.handler = new NelegramHandler();
        this.getServer().getPluginManager().registerEvents(this.handler, this);
        this.getServer().getPluginManager().subscribeToPermission(Server.BROADCAST_CHANNEL_USERS, this.handler);
    }

    public static Nelegram getInstance() {
        return Nelegram.instance;
    }

    public TelegramBot getBot() {
        return this.bot;
    }
}
