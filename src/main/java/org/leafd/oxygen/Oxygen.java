package org.leafd.oxygen;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class Oxygen extends JavaPlugin implements Listener {

    private Map<UUID, Integer> oxygenLevels = new HashMap<>();
    private final int maxOxygen = 100;
    private final int damageAmount = 1; // Amount of damage

    @Override
    public void onEnable() {

        getLogger().info("Oxygen plugin has been enabled!");


        loadOxygenLevels();


        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    UUID uuid = player.getUniqueId();
                    int oxygen = oxygenLevels.getOrDefault(uuid, maxOxygen);


                    if (player.hasPotionEffect(PotionEffectType.WATER_BREATHING)) {

                        int newOxygen = oxygen + 10;
                        oxygenLevels.put(uuid, Math.min(newOxygen, maxOxygen));
                    } else {

                        if (oxygen > 0) {
                            oxygen--;
                            oxygenLevels.put(uuid, oxygen);


                            if (oxygen <= 0) {
                                player.damage(damageAmount);
                            }
                        } else {

                            player.damage(damageAmount);
                        }
                    }
                }
            }
        }.runTaskTimer(this, 20L, 20L); // Delay CHANGE THIS

        // Register events
        Bukkit.getServer().getPluginManager().registerEvents(this, this);

        // Register PlaceholderAPI placeholders
        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            new OxygenPlaceholder(this).register();
        } else {
            getLogger().warning("PlaceholderAPI not found! Placeholder integration disabled.");
        }
    }

    @Override
    public void onDisable() {

        getLogger().info("Oxygen plugin has been disabled!");

        // Save oxygen levels
        saveOxygenLevels();
    }


    public int getOxygenLevel(Player player) {
        UUID uuid = player.getUniqueId();
        return oxygenLevels.getOrDefault(uuid, maxOxygen);
    }


    private void loadOxygenLevels() {
        if (getConfig().contains("oxygenLevels")) {
            oxygenLevels.clear();
            for (String key : getConfig().getConfigurationSection("oxygenLevels").getKeys(false)) {
                UUID uuid = UUID.fromString(key);
                int oxygen = getConfig().getInt("oxygenLevels." + key);
                oxygenLevels.put(uuid, oxygen);
            }
        }
    }


    private void saveOxygenLevels() {
        getConfig().set("oxygenLevels", null);
        for (Map.Entry<UUID, Integer> entry : oxygenLevels.entrySet()) {
            getConfig().set("oxygenLevels." + entry.getKey().toString(), entry.getValue());
        }
        saveConfig();
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {

        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        int oxygen = getConfig().getInt("oxygenLevels." + uuid.toString(), maxOxygen);
        oxygenLevels.put(uuid, oxygen);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {

        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        if (oxygenLevels.containsKey(uuid)) {
            getConfig().set("oxygenLevels." + uuid.toString(), oxygenLevels.get(uuid));
            saveConfig();
            oxygenLevels.remove(uuid);
        }
    }
}
