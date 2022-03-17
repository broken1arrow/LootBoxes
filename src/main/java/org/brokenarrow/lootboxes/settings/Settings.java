package org.brokenarrow.lootboxes.settings;

import org.brokenarrow.lootboxes.builder.SettingsData;

import java.util.HashMap;
import java.util.Map;

public class Settings extends YamlUtil {

	private static final Settings instance = new Settings();

	public Settings() {
		super("Settings.yml", "Settings.yml");
	}

	private final Map<String, SettingsData> settings = new HashMap<>();


	public SettingsData getSettings() {
		return settings.get("Settings");
	}

	@Override
	protected void loadSettingsFromYaml() {

		int amountOfBlocksBelowSurface = customConfig.getInt("Amount_Of_Blocks_Below_Surface");
		boolean spawnOnSurface = customConfig.getBoolean("Spawn_On_Surface");
		boolean warnBeforeSaveWithMetadata = customConfig.getBoolean("Warn_Before_Save_With_Metadata");
		boolean saveMetadataOnItem = customConfig.getBoolean("Save_Metadata_On_Item");

		SettingsData settingsData = new SettingsData.Builder()
				.setAmountOfBlocksBelowSurface(amountOfBlocksBelowSurface)
				.setWarnBeforeSaveWithMetadata(warnBeforeSaveWithMetadata)
				.setSaveMetadataOnItem(saveMetadataOnItem)
				.setSpawnOnSurface(spawnOnSurface).build();

		settings.put("Settings", settingsData);
	}

	public static Settings getInstance() {
		return instance;
	}
}
