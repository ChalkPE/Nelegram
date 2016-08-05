package pe.chalk.nelegram;

import cn.nukkit.Server;
import cn.nukkit.plugin.PluginBase;
import pe.chalk.telegram.TelegramBot;

import java.io.File;
import java.util.Objects;

/**
 * @author ChalkPE <chalk@chalk.pe>
 * @since 2016-08-04
 */
public class Nelegram extends PluginBase {
    private static Nelegram instance;
    private static TelegramBot bot;

    private static String token;
    private static Integer target;

    private NelegramHandler handler;

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

        Nelegram.bot = new TelegramBot(Nelegram.getToken());
        Nelegram.bot.start();

        this.handler = new NelegramHandler();
        this.getServer().getPluginManager().registerEvents(this.getHandler(), this);
        this.getServer().getPluginManager().subscribeToPermission(Server.BROADCAST_CHANNEL_USERS, this.getHandler());

        Nelegram.bot.addHandler(this.handler);
    }

    @Override
    public void onDisable() {
        Nelegram.bot.interrupt();
    }

    public NelegramHandler getHandler() {
        return this.handler;
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
