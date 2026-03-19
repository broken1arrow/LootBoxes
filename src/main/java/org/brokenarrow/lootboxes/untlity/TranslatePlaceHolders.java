package org.brokenarrow.lootboxes.untlity;

import me.clip.placeholderapi.PlaceholderAPI;
import org.brokenarrow.lootboxes.Lootboxes;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class TranslatePlaceHolders {
    private static final Lootboxes plugin = Lootboxes.getInstance();

    public static String getDisplayName(final Player player, final String text, final Object... placeholder) {
        if (player != null)
            if (!plugin.isPlaceholderAPIMissing())
                return PlaceholderAPI.setPlaceholders(player, getDisplayName(text,  placeholder));
        return getDisplayName(text, placeholder);
    }

    public static String getDisplayName(final String displayName, final Object... placeholders) {
        if (displayName != null) {
            return translatePlaceholders(displayName, placeholders);
        }
        return "";
    }

    public static List<String> getLore(final Player player, final List<String> stringList, final Object... placeholder) {
        if (player != null)
            if (!plugin.isPlaceholderAPIMissing())
                return PlaceholderAPI.setPlaceholders(player, getLore(stringList, placeholder));
        return getLore(stringList, placeholder);
    }

    public static List<String> getLore(List<String> stringList, final Object... placeholder) {
        if(stringList == null)
            return new ArrayList<>();
        final List<String> loreList = new ArrayList<>();
        for (final String lore : stringList) {
            final int index = containsList(placeholder);
            if (index != -1 && lore.contains("{" + index + "}"))
                for (final String text : (List<String>) placeholder[index])
                    loreList.add(lore.replace("{" + index + "}", text));
            else
                loreList.add(translatePlaceholders(lore, placeholder));
        }
        return loreList;
    }

    public static int containsList(final Object... placeholder) {
        if (placeholder != null)
            for (int i = 0; i < placeholder.length; i++)
                if (placeholder[i] instanceof List)
                    return i;
        return -1;
    }

    public static String translatePlaceholders(String rawText, @Nullable final Object... placeholders) {

        if (placeholders != null)
            for (int i = 0; i < placeholders.length; i++) {
                if (placeholders[i] instanceof List)
                    continue;
                rawText = rawText.replace("{" + i + "}", placeholders[i] != null ? placeholders[i].toString() : "");
            }
        return ChatColor.translateAlternateColorCodes('&', rawText);
    }

    public static String getTitle(final Player player, final String menuTitle, final Object... placeholders) {
        if (menuTitle != null) {
            if (player != null)
                if (!plugin.isPlaceholderAPIMissing())
                    return PlaceholderAPI.setPlaceholders(player, translatePlaceholders(menuTitle, placeholders));
            return translatePlaceholders(menuTitle, placeholders);
        }
        return "";
    }


    public static Object[] getPlaceholders(Object... placeholders) {
        return placeholders;
    }
}
