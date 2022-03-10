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

public class ItemData {

	@Getter
	public static final ItemData instance = new ItemData();
	private final AllYamlFilesInFolder yamlFiles;
	private File customConfigFile;
	private FileConfiguration customConfig;
	private final Map<String, ItemStack[]> cacheItemData = new HashMap<>();

	public ItemData() {
		this.yamlFiles = new AllYamlFilesInFolder("itemdata", true);
	}

	public Map<String, ItemStack[]> getCacheItemData() {
		return cacheItemData;
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
		final File dataFolder = new File(Lootboxes.getInstance().getDataFolder(), "itemdata");
		final File[] dataFolders = dataFolder.listFiles();
		if (dataFolder.exists() && dataFolders != null) {
			for (File file : dataFolders) {
				String fileName = this.yamlFiles.getFileName(file.getName());

				if (fileName.equals(fileToSave)) {
					customConfig = YamlConfiguration.loadConfiguration(file);
					for (Map.Entry<String, ItemStack[]> entry : cacheItemData.entrySet())
						customConfig.set(entry.getKey(), entry.getValue());
					try {
						customConfig.save(file);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	private void getFilesData() {
		try {
			for (File key : yamlFiles.getYamlFiles("itemdata", "yml")) {

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
		List<ItemStack> items = new ArrayList<>();
		for (String value : values) {
			ConfigurationSection configs = customConfig.getConfigurationSection(value);
			System.out.println("itemStack " + value);
			if (configs != null) {
				for (String childrenKey : configs.getKeys(false)) {
				}
			}
			if (value.equals("item")) {
				ItemStack itemStack = customConfig.getItemStack(value);
				items.add(itemStack);
			}
		}
		System.out.println("itemStack " + items);
		if (!items.isEmpty())
			cacheItemData.put(key.getName(), items.toArray(new ItemStack[0]));
		save("test");
	}
}
