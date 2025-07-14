package org.brokenarrow.lootboxes.lootdata;

import org.broken.arrow.library.yaml.YamlFileManager;
import org.broken.arrow.library.yaml.utillity.ConfigurationWrapper;
import org.brokenarrow.lootboxes.Lootboxes;
import org.brokenarrow.lootboxes.builder.LootData;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import static org.brokenarrow.lootboxes.untlity.RunTimedTask.runtaskLater;

public class ItemData extends YamlFileManager {

	public static final ItemData instance = new ItemData();
	private final Map<String, Map<String, ItemStack>> cacheItemData = new HashMap<>();

	public ItemData() {
		super(Lootboxes.getInstance(), "itemdata.yml", true, true);

	}

	public Map<String, Map<String, ItemStack>> getCacheData() {
		return cacheItemData;
	}

	public Map<String, ItemStack> getCachedItems(final LootData lootTable) {
		return cacheItemData.get(lootTable.getLootTableName());
	}

	public ItemStack getCacheItemData(final String lootTableName, final String itemDataPath) {
		final Map<String, ItemStack> itemStackMap = cacheItemData.get(getItemDataPath(lootTableName));
		if (itemStackMap != null)
			return itemStackMap.get(itemDataPath);
		return null;
	}

	public void removeCacheItemData(final String lootTableName, final String itemDataPath) {
		final Map<String, ItemStack> data = cacheItemData.get(getItemDataPath(lootTableName));
		if (data != null)
			data.remove(itemDataPath);
		saveTask(lootTableName);
	}


	public String updateCacheItemData(final String lootTableName, final String itemDataPath, final ItemStack itemstack) {
		Map<String, ItemStack> itemStackMap = this.cacheItemData.get(lootTableName);

		if (itemStackMap == null) {
			itemStackMap = new HashMap<>();
		}
		itemStackMap.put(itemDataPath, itemstack);
		this.cacheItemData.put(lootTableName, itemStackMap);

		saveTask(lootTableName);
		return itemDataPath;
	}

	public String setCacheItemData(final String lootTableName, String itemKeyPath, final ItemStack itemstack) {
		final String itemDataPath = getItemDataPath(lootTableName);
		Map<String, ItemStack> itemStackMap = cacheItemData.get(itemDataPath);

		if (itemKeyPath == null || itemKeyPath.isEmpty())
			return null;
		itemKeyPath = getFirstAvailableName(itemKeyPath);

		if (itemStackMap == null) {
			itemStackMap = new HashMap<>();
		}
		itemStackMap.put(itemKeyPath, itemstack);
		cacheItemData.put(itemDataPath, itemStackMap);

		saveTask(itemDataPath);
		return itemKeyPath;
	}

	public String setCacheItemData(final String lootTable, String itemKeyPath, final ItemStack itemstack, final boolean itemIsNull) {
		final String itemDataPath = getItemDataPath(lootTable);
		Map<String, ItemStack> itemStackMap = cacheItemData.get(itemDataPath);

		itemKeyPath = getFirstAvailableName(itemKeyPath);
		if (itemStackMap == null) {
			itemStackMap = new HashMap<>();
		}
		itemStackMap.put(itemKeyPath, itemstack);
		cacheItemData.put(itemDataPath, itemStackMap);
		saveTask(itemDataPath);
		return itemDataPath;
	}

	public boolean isCacheItemData(final String itemKey) {
		final Map<String, ItemStack> data = cacheItemData.get(getFileName());

		if (data != null)
			return data.get(itemKey) != null;
		return false;
	}

	public String getFirstAvailableName(final String itemKey) {
		int order = 0;
		while (isCacheItemData(itemKey + "_" + order))
			order += 1;
		return itemKey + "_" + order;
	}


	public String getItemDataPath(final String lootTableName) {
	/*	if (!lootTableName.startsWith("item_meta_"))
			return "item_meta_" + lootTableName;*/
		return lootTableName;
	}

	@Override
	protected void saveDataToFile(@NotNull final File file, @NotNull final ConfigurationWrapper configurationWrapper) {
		FileConfiguration customConfig = YamlConfiguration.loadConfiguration(file);
		customConfig.set("Items", null);
		final Map<String, Map<String, ItemStack>> cachedItems = this.getCacheData();
		for (Entry<String, Map<String, ItemStack>> cachedData : cachedItems.entrySet()) {
			if (cachedData.getValue() != null)
				for (final Map.Entry<String, ItemStack> entry : cachedData.getValue().entrySet()) {
					if (entry.getKey() == null || entry.getKey().isEmpty()) {
						//removeItemData(entry.getKey());
						continue;
					}
					customConfig.set("Items." + cachedData.getKey() + "." + entry.getKey(), entry.getValue());
				}
		}
		try {
			customConfig.save(file);
		} catch (final IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void loadSettingsFromYaml(final File file, FileConfiguration configuration) {
		loadSettings(file, configuration);
	}

	public boolean removeItemData(final String fileName) {
		runtaskLater(5, () -> removeFile(this.getItemDataPath(fileName)), true);
		return false;
	}

	public void saveTask(final String lootTableName) {
		Lootboxes.getInstance().getSaveDataTask().addToSaveCache(this, this.getItemDataPath(lootTableName));
	}

	protected void loadSettings(final File key, final FileConfiguration configuration) {
		final Map<String, ItemStack> stack = new HashMap<>();
		final ConfigurationSection configs = configuration.getConfigurationSection("Items");
		if (configs != null) {
			for (final String table : configs.getKeys(false)) {
				final ConfigurationSection tables = configuration.getConfigurationSection("Items." + table);
				if (tables == null) continue;

				for (final String itemPath : tables.getKeys(false)) {
					final ItemStack itemStack = configuration.getItemStack("Items." + table + "." + itemPath);
					if (itemStack == null) continue;
					stack.put(itemPath, itemStack);
				}
				cacheItemData.put(table, stack);
			}
		}
	}

	public static ItemData getInstance() {
		return instance;
	}

}
