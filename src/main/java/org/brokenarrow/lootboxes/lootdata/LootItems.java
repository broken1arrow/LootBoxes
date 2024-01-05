package org.brokenarrow.lootboxes.lootdata;

import com.google.common.base.Enums;
import org.broken.arrow.yaml.library.YamlFileManager;
import org.broken.arrow.yaml.library.utillity.ConfigurationWrapper;
import org.brokenarrow.lootboxes.Lootboxes;
import org.brokenarrow.lootboxes.builder.LootData;
import org.brokenarrow.lootboxes.untlity.LootDataSave;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.brokenarrow.lootboxes.lootdata.LootItems.YamlKey.GLOBAL_VALUES;
import static org.brokenarrow.lootboxes.lootdata.LootItems.YamlKey.ITEMS;
import static org.brokenarrow.lootboxes.untlity.RunTimedTask.runtaskLater;

public class LootItems extends YamlFileManager {


	private static final LootItems instance = new LootItems();

	private File customConfigFile;
	private FileConfiguration customConfig;
	private BukkitTask id;
	private final Map<String, Map<String, LootData>> cachedLoot = new HashMap<>();


	public LootItems() {
		super(Lootboxes.getInstance(),"tables",false,true);
	}

	public Map<String, Map<String, LootData>> getCachedLoot() {
		return cachedLoot;
	}

	public Map<String, LootData> getCachedTableContents(final String table) {

		return cachedLoot.get(table);
	}

	public static LootItems getInstance() {
		return instance;
	}

	public void addTable(final String table) {
		cachedLoot.put(table, new HashMap<>());
		if (getLootData(table, GLOBAL_VALUES.getKey()) == null) {
			final Map<String, LootData> data = new HashMap<>();
			data.put(GLOBAL_VALUES.getKey(), new LootData.Builder()
					.setChance(0)
					.setMinimum(0)
					.setMaximum(1)
					.setMaterial(Material.AIR)
					.setItemDataPath("")
					.setLootTableName("")
					.setHaveMetadata(false).build());
			cachedLoot.put(table, data);
		}

		saveTask(table);
	}

	public void setCachedLoot(final String lootTable, final String lootItem, final LootData lootData) {
		Map<String, LootData> lootDataMap = cachedLoot.get(lootTable);
		if (lootDataMap == null)
			lootDataMap = new HashMap<>();
		lootDataMap.put(lootItem, lootData);
		cachedLoot.put(lootTable, lootDataMap);
		saveTask(lootTable);
	}

	public String addItems(final String table, final ItemStack itemStack, final String lootTableName, final String itemDataPath, final boolean haveMetadata) {
		Map<String, org.brokenarrow.lootboxes.builder.LootData> items = cachedLoot.get(table);

		final String loot = getFirstAvailableName(table, itemStack.getType() + "");
		if (items == null) {
			items = new HashMap<>();
		}
		items.put(loot, new org.brokenarrow.lootboxes.builder.LootData.Builder()
				.setChance(1)
				.setMinimum(1)
				.setMaximum(itemStack.getAmount())
				.setMaterial(itemStack.getType())
				.setItemDataPath(itemDataPath)
				.setLootTableName(lootTableName)
				.setHaveMetadata(haveMetadata).build());
		cachedLoot.put(table, items);

		saveTask(table);
		return loot;
	}

	public boolean isCacheItem(final String table, final String itemPath) {
		final Map<String, org.brokenarrow.lootboxes.builder.LootData> data = cachedLoot.get(table);
		return data != null && data.get(itemPath) != null;

	}

	public String getFirstAvailableName(final String table, final String itemKey) {
		int order = 0;
		while (isCacheItem(table, itemKey + "_" + order))
			order += 1;
		return itemKey + "_" + order;
	}

	public List<String> getItems(final String table) {
		final Map<String, LootData> tableData = cachedLoot.get(table);
		if (tableData != null)
			return tableData.keySet().stream().filter(key -> key != null && !key.equalsIgnoreCase("global_values")).collect(Collectors.toList());
		return null;
	}

	public Material getMaterial(final String table, final String itemToEdit) {
		final Map<String, org.brokenarrow.lootboxes.builder.LootData> items = cachedLoot.get(table);
		if (items != null) {
			return items.get(itemToEdit).getMaterial();
		}
		return null;
	}

	public LootData getLootData(final String table, final String itemToEdit) {
		final Map<String, LootData> dataMap = cachedLoot.get(table);
		if (dataMap != null) {
			return dataMap.get(itemToEdit);
		}
		return null;
	}

	public void setLootData(final LootDataSave enums, final String table, final String itemToEdit, final Object object) {
		Map<String, LootData> data = cachedLoot.get(table);
		final LootData.Builder lootData = data.get(itemToEdit).getBuilder();

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
				lootData.setItemDataPath((String) object);
				break;
			case META_DATA_FILENAME:
				lootData.setLootTableName((String) object);
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

	public boolean removeLootTable(final String fileName) {
		runtaskLater(5, () -> removeFile(fileName), true);
		return false;
	}

	public void removeItem(final String table, final String itemToRemove) {
		final Map<String, LootData> items = cachedLoot.get(table);
		if (items != null) {
			items.remove(itemToRemove);
		}
		saveTask(table);
	}

	public ItemStack[] getItems() {
		final List<ItemStack> items = new ArrayList<>();
		for (final Map<String, LootData> values : cachedLoot.values())
			for (final Object key : values.keySet())
				if (key instanceof Material)
					items.add(new ItemStack((Material) key));
		return items.toArray(new ItemStack[0]);
	}

	public void saveTask(final String table) {
		Lootboxes.getInstance().getSaveDataTask().addToSaveCache(this, table);
	}

	/**
	 * Subclasses must implement this method to save data to the specified file.
	 *
	 * @param file                 the file to which the data should be saved
	 * @param configurationWrapper the wrapper that allows you to set the path individually
	 *                             for every map or cached list. This capability enables you
	 *                             to customize the save path for different data sets
	 */
	@Override
	protected void saveDataToFile(@NotNull final File file, @NotNull final ConfigurationWrapper configurationWrapper) {
		final String fileName = getNameOfFile(file.getName());
		customConfig = YamlConfiguration.loadConfiguration(file);
		final Map<String, LootData> settings = this.cachedLoot.get(fileName);
		if (settings != null) {
			customConfig.set("Items", null);
			for (final String childrenKey : settings.keySet()) {
				if (childrenKey == null) continue;
				final LootData data = settings.get(childrenKey);
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
					customConfig.set("Items." + childrenKey + ".Itemdata", data.getItemDataPath());
					customConfig.set("Items." + childrenKey + ".Itemdata_Filename", data.getLootTableName());
				}
				try {
					customConfig.save(file);
				} catch (final IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	protected void loadSettingsFromYaml(final File key,FileConfiguration configuration) {

		final Map<String, LootData> data = new HashMap<>();
		FileConfiguration customConfig = this.getCustomConfig();
		final ConfigurationSection configs = customConfig.getConfigurationSection(ITEMS.getKey());
		if (configs != null)
			for (final String childrenKey : configs.getKeys(false)) {
				if (childrenKey == null) continue;
				final String path = ITEMS.getKey() + "." + childrenKey;

				final int chance = customConfig.getInt(path + ".Chance");
				final int minimum = customConfig.getInt(path + ".Minimum");

				final int maximum = customConfig.getInt(path + ".Maximum");
				String itemStack = customConfig.getString(path + ".ItemType", "AIR");
				if (itemStack.equals("AIR"))
					itemStack = childrenKey.toUpperCase();
				final Material material = Enums.getIfPresent(Material.class, itemStack).orNull();
				final boolean haveMetadata = customConfig.getBoolean(path + ".Metadata");
				final String itemdata = customConfig.getString(path + ".Itemdata");
				final String itemdataFileName = customConfig.getString(path + ".Itemdata_Filename", ItemData.getInstance().getFileName());

				data.put(childrenKey, new LootData.Builder()
						.setChance(chance)
						.setMinimum(minimum)
						.setMaximum(maximum)
						.setMaterial(material)
						.setItemDataPath(itemdata)
						.setLootTableName(itemdataFileName)
						.setHaveMetadata(haveMetadata).build());
			}
		final String path = GLOBAL_VALUES.getKey();
		int minimum = customConfig.getInt(path + ".Minimum", 0);
		int maximum = customConfig.getInt(path + ".Maximum", 2);

		final LootData globalValues = data.get(GLOBAL_VALUES.getKey());
		if (globalValues != null) {
			if (globalValues.getMinimum() > 0)
				minimum = globalValues.getMinimum();
			if (globalValues.getMaximum() > 0)
				maximum = globalValues.getMaximum();
		}
		data.put(path, new LootData.Builder()
				.setChance(0)
				.setMinimum(minimum)
				.setMaximum(maximum)
				.setMaterial(Material.AIR)
				.setHaveMetadata(false).
				build());
		this.cachedLoot.put(getNameOfFile(key.getName()), data);
	}

	public enum YamlKey {
		GLOBAL_VALUES("Global_Values"),
		ITEMS("Items");
		private final String key;

		YamlKey(final String key) {
			this.key = key;
		}

		public String getKey() {
			return key;
		}
	}
}
