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
import org.bukkit.scheduler.BukkitTask;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static org.brokenarrow.lootboxes.lootdata.LootItems.YamlKey.GLOBAL_VALUES;
import static org.brokenarrow.lootboxes.lootdata.LootItems.YamlKey.ITEMS;
import static org.brokenarrow.lootboxes.untlity.RunTimedTask.runtaskLater;

public class LootItems extends AllYamlFilesInFolder {

	@Getter
	private static final LootItems instance = new LootItems();

	private File customConfigFile;
	private FileConfiguration customConfig;
	private BukkitTask id;
	private final Map<String, Map<String, LootData>> cachedLoot = new HashMap<>();


	public LootItems() {
		super("tables", true);
	}

	public Map<String, Map<String, LootData>> getCachedLoot() {
		return cachedLoot;
	}

	public Map<String, LootData> getCachedTableContents(String table) {

		return cachedLoot.get(table);
	}

	public void addTable(String table) {
		cachedLoot.put(table, new HashMap<>());
		if (getLootData(table, GLOBAL_VALUES.getKey()) == null) {
			Map<String, LootData> data = new HashMap<>();
			data.put(GLOBAL_VALUES.getKey(), new LootData.Builder()
					.setChance(0)
					.setMinimum(0)
					.setMaximum(1)
					.setMaterial(Material.AIR)
					.setItemdataPath("")
					.setItemdataFileName("")
					.setHaveMetadata(false).build());
			cachedLoot.put(table, data);
		}

		saveTask(table);
	}

	public void setCachedLoot(String lootTable, String lootItem, LootData lootData) {
		Map<String, LootData> lootDataMap = cachedLoot.get(lootTable);
		if (lootDataMap != null)
			lootDataMap.put(lootItem, lootData);
		else
			lootDataMap = Collections.singletonMap(lootItem, lootData);

		cachedLoot.put(lootTable, lootDataMap);
		saveTask(lootTable);
	}

	public String addItems(String table, ItemStack itemStack, String metadatafileName, String itemdataPath, boolean haveMetadata) {
		Map<String, org.brokenarrow.lootboxes.builder.LootData> items = cachedLoot.get(table);

		String loot = getFirstAvailableName(table, itemStack.getType() + "");
		if (items == null) {
			items = new HashMap<>();
		}
		items.put(loot, new org.brokenarrow.lootboxes.builder.LootData.Builder()
				.setChance(1)
				.setMinimum(1)
				.setMaximum(itemStack.getAmount())
				.setMaterial(itemStack.getType())
				.setItemdataPath(itemdataPath)
				.setItemdataFileName(metadatafileName)
				.setHaveMetadata(haveMetadata).build());
		cachedLoot.put(table, items);

		saveTask(table);
		return loot;
	}

	public boolean isCacheItem(String table, String itemPath) {
		Map<String, org.brokenarrow.lootboxes.builder.LootData> data = cachedLoot.get(table);
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
		Map<String, LootData> tableData = cachedLoot.get(table);
		if (tableData != null)
			return tableData.keySet().stream().filter(key -> key != null && !key.equalsIgnoreCase("global_values")).collect(Collectors.toList());
		return null;
	}

	public Material getMaterial(String table, String itemToEdit) {
		Map<String, org.brokenarrow.lootboxes.builder.LootData> items = cachedLoot.get(table);
		if (items != null) {
			return items.get(itemToEdit).getMaterial();
		}
		return null;
	}

	public LootData getLootData(String table, String itemToEdit) {
		Map<String, LootData> dataMap = cachedLoot.get(table);
		if (dataMap != null) {
			return dataMap.get(itemToEdit);
		}
		return null;
	}

	public void setLootData(LootDataSave enums, String table, String itemToEdit, Object object) {
		Map<String, LootData> data = cachedLoot.get(table);
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
		cachedLoot.put(table, data);
		saveTask(table);
	}

	@Override
	public boolean removeFile(String lootTableFileName) {
		runtaskLater(5, () -> {
					final File dataFolder = new File(Lootboxes.getInstance().getDataFolder() + "/tables", lootTableFileName + ".yml");
					dataFolder.delete();
				}
				, true);
		return false;
	}

	public void removeItem(String table, String itemToRemove) {
		Map<String, LootData> items = cachedLoot.get(table);
		if (items != null) {
			items.remove(itemToRemove);
		}
		saveTask(table);
	}

	public ItemStack[] getItems() {
		List<ItemStack> items = new ArrayList<>();
		for (Map<String, LootData> values : cachedLoot.values())
			for (Object key : values.keySet())
				if (key instanceof Material)
					items.add(new ItemStack((Material) key));
		return items.toArray(new ItemStack[0]);
	}

	@Override
	public void reload() {
		if (customConfigFile == null) {
			for (File file : getAllFiles()) {
				customConfigFile = file;

				customConfig = YamlConfiguration.loadConfiguration(customConfigFile);
				getFilesData();
			}
		} else {
			for (File file : getAllFiles()) {
				customConfigFile = file;
				getFilesData();
			}
		}
	}

	@Override
	public void save() {
		save(null);
	}

	public void saveTask(String table) {
		Lootboxes.getInstance().getSaveDataTask().addToSaveCache(this, table);
		/*if (id != null && !id.isCancelled() && (Bukkit.getScheduler().isQueued(id.getTaskId()) || Bukkit.getScheduler().isCurrentlyRunning(id.getTaskId())))
			return;
		id = runtaskLater(5, () -> save(table), true);*/
	}

	@Override
	public void save(String fileToSave) {
		final File dataFolder = new File(Lootboxes.getInstance().getDataFolder(), "tables");
		final File[] dataFolders = dataFolder.listFiles();
		if (dataFolder.exists() && dataFolders != null) {
			if (!checkFolderExist(fileToSave, dataFolders)) {
				final File newDataFolder = new File(Lootboxes.getInstance().getDataFolder() + "/tables", fileToSave + ".yml");
				try {
					newDataFolder.createNewFile();
				} catch (IOException e) {
					e.printStackTrace();
				} finally {
					saveDataToFile(newDataFolder);
				}
			}
			for (File file : dataFolders) {
				String fileName = getNameOfFile(file.getName());

				if (fileToSave == null || fileName.equals(fileToSave)) {
					saveDataToFile(file);
				}

			}
		}
	}

	@Override
	public void saveDataToFile(File file) {
		String fileName = getNameOfFile(file.getName());
		customConfig = YamlConfiguration.loadConfiguration(file);
		Map<String, LootData> settings = this.cachedLoot.get(fileName);
		if (settings != null) {
			for (String childrenKey : settings.keySet()) {
				if (childrenKey == null) continue;
				LootData data = settings.get(childrenKey);
				/*if (!isUpperCase)
					childrenKey = childrenKey.toLowerCase();*/
				//final Material material = (Material) childrenKey;
				if (childrenKey.equalsIgnoreCase("global_values")) {
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

	@Override
	protected void loadSettingsFromYaml(File file) {

	}


	private void getFilesData() {
		try {
			for (File key : getYamlFiles("tables")) {

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
		Map<String, LootData> data = new HashMap<>();
		ConfigurationSection configs = customConfig.getConfigurationSection(ITEMS.getKey());
		if (configs != null)
			for (String childrenKey : configs.getKeys(false)) {
				if (childrenKey == null) continue;
				String path = ITEMS.getKey() + "." + childrenKey;

				int chance = customConfig.getInt(path + ".Chance");
				int minimum = customConfig.getInt(path + ".Minimum");

				int maximum = customConfig.getInt(path + ".Maximum");
				String itemStack = customConfig.getString(path + ".ItemType", "AIR");
				if (itemStack.equals("AIR"))
					itemStack = childrenKey.toUpperCase();
				Material material = Enums.getIfPresent(Material.class, itemStack).orNull();
				boolean haveMetadata = customConfig.getBoolean(path + ".Metadata");
				String itemdata = customConfig.getString(path + ".Itemdata");
				String itemdataFileName = customConfig.getString(path + ".Itemdata_Filename", ItemData.getInstance().getFileName());

				data.put(childrenKey, new LootData.Builder()
						.setChance(chance)
						.setMinimum(minimum)
						.setMaximum(maximum)
						.setMaterial(material)
						.setItemdataPath(itemdata)
						.setItemdataFileName(itemdataFileName)
						.setHaveMetadata(haveMetadata).build());
			}

		String path = GLOBAL_VALUES.getKey();
		int minimum = customConfig.getInt(path + ".Minimum", 0);
		int maximum = customConfig.getInt(path + ".Maximum", 2);

		LootData globalValues = data.get(GLOBAL_VALUES.getKey());
		if (globalValues != null) {
			if (globalValues.getMinimum() > 0)
				minimum = globalValues.getMinimum();
			if (globalValues.getMaximum() > 0)
				maximum = globalValues.getMaximum();
		}
		data.put(path, new LootData.Builder()
				.setChance(0).setMinimum(minimum)
				.setMaximum(maximum)
				.setMaterial(Material.AIR)
				.setHaveMetadata(false).
				build());

		this.cachedLoot.put(getNameOfFile(String.valueOf(key)), data);
	}

	public enum YamlKey {
		GLOBAL_VALUES("Global_Values"),
		ITEMS("Items");
		private final String key;

		YamlKey(String key) {
			this.key = key;
		}

		public String getKey() {
			return key;
		}
	}
}
