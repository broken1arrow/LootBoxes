package org.brokenarrow.lootboxes.settings;

import org.brokenarrow.lootboxes.builder.SettingsData;

import java.util.HashMap;
import java.util.Map;

public class Settings extends YamlUtil {

	private static final Settings instance = new Settings();

	public Settings() {
		super("settings.yml", "settings.yml");
	}

	private final Map<String, SettingsData> settings = new HashMap<>();


	public SettingsData getSettings() {
		return settings.get("Settings");
	}

	@Override
	protected void loadSettingsFromYaml() {

		int amountOfBlocksBelowSurface = customConfig.getInt("Amount_Of_Blocks_Below_Surface");
		boolean spawnOnSurface = customConfig.getBoolean("Spawn_On_Surface");

		SettingsData settingsData = new SettingsData.Builder()
				.setAmountOfBlocksBelowSurface(amountOfBlocksBelowSurface)
				.setSpawnOnSurface(spawnOnSurface).build();

		settings.put("Settings", settingsData);
	}

	public static Settings getInstance() {
		return instance;
	}
}
