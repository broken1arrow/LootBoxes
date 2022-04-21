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

	@Override
	public void reload() {
		super.reload();
	}

	public SettingsData getSettings() {
		return settings.get("Settings");
	}

	@Override
	protected void loadSettingsFromYaml() {

		int amountOfBlocksBelowSurface = customConfig.getInt("Amount_Of_Blocks_Below_Surface");
		boolean spawnOnSurface = customConfig.getBoolean("Spawn_On_Surface");
		boolean warnBeforeSaveWithMetadata = customConfig.getBoolean("Warn_Before_Save_With_Metadata");
		boolean randomContinerSpawn = customConfig.getBoolean("Random_Continer_Spawn");
		boolean saveMetadataOnItem = customConfig.getBoolean("Save_Metadata_On_Item");
		int increse = customConfig.getInt("Max_and_min_amount.Increse_with");
		int decrese = customConfig.getInt("Max_and_min_amount.Decrese_with");
		String language = customConfig.getString("Language");
		SettingsData settingsData = new SettingsData.Builder()
				.setAmountOfBlocksBelowSurface(amountOfBlocksBelowSurface)
				.setWarnBeforeSaveWithMetadata(warnBeforeSaveWithMetadata)
				.setSaveMetadataOnItem(saveMetadataOnItem)
				.setSpawnOnSurface(spawnOnSurface)
				.setRandomContinerSpawn(randomContinerSpawn)
				.setIncrese(increse)
				.setDecrese(decrese)
				.setLanguage(language)
				.build();

		settings.put("Settings", settingsData);
	}

	public static Settings getInstance() {
		return instance;
	}
}
