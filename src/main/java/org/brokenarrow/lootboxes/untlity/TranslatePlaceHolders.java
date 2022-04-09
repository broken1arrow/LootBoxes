package org.brokenarrow.lootboxes.untlity;

import java.util.List;

public class TranslatePlaceHolders {


	public static boolean checkListForPlaceholdersAndTranslate(List<String> lores, String lore, Object... placeholders) {
		int number = containsList(placeholders);
		if (number < 0) return false;

		if (lore.contains("{" + number + "}")) {
			for (Object text : (List<?>) placeholders[number])
				lores.add(lore.replace(("{" + number + "}"), (String) text));
			return true;
		}
		return false;
	}

	public static int containsList(Object... placeholders) {
		if (placeholders != null)
			for (int i = 0; i < placeholders.length; i++)
				if (placeholders[i] instanceof List)
					return i;
		return -1;
	}

	public static String translatePlaceholders(String rawText, Object... placeholders) {

		if (placeholders != null)
			for (int i = 0; i < placeholders.length; i++) {
				if (placeholders[i] instanceof List)
					continue;
				rawText = rawText.replace("{" + i + "}", placeholders[i] != null ? placeholders[i].toString() : "");
			}
		return ifContainsBoolen(rawText);
	}

	private static String ifContainsBoolen(String text) {
		if (text.contains("true"))
			return text.replace("true", "yes");//ChatMessages.BOOLEAN_TRUE.languageMessages());
		else if (text.contains("false"))
			return text.replace("false", "no");//ChatMessages.BOOLEAN_FALSE.languageMessages());
		else
			return text;
	}

}