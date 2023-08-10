package org.brokenarrow.lootboxes.untlity;

import org.apache.commons.lang.WordUtils;

public class BountifyStrings {

	/**
	 * Removes _ from the object, lowercases everything and finally capitalizes it
	 *
	 * @param object the enum you want to change.
	 * @return string some has  capitalizes first leter in every word.
	 */
	public static String bountifyCapitalized(Object object) {
		String copy = new String(object.toString());
		return WordUtils.capitalizeFully(replaceSymbole(copy.toLowerCase()));
	}

	/**
	 * Removes _ from the enum, lowercase's everything and finally capitalizes it
	 *
	 * @param enumeration the enum you want to change.
	 * @return string some has capitalizes first leter in every word.
	 */
	public static String bountifyCapitalized(Enum<?> enumeration) {
		return WordUtils.capitalizeFully(replaceSymbole(enumeration.toString().toLowerCase()));
	}

	public static String replaceSymbole(String name) {
		return name.toLowerCase().replace("_", " ");
	}
}
