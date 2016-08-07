package pe.chalk.nelegram;

import cn.nukkit.Server;
import cn.nukkit.plugin.PluginBase;
import pe.chalk.nelegram.event.TelegramMessageEvent;
import pe.chalk.telegram.TelegramBot;
import pe.chalk.telegram.method.MeGetter;
import pe.chalk.telegram.type.Update;
import pe.chalk.telegram.type.user.User;

import java.io.File;
import java.util.Objects;

import static cn.nukkit.utils.TextFormat.*;

/**
 * @author ChalkPE <chalk@chalk.pe>
 * @since 2016-08-04
 */
public class Nelegram extends PluginBase {
    private static Nelegram instance;
    private static TelegramBot bot;

    private static String token;
    private static Integer target;
    private static User me;

    private NelegramMessenger messenger;
    private NelegramAuthenticator authenticator;

    public static Nelegram getInstance() {
        return Nelegram.instance;
    }

    public static TelegramBot getBot() {
        return Nelegram.bot;
    }

    public static String getToken() {
        return Nelegram.token;
    }

    public static Integer getTarget() {
        return Nelegram.target;
    }

    public static User getMe() {
        return Nelegram.me;
    }

    @Override
    public void onLoad() {
        Nelegram.instance = this;
    }

    @Override
    public void onEnable() {
        this.saveDefaultConfig();

        Nelegram.token = this.require("token", "");
        if (Objects.isNull(Nelegram.token)) return;

        Nelegram.target = this.require("target", 0);
        if (Objects.isNull(Nelegram.target)) return;

        this.messenger = new NelegramMessenger();
        this.getServer().getPluginManager().registerEvents(this.getMessenger(), this);
        this.getServer().getPluginManager().subscribeToPermission(Server.BROADCAST_CHANNEL_USERS, this.getMessenger());

        this.authenticator = new NelegramAuthenticator();
        this.getServer().getPluginManager().registerEvents(this.getAuthenticator(), this);

        Nelegram.bot = new TelegramBot(Nelegram.getToken());
        Nelegram.bot.addHandler(updates -> updates.parallelStream().map(Update::getMessage).filter(Objects::nonNull).map(TelegramMessageEvent::new).forEach(this.getServer().getPluginManager()::callEvent));
        Nelegram.bot.start();

        Nelegram.me = new MeGetter().get(Nelegram.bot);
        this.getLogger().info("bot started: " + GREEN + "@" + Nelegram.me.getUsername() + RESET + " (" + Nelegram.me.getId() + ")");
    }

    @Override
    public void onDisable() {
        Nelegram.bot.interrupt();
    }

    public NelegramMessenger getMessenger() {
        return this.messenger;
    }

    public NelegramAuthenticator getAuthenticator() {
        return this.authenticator;
    }

    private <T> T require(String key, T defaultValue) {
        final T value = this.getConfig().get(key, defaultValue);
        if (!value.equals(defaultValue)) return value;

        this.getLogger().alert("You need to set `" + key + "` to enable this plugin");
        this.getLogger().alert("-> " + new File(this.getDataFolder(), "config.yml").getAbsolutePath());

        this.getServer().getPluginManager().disablePlugin(this);
        return null;
    }
}
