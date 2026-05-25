package yoflixxz.exoStats;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Statistic;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

public class ExoStats extends JavaPlugin implements Listener, CommandExecutor {

    private Component format(String text) {
        return LegacyComponentSerializer.legacySection().deserialize(text);
    }

    @Override
    public void onEnable() {
        getCommand("stats").setExecutor(this);
        getServer().getPluginManager().registerEvents(this, this);
        getLogger().info("ExoStats has been enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info("ExoStats has been disabled!");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cOnly players can use this command!");
            return true;
        }

        Player player = (Player) sender;

        if (args.length == 0) {
            openStatsGUI(player, player);
        } else {
            Player target = Bukkit.getPlayer(args[0]);
            if (target == null) {
                player.sendMessage("§cPlayer not found!");
                return true;
            }
            openStatsGUI(player, target);
        }

        return true;
    }

    private void openStatsGUI(Player viewer, Player target) {
        Inventory inv = Bukkit.createInventory(null, 27, format("§8ꜱᴛᴀᴛꜱ §8» " + target.getName()));

        // Top row - Level/XP centered
        inv.setItem(4, createLevelItem(target));

        // Middle row - Stats around player head
        inv.setItem(10, createKillsItem(target));
        inv.setItem(11, createDeathsItem(target));
        inv.setItem(12, createKDRItem(target));
        inv.setItem(13, createPlayerHead(target));
        inv.setItem(14, createPlaytimeItem(target));
        inv.setItem(15, createBalanceItem(target));
        inv.setItem(16, createPingItem(target));

        // Bottom row - Close button centered
        inv.setItem(22, createCloseButton());

        viewer.openInventory(inv);
    }

    private ItemStack createPlayerHead(Player target) {
        ItemStack item = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) item.getItemMeta();
        meta.setOwningPlayer(target);
        meta.displayName(format("§c" + target.getName()));

        long firstPlayed = target.getFirstPlayed();
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy");
        SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a");
        String date = dateFormat.format(new Date(firstPlayed));
        String time = timeFormat.format(new Date(firstPlayed));

        meta.lore(Arrays.asList(
                format("§7Joined on §e" + date),
                format("§7at §a" + time)));
        item.setItemMeta(meta);
        return item;
    }

    private ItemStack createKillsItem(Player target) {
        ItemStack item = new ItemStack(Material.DIAMOND_SWORD);
        ItemMeta meta = item.getItemMeta();
        meta.displayName(format("§cᴋɪʟʟꜱ"));
        int kills = target.getStatistic(Statistic.PLAYER_KILLS);
        meta.lore(Arrays.asList(format("§7Total: §a" + kills)));
        item.setItemMeta(meta);
        return item;
    }

    private ItemStack createDeathsItem(Player target) {
        ItemStack item = new ItemStack(Material.SKELETON_SKULL);
        ItemMeta meta = item.getItemMeta();
        meta.displayName(format("§cᴅᴇᴀᴛʜꜱ"));
        int deaths = target.getStatistic(Statistic.DEATHS);
        meta.lore(Arrays.asList(format("§7Total: §c" + deaths)));
        item.setItemMeta(meta);
        return item;
    }

    private ItemStack createKDRItem(Player target) {
        ItemStack item = new ItemStack(Material.NETHER_STAR);
        ItemMeta meta = item.getItemMeta();
        meta.displayName(format("§eᴋᴅʀ"));

        int kills = target.getStatistic(Statistic.PLAYER_KILLS);
        int deaths = target.getStatistic(Statistic.DEATHS);
        String kdr;

        if (deaths == 0) {
            kdr = "—";
        } else {
            kdr = String.format("%.2f", (double) kills / deaths);
        }

        meta.lore(Arrays.asList(format("§7Ratio: §6" + kdr)));
        item.setItemMeta(meta);
        return item;
    }

    private ItemStack createPlaytimeItem(Player target) {
        ItemStack item = new ItemStack(Material.CLOCK);
        ItemMeta meta = item.getItemMeta();
        meta.displayName(format("§bᴘʟᴀʏᴛɪᴍᴇ"));

        int ticks = target.getStatistic(Statistic.PLAY_ONE_MINUTE);
        int totalSeconds = ticks / 20;
        int totalMinutes = totalSeconds / 60;
        int totalHours = totalMinutes / 60;

        int days = totalHours / 24;
        int hours = totalHours % 24;
        int minutes = totalMinutes % 60;

        String playtime;
        if (days > 0) {
            playtime = String.format("§d%d§dd §d%d§dh §d%d§dm", days, hours, minutes);
        } else {
            playtime = String.format("§d%d§dh §d%d§dm", hours, minutes);
        }

        meta.lore(Arrays.asList(format("§7Total: " + playtime)));
        item.setItemMeta(meta);
        return item;
    }

    private ItemStack createBalanceItem(Player target) {
        ItemStack item = new ItemStack(Material.GOLD_INGOT);
        ItemMeta meta = item.getItemMeta();
        meta.displayName(format("§6ʙᴀʟᴀɴᴄᴇ"));

        String balance = replacePlaceholders(target, "$%formatted_balance%");
        String coins = replacePlaceholders(target, "%coins_balance%");

        meta.lore(Arrays.asList(
                format("§7Balance: §a" + balance),
                format("§7Coins: §e" + coins)));
        item.setItemMeta(meta);
        return item;
    }

    private String replacePlaceholders(Player player, String placeholder) {
        if (getServer().getPluginManager().getPlugin("PlaceholderAPI") != null) {
            return me.clip.placeholderapi.PlaceholderAPI.setPlaceholders(player, placeholder);
        }
        return placeholder;
    }

    private ItemStack createPingItem(Player target) {
        ItemStack item = new ItemStack(Material.REDSTONE);
        ItemMeta meta = item.getItemMeta();
        meta.displayName(format("§aᴘɪɴɢ"));

        int ping = target.getPing();
        String color;
        if (ping < 50) {
            color = "§a";
        } else if (ping < 100) {
            color = "§e";
        } else {
            color = "§c";
        }

        meta.lore(Arrays.asList(format("§7Latency: " + color + ping + "ms")));
        item.setItemMeta(meta);
        return item;
    }

    private ItemStack createLevelItem(Player target) {
        ItemStack item = new ItemStack(Material.EXPERIENCE_BOTTLE);
        ItemMeta meta = item.getItemMeta();
        meta.displayName(format("§aʟᴇᴠᴇʟ & ᴇxᴘ"));

        int level = target.getLevel();
        float exp = target.getExp();
        int expPercentage = (int) (exp * 100);

        meta.lore(Arrays.asList(
                format("§7Level: §b" + level),
                format("§7Progress: §2" + expPercentage + "%")));
        item.setItemMeta(meta);
        return item;
    }

    private ItemStack createCloseButton() {
        ItemStack item = new ItemStack(Material.BARRIER);
        ItemMeta meta = item.getItemMeta();
        meta.displayName(format("§cᴄʟᴏꜱᴇ"));
        meta.lore(Arrays.asList(format("§7Click to close")));
        item.setItemMeta(meta);
        return item;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        String title = LegacyComponentSerializer.legacySection().serialize(event.getView().title());
        if (title.contains("§8ꜱᴛᴀᴛꜱ §8»")) {
            event.setCancelled(true);

            // Check if close button was clicked
            if (event.getCurrentItem() != null &&
                    event.getCurrentItem().getType() == Material.BARRIER) {
                event.getWhoClicked().closeInventory();
            }
        }
    }
}