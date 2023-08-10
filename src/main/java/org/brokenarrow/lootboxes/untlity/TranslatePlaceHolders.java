package org.brokenarrow.lootboxes.untlity;

import me.clip.placeholderapi.PlaceholderAPI;
import org.brokenarrow.lootboxes.Lootboxes;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class TranslatePlaceHolders {

	public static List<String> translatePlaceholdersLore(Player player, List<String> lores, Object... placeholders) {
		if (!Lootboxes.getInstance().isPlaceholderAPIMissing())
			return translatePlaceholdersLore(PlaceholderAPI.setPlaceholders(player, lores), placeholders);
		return translatePlaceholdersLore(lores, placeholders);
	}
	public static List<String> translatePlaceholdersLore(List<String> lores, Object... placeholders) {
		if (lores == null) return new ArrayList<>();
		List<String> clonedLores = new ArrayList<>(lores);
		List<String> list = new ArrayList<>();
		for (String lore : clonedLores) {
			if (!checkListForPlaceholdersAndTranslate(list, lore, placeholders))
				list.add(translatePlaceholders(lore, placeholders));
		}
		return list;
	}

	public static boolean checkListForPlaceholdersAndTranslate(List<String> lores, String lore, Object... placeholders) {
		int number = containsList(placeholders);
		if (number < 0) return false;
		if (lore.contains("{" + number + "}")) {
			for (Object text : (List<?>) placeholders[number]) {
				lores.add(lore.replace(("{" + number + "}"), text + ""));
			}
			return true;
		}
		return false;
	}

	public static Object[] getPlaceholders(Object... placeholder) {
		return placeholder;
	}
	public static int containsList(Object... placeholders) {
		if (placeholders != null)
			for (int i = 0; i < placeholders.length; i++)
				if (placeholders[i] instanceof List)
					return i;
		return -1;
	}

	public static String translatePlaceholders(Player player, String rawText, Object... placeholders) {
		if (!Lootboxes.getInstance().isPlaceholderAPIMissing())
			return translatePlaceholders(PlaceholderAPI.setPlaceholders(player, rawText), placeholders);
		return translatePlaceholders(rawText, placeholders);
	}

	public static String translatePlaceholders(String rawText, Object... placeholders) {
		if (placeholders != null)
			for (int i = 0; i < placeholders.length; i++) {
				if (placeholders[i] instanceof List)
					continue;
				if (rawText == null) continue;
				rawText = rawText.replace("{" + i + "}", placeholders[i] != null ? placeholders[i].toString() : "");
			}
		return ifContainsBoolen(rawText);
	}

	private static String ifContainsBoolen(String text) {
		if (text != null)
			if (text.contains("true"))
				return text.replace("true", "yes");//ChatMessages.BOOLEAN_TRUE.languageMessages());
			else if (text.contains("false"))
				return text.replace("false", "no");//ChatMessages.BOOLEAN_FALSE.languageMessages());
			else
				return text;
		return "";
	}

}
