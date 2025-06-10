package com.discordlogger;

import io.papermc.paper.event.player.AsyncChatEvent;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.player.*;
import org.bukkit.plugin.java.JavaPlugin;

public class DiscordLogger extends JavaPlugin implements Listener {

    private DiscordWebhook chatWebhook;
    private DiscordWebhook cmdWebhook;
    private FileConfiguration config;
    @Override
    public void onEnable() {
        saveDefaultConfig();
        config = getConfig();

        chatWebhook = new DiscordWebhook(
                config.getString("chat_webhook_url"),
                config.getString("bot_name"),
                config.getString("avatar_url")
        );

        cmdWebhook = new DiscordWebhook(
                config.getString("cmd_webhook_url"),
                config.getString("bot_name"),
                config.getString("avatar_url")
        );

        getServer().getPluginManager().registerEvents(this, this);
        getLogger().info("DiscordLogger enabled.");
    }

    @EventHandler
    public void onChat(AsyncChatEvent event) {
        Player player = event.getPlayer();

        String message = net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer.plainText().serialize(event.message());
        String format = getConfig().getString("message_format", "(||%s||) <**`%s`**>: %s");

        String formatted = String.format(format, player.getUniqueId(), player.getName(), message);

        if (chatWebhook != null) {
            Bukkit.getScheduler().runTaskAsynchronously(this, () -> {
                try {
                    chatWebhook.sendMessage(formatted);
                } catch (Exception e) {
                    getLogger().warning("Failed to send message to Discord webhook: " + e.getMessage());
                }
            });
        } else {
            getLogger().warning("Webhook is not initialized!");
        }
    }


    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        String format = config.getString("join_format", "[JOIN] **`%s` joined the game** (%s)");
        String message = String.format(format, player.getName(), player.getUniqueId());
        chatWebhook.sendMessage(message);
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        String format = config.getString("leave_format", "[LEAVE] **`%s` left the game** (%s)");
        String message = String.format(format, player.getName(), player.getUniqueId());
        chatWebhook.sendMessage(message);
    }

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        String format = config.getString("command_format", "[CMD] (||%s||) **`%s`** executed command: `%s`");
        String message = String.format(format, player.getUniqueId(), player.getName(), event.getMessage());
        cmdWebhook.sendMessage(message);
    }
}
