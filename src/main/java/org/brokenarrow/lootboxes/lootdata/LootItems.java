package org.brokenarrow.lootboxes.lootdata;

import org.brokenarrow.lootboxes.settings.AllYamlFilesInFolder;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class LootItems {


	private final AllYamlFilesInFolder yamlFiles;
	private File customConfigFile;
	private FileConfiguration customConfig;
	private final Map<String, Map<String, LootData>> settings = new HashMap<>();

	public LootItems() {
		this.yamlFiles = new AllYamlFilesInFolder("tables", true);
	}

	public void reload() {
		if (customConfigFile == null) {
			for (File file : this.yamlFiles.reload()) {
				customConfigFile = file;

				customConfig = YamlConfiguration.loadConfiguration(customConfigFile);
				getFilesData();
			}
		} else {
			for (File file : this.yamlFiles.reload()) {
				customConfigFile = file;
				getFilesData();
			}
		}
	}

	private void getFilesData() {
		try {
			for (File key : yamlFiles.getYamlFiles("tables", "yml")) {

				customConfig.load(key);
				Set<String> value = customConfig.getKeys(false);
				loadSettingsFromYaml(key, value);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InvalidConfigurationException e) {
			e.printStackTrace();
		}
	}

	protected void loadSettingsFromYaml(File key, Set<String> value) {
		System.out.println("Test ndddddd " + value);
		Map<String, LootData> data = new HashMap<>();
		for (String val : value) {
			ConfigurationSection configs = customConfig.getConfigurationSection(val);

			if (configs != null)
				for (String va : configs.getKeys(false)) {
					System.out.println("Test 4444444ndddddd " + va);
					int chance = customConfig.getInt(va + ".chance");
					int minimum = customConfig.getInt(va + ".minimum");
					
					int maximum = customConfig.getInt(va + ".maximum");
					boolean haveMetadata = customConfig.getBoolean(va + ".metadata");
					String itemdata = customConfig.getString(va + ".itemdata");

					data.put(va, new LootData(chance, minimum, maximum, itemdata, haveMetadata));
				}
			int minimum = 0;
			int maximum = 0;
			if (val.equals("minimum"))
				minimum = customConfig.getInt(val);
			else if (val.equals("maximum"))
				maximum = customConfig.getInt(val);
			LootData globalValues = data.get("Global_Values");
			if (globalValues != null) {
				if (globalValues.getMinimum() > 0)
					minimum = globalValues.getMinimum();
				if (globalValues.getMaximum() > 0)
					maximum = globalValues.getMaximum();
			}

			data.put("Global_Values", new LootData(0, minimum, maximum, "", false));
		}
		this.settings.put(this.yamlFiles.getFileName(String.valueOf(key)), data);
		System.out.println("settings map " + this.settings);
		System.out.println("settings map " + this.settings.get("test").get("Global_Values").getMinimum());
		System.out.println("settings map " + this.settings.get("test").get("Global_Values").getMaximum());

	}

	public static class LootData {
		private final int chance;
		private final int minimum;
		private final int maximum;
		private final String itemdata;
		private final boolean haveMetadata;

		public LootData(int chance, int minimum, int maximum, String itemdata, boolean haveMetadata) {
			this.chance = chance;
			this.minimum = minimum;
			this.maximum = maximum;
			this.itemdata = itemdata;
			this.haveMetadata = haveMetadata;
		}

		public int getChance() {
			return chance;
		}

		public int getMinimum() {
			return minimum;
		}

		public int getMaximum() {
			return maximum;
		}

		public boolean isHaveMetadata() {
			return haveMetadata;
		}

		public String getItemdata() {
			return itemdata;
		}
	}
}
