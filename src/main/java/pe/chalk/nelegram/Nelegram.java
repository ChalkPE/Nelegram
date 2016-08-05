package pe.chalk.nelegram;

import cn.nukkit.Server;
import cn.nukkit.plugin.PluginBase;
import org.telegram.telegrambots.TelegramBotsApi;
import pe.chalk.nelegram.util.UnsafeRunnable;

import java.io.File;

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

        if (this.getConfig().getString("token", "").trim().isEmpty()) {
            this.getLogger().alert("You need to set your Telegram bot token to enable this plugin");
            this.getLogger().alert("-> " + new File(this.getDataFolder(), "config.yml").getAbsolutePath());
            this.getServer().getPluginManager().disablePlugin(this);
            return;
        }

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
