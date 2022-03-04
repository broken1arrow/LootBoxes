package org.brokenarrow.lootboxes.lootdata;

import org.brokenarrow.lootboxes.Lootboxes;
import org.brokenarrow.lootboxes.settings.AllYamlFilesInFolder;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.*;

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

	public void save(String fileToSave) {
		final File dataFolder = new File(Lootboxes.getInstance().getDataFolder(), "tables");
		final File[] dataFolders = dataFolder.listFiles();
		if (dataFolder.exists() && dataFolders != null) {
			for (File file : dataFolders) {
				String fileName = this.yamlFiles.getFileName(file.getName());

				if (fileName.equals(fileToSave)) {
					customConfig = YamlConfiguration.loadConfiguration(file);
					Map<String, LootData> settings = this.settings.get(fileName);
					if (settings != null) {
						for (String keys : settings.keySet()) {
							System.out.println("key " + fileName + "settings " + keys + settings.get(keys).getString());

							customConfig.set("items." + keys + ".chance", settings.get(keys).getChance());
							customConfig.set("items." + keys + ".minimum", settings.get(keys).getMinimum());
							customConfig.set("items." + keys + ".maximum", settings.get(keys).getMaximum());
							customConfig.set("items." + keys + ".haveMetadata", settings.get(keys).getItemdata());
							customConfig.set("items." + keys + ".itemdata", settings.get(keys).getItemdata());
							try {
								customConfig.save(file);
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
					}
				}

			}
		}
	}

	public static String toStringFormatted(Map<?, ?> serialize) {
		final List<String> lines = new ArrayList<>();

		lines.add("{");

		for (final Map.Entry<?, ?> entry : serialize.entrySet()) {
			final Object value = entry.getValue();

			if (value != null && !value.toString().equals("[]") && !value.toString().equals("{}") && !value.toString().isEmpty() && !value.toString().equals("0.0") && !value.toString().equals("false"))

				lines.add("\t'" + entry.getKey() + "' = '" + value + "'");
		}

		lines.add("}");

		return String.join("\n", lines);
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

	protected void loadSettingsFromYaml(File key, Set<String> values) {
		System.out.println("Test ndddddd " + values);
		Map<String, LootData> data = new HashMap<>();
		for (String value : values) {
			ConfigurationSection configs = customConfig.getConfigurationSection(value);

			if (configs != null)
				for (String childrenKey : configs.getKeys(false)) {
					System.out.println("Test 4444444ndddddd " + childrenKey);
					int chance = customConfig.getInt(value + "." + childrenKey + ".chance");
					int minimum = customConfig.getInt(value + "." + childrenKey + ".minimum");

					int maximum = customConfig.getInt(value + "." + childrenKey + ".maximum");
					boolean haveMetadata = customConfig.getBoolean(value + "." + childrenKey + ".metadata");
					String itemdata = customConfig.getString(value + "." + childrenKey + ".itemdata");
					System.out.println("chance " + chance);
					data.put(childrenKey, new LootData(chance, minimum, maximum, itemdata, haveMetadata));
				}
			int minimum = 0;
			int maximum = 0;
			if (value.equals("minimum"))
				minimum = customConfig.getInt(value);
			else if (value.equals("maximum"))
				maximum = customConfig.getInt(value);
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
		save("test");
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

		public String getString() {
			return "{" +
					"\t'chance '=' " + chance + "'\n" +
					"\t'minimum '=' " + minimum + "'\n" +
					"\t'maximum '=' " + maximum + "'\n" +
					"\t'itemdata '=' " + itemdata + "'\n" +
					"\t'haveMetadata '=' " + haveMetadata + "'\n" +
					'}';
		}

	}
}
