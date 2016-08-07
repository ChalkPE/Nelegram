package pe.chalk.nelegram;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.entity.Entity;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.block.BlockBreakEvent;
import cn.nukkit.event.block.BlockPlaceEvent;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.event.inventory.CraftItemEvent;
import cn.nukkit.event.inventory.InventoryOpenEvent;
import cn.nukkit.event.inventory.InventoryPickupArrowEvent;
import cn.nukkit.event.inventory.InventoryPickupItemEvent;
import cn.nukkit.event.player.*;
import cn.nukkit.inventory.InventoryHolder;
import cn.nukkit.utils.Config;
import cn.nukkit.utils.ConfigSection;
import cn.nukkit.utils.TextFormat;
import pe.chalk.nelegram.event.TelegramMessageEvent;
import pe.chalk.telegram.method.TextMessageSender;
import pe.chalk.telegram.type.message.Message;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * @author ChalkPE <chalk@chalk.pe>
 * @since 2016-08-07
 */
public class NelegramAuthenticator implements Listener {
    private Config config;
    private Map<String, Boolean> players;
    private Map<String, String> passwords;

    public NelegramAuthenticator() {
        this.config = new Config(new File(Nelegram.getInstance().getDataFolder(), "auth.yml"), Config.YAML);
        if (!this.config.exists("ids")) this.config.set("ids", new ConfigSection());

        this.players = new HashMap<>();
        this.passwords = new HashMap<>();
    }

    public boolean isEqualPlayer(Player a, Player b) {
        return a.getName().equalsIgnoreCase(b.getName());
    }

    public boolean isRegistered(Player player) {
        return this.isRegistered(player.getName());
    }

    public boolean isRegistered(String player) {
        return this.config.getSection("ids").exists(player.toLowerCase());
    }

    public int getChatId(Player player) {
        return this.getChatId(player.getName());
    }

    public int getChatId(String player) {
        return this.config.getSection("ids").getInt(player.toLowerCase());
    }

    public boolean isAuthenticated(Player player) {
        return this.isAuthenticated(player.getName());
    }

    public boolean isAuthenticated(String player) {
        return Boolean.TRUE.equals(this.players.get(player.toLowerCase()));
    }

    public void setAuthenticated(Player player) {
        this.setAuthenticated(player.getName());
    }

    public void setAuthenticated(String player) {
        this.setAuthenticated(player, true);
    }

    public void setAuthenticated(Player player, boolean authenticated) {
        this.setAuthenticated(player.getName(), authenticated);
    }

    public void setAuthenticated(String player, boolean authenticated) {
        this.players.put(player.toLowerCase(), authenticated);
    }

    @EventHandler
    @SuppressWarnings("unused")
    public void onTelegramMessage(TelegramMessageEvent event) {
        final Message message = event.getMessage();
        if (!message.hasFrom() || !message.getFrom().hasUsername()) return;

        this.config.getSection("ids").put(message.getFrom().getUsername().toLowerCase(), message.getFrom().getId());
        this.config.save();
    }

    @EventHandler
    @SuppressWarnings("unused")
    public void onPreLogin(PlayerPreLoginEvent event) {
        final Player player = event.getPlayer();

        if (Server.getInstance().getOnlinePlayers().values().stream().anyMatch(online -> this.isEqualPlayer(online, player))) {
            event.setCancelled();
            player.kick("Already logged in");
            return;
        }

        if (!this.isRegistered(player)) {
            event.setCancelled();
            player.kick("https://telegram.me/" + Nelegram.getMe().getUsername());
            return;
        }

        this.setAuthenticated(player, false);
    }

    @EventHandler
    @SuppressWarnings("unused")
    public void onJoin(PlayerJoinEvent event) {
        final Player player = event.getPlayer();

        final String password = Integer.toString(10000 + new Random().nextInt(90000));
        this.passwords.put(player.getName().toLowerCase(), password);

        new TextMessageSender(this.getChatId(player), "Password: " + password).send(Nelegram.getBot());

        player.sendMessage(TextFormat.AQUA + "A password has been sent to you.");
        player.sendMessage(TextFormat.AQUA + "Please check your Telegram account.");
    }

    @EventHandler
    @SuppressWarnings("unused")
    public void onChat(PlayerChatEvent event) {
        final Player player = event.getPlayer();
        if (!this.isAuthenticated(player)) {
            event.setCancelled();

            if (!event.getMessage().equals(this.passwords.get(player.getName().toLowerCase()))) {
                player.sendMessage(TextFormat.RED + "Please type your password.");
                return;
            }

            this.setAuthenticated(player);

            player.sendMessage(TextFormat.GREEN + "Welcome!");
            new TextMessageSender(this.getChatId(player), "You've been logged in from " + player.getAddress()).send(Nelegram.getBot());
        }
    }

    @EventHandler
    @SuppressWarnings("unused")
    public void onCommandPreProcess(PlayerCommandPreprocessEvent event) {
        final Player player = event.getPlayer();
        if (!this.isAuthenticated(player)) {
            event.setCancelled();
            player.sendMessage(TextFormat.RED + "Please type your password.");
        }
    }

    @EventHandler
    @SuppressWarnings("unused")
    public void onDrop(PlayerDropItemEvent event) {
        if (!this.isAuthenticated(event.getPlayer())) event.setCancelled();
    }

    @EventHandler
    @SuppressWarnings("unused")
    public void onFoodLevelChange(PlayerFoodLevelChangeEvent event) {
        if (!this.isAuthenticated(event.getPlayer())) event.setCancelled();
    }

    @EventHandler
    @SuppressWarnings("unused")
    public void onItemConsume(PlayerItemConsumeEvent event) {
        if (!this.isAuthenticated(event.getPlayer())) event.setCancelled();
    }

    @EventHandler
    @SuppressWarnings("unused")
    public void onItemHold(PlayerItemHeldEvent event) {
        if (!this.isAuthenticated(event.getPlayer())) event.setCancelled();
    }

    @EventHandler
    @SuppressWarnings("unused")
    public void onMove(PlayerMoveEvent event) {
        if (!this.isAuthenticated(event.getPlayer())) event.setCancelled();
    }

    @EventHandler
    @SuppressWarnings("unused")
    public void onSneak(PlayerToggleSneakEvent event) {
        if (!this.isAuthenticated(event.getPlayer())) event.setCancelled();
    }

    @EventHandler
    @SuppressWarnings("unused")
    public void onSprint(PlayerToggleSprintEvent event) {
        if (!this.isAuthenticated(event.getPlayer())) event.setCancelled();
    }

    @EventHandler
    @SuppressWarnings("unused")
    public void onDamage(EntityDamageEvent event) {
        final Entity entity = event.getEntity();
        if (entity instanceof Player && this.isAuthenticated((Player) entity)) event.setCancelled();
    }

    @EventHandler
    @SuppressWarnings("unused")
    public void onInventoryOpen(InventoryOpenEvent event) {
    }

    @EventHandler
    @SuppressWarnings("unused")
    public void onPickup(InventoryPickupItemEvent event) {
        final InventoryHolder holder = event.getInventory().getHolder();
        if (holder instanceof Player && this.isAuthenticated((Player) holder)) event.setCancelled();
    }

    @EventHandler
    @SuppressWarnings("unused")
    public void onPickupArrow(InventoryPickupArrowEvent event) {
        final InventoryHolder holder = event.getInventory().getHolder();
        if (holder instanceof Player && this.isAuthenticated((Player) holder)) event.setCancelled();
    }

    @EventHandler
    @SuppressWarnings("unused")
    public void onInventoryOpen(CraftItemEvent event) {
        if (!this.isAuthenticated(event.getPlayer())) event.setCancelled();
    }

    @EventHandler
    @SuppressWarnings("unused")
    public void onInteract(PlayerInteractEvent event) {
        final Player player = event.getPlayer();
        if (!this.isAuthenticated(player)) {
            event.setCancelled();
            player.sendMessage(TextFormat.RED + "Please type your password.");
        }
    }

    @EventHandler
    @SuppressWarnings("unused")
    public void onBreak(BlockBreakEvent event) {
        if (!this.isAuthenticated(event.getPlayer())) event.setCancelled();
    }

    @EventHandler
    @SuppressWarnings("unused")
    public void onPlace(BlockPlaceEvent event) {
        final Player player = event.getPlayer();
        if (!this.isAuthenticated(player)) {
            event.setCancelled();
            player.sendMessage(TextFormat.RED + "Please type your password.");
        }
    }
}
