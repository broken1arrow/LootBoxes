package org.brokenarrow.lootboxes.lootdata;

import org.broken.arrow.yaml.library.YamlFileManager;
import org.brokenarrow.lootboxes.Lootboxes;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.brokenarrow.lootboxes.untlity.RunTimedTask.runtaskLater;

public class ItemData extends YamlFileManager {

	public static final ItemData instance = new ItemData();
	private FileConfiguration customConfig;
	private final Map<String, Map<String, ItemStack>> cacheItemData = new HashMap<>();

	public ItemData() {
		super(Lootboxes.getInstance(),"itemdata", true,true);

	}

	public Map<String, Map<String, ItemStack>> getCacheData() {
		return cacheItemData;
	}

	public Map<String, ItemStack> getCachedItems(final String fileName) {
		return cacheItemData.get(fileName);
	}

	public ItemStack getCacheItemData(final String filname, final String itemdataPath) {
		final Map<String, ItemStack> data = cacheItemData.get(getItemDataPath(filname));
		if (data != null)
			return data.get(itemdataPath);
		return null;
	}

	public void removeCacheItemData(final String filname, final String itemdataPath) {
		final Map<String, ItemStack> data = cacheItemData.get(getItemDataPath(filname));
		if (data != null)
			data.remove(itemdataPath);
		saveTask(filname);
	}

	public ItemStack getCacheItemData(final String path) {
		return getCacheItemData(getFileName(), path);
	}

	public String updateCacheItemData(final String fileName, final String itemDataPath, final ItemStack itemstack) {
		Map<String, ItemStack> itemStackMap = this.cacheItemData.get(fileName);

		if (itemStackMap == null) {
			itemStackMap = new HashMap<>();
		}
		itemStackMap.put(itemDataPath, itemstack);
		this.cacheItemData.put(fileName, itemStackMap);

		saveTask(fileName);
		return itemDataPath;
	}

	public String setCacheItemData(final String lootTable, String itemKeyPath, final ItemStack itemstack) {
		final String itemDataPath = getItemDataPath(lootTable);
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
		if (!lootTableName.startsWith("item_meta_"))
			return "item_meta_" + lootTableName;
		return lootTableName;
	}

	@Override
	public void saveDataToFile(final File file) {

		customConfig = YamlConfiguration.loadConfiguration(file);
		customConfig.set("Items", null);
		final Map<String, ItemStack> cachedItems = this.getCachedItems(getItemDataPath(getNameOfFile(file.getName())));

		if (cachedItems != null)
			for (final Map.Entry<String, ItemStack> entry : cachedItems.entrySet()) {
				if (entry.getKey() == null || entry.getKey().isEmpty()) {
					removeItemData(getNameOfFile(file.getName()));
					continue;
				}
				customConfig.set("Items." + entry.getKey(), entry.getValue());
			}
		try {
			customConfig.save(file);
		} catch (final IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void loadSettingsFromYaml(final File file) {
		try {
			customConfig = getCustomConfig();
			customConfig.load(file);
			final Set<String> value = customConfig.getKeys(false);
			loadSettingsFromYaml(file, value);
		} catch (final IOException e) {
			e.printStackTrace();
		} catch (final InvalidConfigurationException e) {
			e.printStackTrace();
		}
	}

	public boolean removeItemData(final String fileName) {
		runtaskLater(5, () -> removeFile(this.getItemDataPath(fileName)), true);
		return false;
	}

	public void saveTask(final String lootTableName) {
		Lootboxes.getInstance().getSaveDataTask().addToSaveCache(this, this.getItemDataPath(lootTableName));
	}

	protected void loadSettingsFromYaml(final File key, final Set<String> values) {
		final List<ItemStack> items = new ArrayList<>();
		final Map<String, ItemStack> stack = new HashMap<>();
		for (final String value : values) {
			final ConfigurationSection configs = customConfig.getConfigurationSection(value);
			if (configs != null) {
				for (final String childrenKey : configs.getKeys(false)) {
					final ItemStack itemStack = customConfig.getItemStack(value + "." + childrenKey);
					stack.put(childrenKey, itemStack);
				}
			}
			cacheItemData.put(getItemDataPath(key.getName().replace(".yml", "")), stack);
		}
	}

	public static ItemData getInstance() {
		return instance;
	}

}
