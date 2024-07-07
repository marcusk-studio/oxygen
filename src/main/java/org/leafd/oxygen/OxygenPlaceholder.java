package org.leafd.oxygen;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class OxygenPlaceholder extends PlaceholderExpansion {

    private final Oxygen plugin;

    public OxygenPlaceholder(Oxygen plugin) {
        this.plugin = plugin;
    }

    @Override
    public @NotNull String getIdentifier() {
        return "oxygen";
    }

    @Override
    public @NotNull String getAuthor() {
        return plugin.getDescription().getAuthors().toString();
    }

    @Override
    public @NotNull String getVersion() {
        return plugin.getDescription().getVersion();
    }

    @Override
    public String onPlaceholderRequest(Player player, @NotNull String identifier) {
        if (player == null) {
            return "";
        }

        // Check if the requested placeholder is our oxygen level
        if (identifier.equals("level")) {
            int oxygenLevel = plugin.getOxygenLevel(player);
            return String.valueOf(oxygenLevel);
        }

        return null;
    }
}
