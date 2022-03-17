package org.brokenarrow.lootboxes.lootdata;

import com.google.common.base.Enums;
import lombok.Getter;
import org.brokenarrow.lootboxes.Lootboxes;
import org.brokenarrow.lootboxes.settings.AllYamlFilesInFolder;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class LootItems {

	@Getter
	public static final LootItems instance = new LootItems();
	private final AllYamlFilesInFolder yamlFiles;
	private File customConfigFile;
	private FileConfiguration customConfig;
	private boolean isUpperCase;
	private final Map<String, Map<Object, LootData>> settings = new HashMap<>();

	public LootItems() {
		this.yamlFiles = new AllYamlFilesInFolder("tables", true);
	}

	public Map<String, Map<Object, LootData>> getSettings() {
		return settings;
	}

	public void addTable(String table) {
		settings.put(table, new HashMap<>());
	}

	public void addItems(String table, ItemStack itemStack, String fileNameMetadata, boolean haveMetadata) {
		Map<Object, LootData> items = settings.get(table);
		if (items != null) {
			Map<Object, LootData> data = new HashMap<>();
			items.put(itemStack, new LootData(1, 1, itemStack.getAmount(), fileNameMetadata, haveMetadata));
			settings.put(table, items);

		}

	}

	public List<Object> getItems(String table) {
		return settings.get(table).keySet().stream().filter(key -> !key.toString().equalsIgnoreCase("global_values")).collect(Collectors.toList());
	}

	public ItemStack[] getItems() {
		List<ItemStack> items = new ArrayList<>();
		for (Map<Object, LootData> values : settings.values())
			for (Object key : values.keySet())
				if (key instanceof Material)
					items.add(new ItemStack((Material) key));
		return items.toArray(new ItemStack[0]);
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
					Map<Object, LootData> settings = this.settings.get(fileName);
					if (settings != null) {
						for (Object childrenKey : settings.keySet()) {
							System.out.println("key " + fileName + "settings " + childrenKey);
							LootData data = settings.get(childrenKey);
							if (!isUpperCase)
								childrenKey = childrenKey.toString().toLowerCase();
							//final Material material = (Material) childrenKey;
							if (childrenKey.toString().equals("global_values") || childrenKey.toString().equals("GLOBAL_VALUES")) {
								customConfig.set("Global_Values." + ".Minimum", data.getMinimum());
								customConfig.set("Global_Values." + ".Maximum", data.getMaximum());
							} else {
								customConfig.set("Items." + childrenKey + ".Chance", data.getChance());
								customConfig.set("Items." + childrenKey + ".Minimum", data.getMinimum());
								customConfig.set("Items." + childrenKey + ".Maximum", data.getMaximum());
								customConfig.set("Items." + childrenKey + ".Metadata", data.isHaveMetadata());
								customConfig.set("Items." + childrenKey + ".Itemdata", data.getItemdata());
							}
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
		Map<Object, LootData> data = new HashMap<>();
		for (String value : values) {
			ConfigurationSection configs = customConfig.getConfigurationSection(value);

			if (value.equals("Items")) {
				if (configs != null)
					for (String childrenKey : configs.getKeys(false)) {
						if (childrenKey == null) continue;

						String matrial = childrenKey.toUpperCase();
						isUpperCase = childrenKey.equals(matrial);
						Material item = Enums.getIfPresent(Material.class, matrial).orNull();
						if (item == null)
							continue;
						int chance = customConfig.getInt(value + "." + childrenKey + ".Chance");
						int minimum = customConfig.getInt(value + "." + childrenKey + ".Minimum");

						int maximum = customConfig.getInt(value + "." + childrenKey + ".Maximum");
						boolean haveMetadata = customConfig.getBoolean(value + "." + childrenKey + ".Metadata");
						String itemdata = customConfig.getString(value + "." + childrenKey + ".Itemdata");

						data.put(item, new LootData(chance, minimum, maximum, itemdata, haveMetadata));
					}
			} else if (value.equals("Global_Values")) {
				int minimum = customConfig.getInt(value + ".Minimum");
				int maximum = customConfig.getInt(value + ".Maximum");

				LootData globalValues = data.get("Global_Values");
				if (globalValues != null) {
					if (globalValues.getMinimum() > 0)
						minimum = globalValues.getMinimum();
					if (globalValues.getMaximum() > 0)
						maximum = globalValues.getMaximum();
				}

				data.put(value, new LootData(0, minimum, maximum, "", false));
			}
		}
		this.settings.put(this.yamlFiles.getFileName(String.valueOf(key)), data);
		System.out.println("this.settings " + this.settings);
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
