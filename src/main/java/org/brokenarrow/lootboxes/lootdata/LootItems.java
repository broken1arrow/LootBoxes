package org.brokenarrow.lootboxes.lootdata;

import com.google.common.base.Enums;
import lombok.Getter;
import org.brokenarrow.lootboxes.Lootboxes;
import org.brokenarrow.lootboxes.builder.LootData;
import org.brokenarrow.lootboxes.settings.AllYamlFilesInFolder;
import org.brokenarrow.lootboxes.untlity.LootDataSave;
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
	private final Map<String, Map<String, org.brokenarrow.lootboxes.builder.LootData>> settings = new HashMap<>();

	public LootItems() {
		this.yamlFiles = new AllYamlFilesInFolder("tables", true);
	}

	public Map<String, Map<String, org.brokenarrow.lootboxes.builder.LootData>> getSettings() {
		return settings;
	}

	public void addTable(String table) {
		settings.put(table, new HashMap<>());
		save(table);
	}

	public String addItems(String table, ItemStack itemStack, String metadatafileName, String itemdataPath, boolean haveMetadata) {
		Map<String, org.brokenarrow.lootboxes.builder.LootData> items = settings.get(table);

		String loot = getFirstAvailableName(table, itemStack.getType() + "");
		if (items != null) {
			items.put(loot, new org.brokenarrow.lootboxes.builder.LootData.Builder()
					.setChance(1)
					.setMinimum(1)
					.setMaximum(itemStack.getAmount())
					.setMaterial(itemStack.getType())
					.setItemdataPath(itemdataPath)
					.setItemdataFileName(metadatafileName)
					.setHaveMetadata(haveMetadata).build());
		} else {
			items = new HashMap<>();
			items.put(loot, new org.brokenarrow.lootboxes.builder.LootData.Builder()
					.setChance(1)
					.setMinimum(1)
					.setMaximum(itemStack.getAmount())
					.setMaterial(itemStack.getType())
					.setItemdataPath(itemdataPath)
					.setItemdataFileName(metadatafileName)
					.setHaveMetadata(haveMetadata).build());
		}
		settings.put(table, items);
		return loot;
	}

	public boolean isCacheItem(String table, String itemPath) {
		Map<String, org.brokenarrow.lootboxes.builder.LootData> data = settings.get(table);
		if (data != null)
			return data.get(itemPath) != null;
		return false;
	}

	public String getFirstAvailableName(String table, String itemKey) {
		int order = 0;
		while (isCacheItem(table, itemKey + "_" + order))
			order += 1;
		return itemKey + "_" + order;
	}

	public List<String> getItems(String table) {
		return settings.get(table).keySet().stream().filter(key -> key != null && !key.equalsIgnoreCase("global_values")).collect(Collectors.toList());
	}

	public Material getMaterial(String table, String itemToEdit) {
		Map<String, org.brokenarrow.lootboxes.builder.LootData> items = settings.get(table);
		if (items != null) {
			return items.get(itemToEdit).getMaterial();
		}
		return null;
	}

	public LootData getLootData(String table, String itemToEdit) {
		Map<String, LootData> dataMap = settings.get(table);
		if (dataMap != null) {
			return dataMap.get(itemToEdit);
		}
		return null;
	}

	public void setLootData(LootDataSave enums, String table, String itemToEdit, Object object) {
		Map<String, LootData> data = settings.get(table);
		LootData.Builder lootData = data.get(itemToEdit).getBuilder();

		switch (enums) {
			case CHANCE:
				lootData.setChance((int) object);
				break;
			case MIN:
				lootData.setMinimum((int) object);
				break;
			case MAX:
				lootData.setMaximum((int) object);
				break;
			case ITEM:
				lootData.setMaterial((Material) object);
				break;
			case ITEM_DATA_PATH:
				lootData.setItemdataPath((String) object);
				break;
			case META_DATA_FILENAME:
				lootData.setItemdataFileName((String) object);
				break;
			case HAVE_META_DATA:
				lootData.setHaveMetadata((Boolean) object);
				break;
		}
		if (data == null)
			data = new HashMap<>();
		data.put(itemToEdit, lootData.build());
		settings.put(table, data);


	}

	public void removeItem(String table, String itemToRemove) {
		Map<String, org.brokenarrow.lootboxes.builder.LootData> items = settings.get(table);
		if (items != null) {
			items.remove(itemToRemove);
		}
		save(table);
	}

	public ItemStack[] getItems() {
		List<ItemStack> items = new ArrayList<>();
		for (Map<String, org.brokenarrow.lootboxes.builder.LootData> values : settings.values())
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

	public void save() {
		save(null);
	}

	public void save(String fileToSave) {
		final File dataFolder = new File(Lootboxes.getInstance().getDataFolder(), "tables");
		final File[] dataFolders = dataFolder.listFiles();
		if (dataFolder.exists() && dataFolders != null) {
			if (!checkFolderExist(fileToSave, dataFolders)) {
				final File newDataFolder = new File(Lootboxes.getInstance().getDataFolder() + "/tables", fileToSave + ".yml");
				System.out.println("fileToSave " + fileToSave);
				try {
					newDataFolder.createNewFile();
				} catch (IOException e) {
					e.printStackTrace();
				} finally {
					saveDataToFile(newDataFolder);
				}
			}
			for (File file : dataFolders) {
				String fileName = this.yamlFiles.getFileName(file.getName());

				if (fileToSave == null || fileName.equals(fileToSave)) {
					saveDataToFile(file);
				}

			}
		}
	}

	public void saveDataToFile(File file) {
		String fileName = this.yamlFiles.getFileName(file.getName());
		customConfig = YamlConfiguration.loadConfiguration(file);
		Map<String, org.brokenarrow.lootboxes.builder.LootData> settings = this.settings.get(fileName);
		if (settings != null) {
			for (String childrenKey : settings.keySet()) {
				if (childrenKey == null) continue;
				org.brokenarrow.lootboxes.builder.LootData data = settings.get(childrenKey);
				/*if (!isUpperCase)
					childrenKey = childrenKey.toLowerCase();*/
				//final Material material = (Material) childrenKey;
				if (childrenKey.equalsIgnoreCase("global_values") || childrenKey.equals("GLOBAL_VALUES")) {
					customConfig.set("Global_Values." + ".Minimum", data.getMinimum());
					customConfig.set("Global_Values." + ".Maximum", data.getMaximum());
				} else if (data.getMaterial() != null) {
					customConfig.set("Items." + childrenKey + ".ItemType", data.getMaterial().name());
					customConfig.set("Items." + childrenKey + ".Chance", data.getChance());
					customConfig.set("Items." + childrenKey + ".Minimum", data.getMinimum());
					customConfig.set("Items." + childrenKey + ".Maximum", data.getMaximum());
					customConfig.set("Items." + childrenKey + ".Metadata", data.isHaveMetadata());
					customConfig.set("Items." + childrenKey + ".Itemdata", data.getItemdataPath());
					customConfig.set("Items." + childrenKey + ".Itemdata_Filename", data.getItemdataFileName());
				}
				try {
					customConfig.save(file);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public boolean checkFolderExist(String fileToSave, File[] dataFolders) {
		if (fileToSave != null)
			for (File file : dataFolders) {
				String fileName = this.yamlFiles.getFileName(file.getName());
				if (fileName.equals(fileToSave))
					return true;
			}
		return false;
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
		Map<String, org.brokenarrow.lootboxes.builder.LootData> data = new HashMap<>();
		for (String value : values) {
			ConfigurationSection configs = customConfig.getConfigurationSection(value);

			if (value.equals("Items")) {
				if (configs != null)
					for (String childrenKey : configs.getKeys(false)) {
						if (childrenKey == null) continue;

						int chance = customConfig.getInt(value + "." + childrenKey + ".Chance");
						int minimum = customConfig.getInt(value + "." + childrenKey + ".Minimum");

						int maximum = customConfig.getInt(value + "." + childrenKey + ".Maximum");
						String itemStack = customConfig.getString(value + "." + childrenKey + ".ItemType", "AIR");
						if (itemStack.equals("AIR"))
							itemStack = new String(childrenKey).toUpperCase();
						Material material = Enums.getIfPresent(Material.class, itemStack).orNull();
						boolean haveMetadata = customConfig.getBoolean(value + "." + childrenKey + ".Metadata");
						String itemdata = customConfig.getString(value + "." + childrenKey + ".Itemdata");
						String itemdataFileName = customConfig.getString(value + "." + childrenKey + ".Itemdata_Filename", ItemData.getInstance().getFileName());

						data.put(childrenKey, new org.brokenarrow.lootboxes.builder.LootData.Builder()
								.setChance(chance)
								.setMinimum(minimum)
								.setMaximum(maximum)
								.setMaterial(material)
								.setItemdataPath(itemdata)
								.setItemdataFileName(itemdataFileName)
								.setHaveMetadata(haveMetadata).build());
					}
			} else if (value.equals("Global_Values")) {
				int minimum = customConfig.getInt(value + ".Minimum");
				int maximum = customConfig.getInt(value + ".Maximum");

				org.brokenarrow.lootboxes.builder.LootData globalValues = data.get("Global_Values");
				if (globalValues != null) {
					if (globalValues.getMinimum() > 0)
						minimum = globalValues.getMinimum();
					if (globalValues.getMaximum() > 0)
						maximum = globalValues.getMaximum();
				}

				data.put(value, new org.brokenarrow.lootboxes.builder.LootData.Builder()
						.setChance(0)
						.setMinimum(minimum)
						.setMaximum(maximum)
						.setMaterial(Material.AIR)
						.setItemdataPath("")
						.setItemdataFileName("")
						.setHaveMetadata(false).build());
			}
		}
		this.settings.put(this.yamlFiles.getFileName(String.valueOf(key)), data);
	}
/*
	public static class LootData {
		private final int chance;
		private final int minimum;
		private final int maximum;
		private final Material material;
		private final String itemdataPath;
		private final String itemdataFileName;
		private final boolean haveMetadata;

		public LootData(int chance, int minimum, int maximum, Material material, String itemdataPath, String itemdataFileName, boolean haveMetadata) {
			this.chance = chance;
			this.minimum = minimum;
			this.maximum = maximum;
			this.material = material;
			this.itemdataPath = itemdataPath;
			this.itemdataFileName = itemdataFileName;
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

		public Material getMaterial() {
			return material;
		}

		public boolean isHaveMetadata() {
			return haveMetadata;
		}

		public String getItemdataPath() {
			return itemdataPath;
		}

		public String getItemdataFileName() {
			return itemdataFileName;
		}

		public String getString() {
			return "{" +
					"\t'chance '=' " + chance + "'\n" +
					"\t'minimum '=' " + minimum + "'\n" +
					"\t'maximum '=' " + maximum + "'\n" +
					"\t'itemdata '=' " + itemdataPath + "'\n" +
					"\t'haveMetadata '=' " + haveMetadata + "'\n" +
					'}';
		}

	}*/
}
