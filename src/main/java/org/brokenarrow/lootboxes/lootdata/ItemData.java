package org.brokenarrow.lootboxes.lootdata;

import lombok.Getter;
import org.brokenarrow.lootboxes.Lootboxes;
import org.brokenarrow.lootboxes.settings.AllYamlFilesInFolder;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class ItemData extends AllYamlFilesInFolder {

	@Getter
	public static final ItemData instance = new ItemData();

	private String fileName;
	private FileConfiguration customConfig = getCustomConfig();
	private final Map<String, Map<String, ItemStack>> cacheItemData = new HashMap<>();

	public ItemData() {
		super("itemdata", true);

	}

	public Map<String, Map<String, ItemStack>> getCacheData() {
		return cacheItemData;
	}

	public ItemStack getCacheItemData(String filname, String itemdataPath) {
		Map<String, ItemStack> data = cacheItemData.get(filname);
		if (data != null)
			return data.get(itemdataPath);
		return null;
	}

	public ItemStack removeCacheItemData(String filname, String itemdataPath) {
		Map<String, ItemStack> data = cacheItemData.get(filname);
		if (data != null)
			return data.remove(itemdataPath);
		return null;
	}

	public ItemStack getCacheItemData(String path) {
		return getCacheItemData(getFileName(), path);
	}

	public String updateCacheItemData(String itemdataPath, ItemStack itemstack) {
		Map<String, ItemStack> itemStackMap = this.cacheItemData.get(getFileName());

		if (itemStackMap == null) {
			itemStackMap = new HashMap<>();
		}
		itemStackMap.put(itemdataPath, itemstack);
		this.cacheItemData.put(getFileName(), itemStackMap);

		saveTask(null);
		return itemdataPath;
	}

	public String setCacheItemData(String itemdataPath, ItemStack itemstack) {
		Map<String, ItemStack> itemStackMap = cacheItemData.get(getFileName());

		if (itemdataPath == null || itemdataPath.isEmpty())
			return null;
		itemdataPath = getFirstAvailableName(itemdataPath);

		if (itemStackMap == null) {
			itemStackMap = new HashMap<>();
		}
		itemStackMap.put(itemdataPath, itemstack);
		cacheItemData.put(getFileName(), itemStackMap);

		saveTask(null);
		return itemdataPath;
	}

	public String setCacheItemData(String itemdataPath, ItemStack itemstack, boolean itemIsNull) {
		Map<String, ItemStack> itemStackMap = cacheItemData.get(getFileName());

		itemdataPath = getFirstAvailableName(itemdataPath);
		if (itemStackMap == null) {
			itemStackMap = new HashMap<>();
		}
		itemStackMap.put(itemdataPath, itemstack);
		cacheItemData.put(getFileName(), itemStackMap);
		return itemdataPath;
	}

	public boolean isCacheItemData(String itemKey) {
		Map<String, ItemStack> data = cacheItemData.get(getFileName());

		if (data != null)
			return data.get(itemKey) != null;
		return false;
	}

	public String getFirstAvailableName(String itemKey) {
		int order = 0;
		while (isCacheItemData(itemKey + "_" + order))
			order += 1;
		return itemKey + "_" + order;
	}

	@Override
	public String getFileName() {
		return fileName;
	}

	@Override
	public void saveDataToFile(File file) {

		customConfig = YamlConfiguration.loadConfiguration(file);
		customConfig.set("Items", null);
		for (Map.Entry<String, Map<String, ItemStack>> entry : this.getCacheData().entrySet()) {

			for (Map.Entry<String, ItemStack> ent : entry.getValue().entrySet()) {
				customConfig.set("Items." + ent.getKey(), ent.getValue());
			}
		}
		try {
			customConfig.save(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void loadSettingsFromYaml(File file) {
		try {
			customConfig.load(file);
			Set<String> value = customConfig.getKeys(false);
			loadSettingsFromYaml(file, value);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InvalidConfigurationException e) {
			e.printStackTrace();
		}
	}

	public void saveTask(String fileToSave) {
		Lootboxes.getInstance().getSaveDataTask().addToSaveCache(this, fileToSave);
		//runtaskLater(5, () -> save(containerDataFileName), true);
	}

	/*
		@Override
		public void save() {
			save(null);
		}

		@Override
		public void save(String fileToSave) {
			final File dataFolder = new File(Lootboxes.getInstance().getDataFolder(), "itemdata");
			final File[] dataFolders = dataFolder.listFiles();
			if (dataFolder.exists() && dataFolders != null) {
				for (File file : dataFolders) {
					String fileName = getNameOfFile(file.getName());

					if (fileToSave == null || fileName.equals(fileToSave)) {
						customConfig = YamlConfiguration.loadConfiguration(file);
						customConfig.set("Items", null);
						for (Map.Entry<String, Map<String, ItemStack>> entry : cacheItemData.entrySet()) {

							for (Map.Entry<String, ItemStack> ent : entry.getValue().entrySet()) {
								customConfig.set("Items." + ent.getKey(), ent.getValue());
							}
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
	*/
	protected void loadSettingsFromYaml(File key, Set<String> values) {
		List<ItemStack> items = new ArrayList<>();
		Map<String, ItemStack> stack = new HashMap<>();
		for (String value : values) {
			ConfigurationSection configs = customConfig.getConfigurationSection(value);
			if (configs != null) {
				for (String childrenKey : configs.getKeys(false)) {
					ItemStack itemStack = customConfig.getItemStack(value + "." + childrenKey);
					stack.put(childrenKey, itemStack);
				}
			}
			cacheItemData.put(key.getName().replace(".yml", ""), stack);
			//items.add(itemStack);

		}
		fileName = String.valueOf(key.getName().replace(".yml", ""));
	/*9	if (!items.isEmpty())
			cacheItemData.put(key.getName().replace(".yml", ""), items.toArray(new ItemStack[0]));*/

		//save("test");
	}
}
