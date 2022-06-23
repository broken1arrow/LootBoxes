package org.brokenarrow.lootboxes.untlity;

import org.brokenarrow.lootboxes.Lootboxes;
import org.brokenarrow.lootboxes.settings.Settings;

public class DebugMessages {
	private static final Settings settings = Lootboxes.getInstance().getSettings();

	public static void sendDebug(String message, Class<?> clazz) {
		if (settings.getSettings().isDebug()) {
			System.out.println("[Lootboxes] " + clazz + " >" + message);
		}
	}
}
