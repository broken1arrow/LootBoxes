package org.brokenarrow.lootboxes.lootdata;

import com.google.common.base.Enums;
import lombok.Getter;
import org.brokenarrow.lootboxes.Lootboxes;
import org.brokenarrow.lootboxes.builder.EntityKeyData;
import org.brokenarrow.lootboxes.builder.KeyMobDropData;
import org.brokenarrow.lootboxes.settings.AllYamlFilesInFolder;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.logging.Level;
import java.util.stream.Collectors;

import static org.brokenarrow.lootboxes.untlity.RunTimedTask.runtaskLater;

public class KeyDropData extends AllYamlFilesInFolder {

	@Getter
	private static final KeyDropData instance = new KeyDropData();
	private final Map<String, Map<String, KeyMobDropData>> cachedKeyData = new HashMap<>();
	private final Map<String, EntityKeyData> entityCache = new HashMap<>();
	private File customConfigFile;
	private FileConfiguration customConfig;

	public KeyDropData() {
		super("keysDropData", true);

	}

	public Map<String, EntityKeyData> getEntityCache() {
		return entityCache;
	}

	public Map<String, Map<String, KeyMobDropData>> getCachedKeyData() {
		return cachedKeyData;
	}

	public Map<String, KeyMobDropData> getKeyMobDropValues(String fileName) {
		return cachedKeyData.get(fileName);
	}

	public Set<EntityKeyData> getEntityCache(EntityType entityType) {
		Set<EntityKeyData> entityKeyDataSet = new HashSet<>();
		for (Map.Entry<String, EntityKeyData> entry : this.entityCache.entrySet()) {
			String[] key = entry.getKey().split("#");
			if (key.length != 2) continue;
			if (!key[1].equals(entityType.name())) continue;

			EntityKeyData entityKeyData = entityCache.get(key[0] + "#" + key[1]);
			if (entityKeyData != null)
				entityKeyDataSet.add(entityKeyData);
		}
		System.out.println("this.entityCache " + this.entityCache);
		return entityKeyDataSet;
	}

	public boolean createKeyData(String containerDataFileName, String keyName) {
		Map<String, KeyMobDropData> dropDataMap = cachedKeyData.get(containerDataFileName);
		if (dropDataMap != null) {
			if (dropDataMap.containsKey(keyName)) {
				Lootboxes.getInstance().getLogger().log(Level.WARNING, "This key is dublicate " + keyName + ". chose diffrent name");
				return false;
			} else
				dropDataMap.put(keyName, new KeyMobDropData.Builder().build());
			cachedKeyData.put(containerDataFileName, dropDataMap);
		} else {
			cachedKeyData.put(containerDataFileName, new HashMap<>());
		}
		saveTask(containerDataFileName);
		return true;
	}

	public void removeKeyMobDropData(String containerDataFileName, String keyName) {
		Map<String, KeyMobDropData> dropDataMap = cachedKeyData.get(containerDataFileName);
		if (dropDataMap != null) {
			dropDataMap.remove(keyName);
			saveTask(containerDataFileName);
		}
	}

	public KeyMobDropData getKeyMobDropData(String fileName, String keyName) {
		Map<String, KeyMobDropData> dropDataMap = cachedKeyData.get(fileName);
		if (dropDataMap != null)
			return dropDataMap.get(keyName);
		return null;
	}

	public KeyMobDropData getKeyMobDropData(String keyName) {
		for (Map<String, KeyMobDropData> dropDataMap : this.cachedKeyData.values()) {
			KeyMobDropData keysData = dropDataMap.get(keyName);
			if (keysData != null)
				return keysData;
		}
		return null;
	}

	public void putCachedKeyData(String fileName, String keyName, KeyMobDropData keyMobDropData) {
		Map<String, KeyMobDropData> dropDataMap = cachedKeyData.get(fileName);
		if (dropDataMap == null) {
			dropDataMap = new HashMap<>();
		}
		dropDataMap.put(keyName, keyMobDropData);
		this.cachedKeyData.put(fileName, dropDataMap);
		saveTask(fileName);
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

	public void save() {
		save(null);
	}

	public void removeFile(String containerDataFileName) {
		runtaskLater(5, () -> {
					final File dataFolder = new File(Lootboxes.getInstance().getDataFolder() + "/keysDropData", containerDataFileName + ".yml");
					dataFolder.delete();
				}
				, true);
	}

	public void saveTask(String containerDataFileName) {
		runtaskLater(5, () -> save(containerDataFileName), true);
	}

	public void save(String fileToSave) {
		final File dataFolder = new File(Lootboxes.getInstance().getDataFolder(), "keysDropData");
		final File[] dataFolders = dataFolder.listFiles();
		if (dataFolder.exists() && dataFolders != null) {
			if (!checkFolderExist(fileToSave, dataFolders)) {
				final File newDataFolder = new File(Lootboxes.getInstance().getDataFolder() + "/keysDropData", fileToSave + ".yml");
				try {
					newDataFolder.createNewFile();
				} catch (IOException e) {
					e.printStackTrace();
				} finally {
					saveDataToFile(newDataFolder);
				}
			} else {
				for (File file : dataFolders) {
					if (getFileName(file.getName()).equals(fileToSave)) {
						saveDataToFile(file);
						return;
					}
				}
			}
			if (fileToSave == null)
				for (File file : dataFolders) {
					saveDataToFile(file);
				}
		}
	}

	public void saveDataToFile(File file) {
		String fileName = getFileName(file.getName());
		customConfig = YamlConfiguration.loadConfiguration(file);
		Map<String, KeyMobDropData> settings = this.cachedKeyData.get(fileName);
		if (settings != null) {
			customConfig.set("Keys_Data", null);
			for (String childrenKey : settings.keySet()) {
				if (childrenKey == null) continue;
				KeyMobDropData data = settings.get(childrenKey);
				if (data == null) {
					customConfig.set("Keys_Data." + childrenKey + ".Chance", 2);
					customConfig.set("Keys_Data." + childrenKey + ".Minimum", 1);
					customConfig.set("Keys_Data." + childrenKey + ".Maximum", 1);
					customConfig.set("Keys_Data." + childrenKey + ".Entity_list", new ArrayList<>());
				} else {
					customConfig.set("Keys_Data." + childrenKey + ".Chance", data.getChance());
					customConfig.set("Keys_Data." + childrenKey + ".Minimum", data.getMinimum());
					customConfig.set("Keys_Data." + childrenKey + ".Maximum", data.getMaximum());
					if (data.getEntityTypes() == null || data.getEntityTypes().isEmpty())
						customConfig.set("Keys_Data." + childrenKey + ".Entity_list", new ArrayList<>());
					else
						for (EntityType entityType : data.getEntityTypes()) {
							if (entityType != null) {
								customConfig.set("Keys_Data." + childrenKey + ".Entity_list", data.getEntityTypes().stream().map(Enum::name).collect(Collectors.toList()));
							}
						}
				}
			}
			try {
				customConfig.save(file);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}


	private void getFilesData() {
		try {
			for (File key : getYamlFiles("keysDropData", "yml")) {

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
		Map<String, KeyMobDropData> data = new HashMap<>();
		for (String value : values) {
			ConfigurationSection configs = customConfig.getConfigurationSection(value);
			if (configs != null)
				for (String childrenKey : configs.getKeys(false)) {
					int chance = customConfig.getInt(value + "." + childrenKey + ".Chance");
					int minimum = customConfig.getInt(value + "." + childrenKey + ".Minimum");
					int maximum = customConfig.getInt(value + "." + childrenKey + ".Maximum");
					List<EntityType> entityList = convertStringToEntityType(customConfig.getStringList(value + "." + childrenKey + ".Entity_list"));
					if (entityList != null && !entityList.isEmpty()) {
						for (EntityType entityType : entityList)
							this.entityCache.put(getFileName(key.getName()) + "_" + childrenKey + "#" + entityType, new EntityKeyData(childrenKey, getFileName(key.getName())));
					}
					KeyMobDropData.Builder builder = new KeyMobDropData.Builder();
					builder.setChance(chance)
							.setMinimum(minimum)
							.setMaximum(maximum)
							.setEntityTypes(entityList)
							.setKeyName(childrenKey)
							.setContainerDataFileName(getFileName(String.valueOf(key)));
					data.put(childrenKey, builder.build());
				}
		}
		cachedKeyData.put(getFileName(key.getName()), data);
	}

	private List<EntityType> convertStringToEntityType(List<String> entityList) {
		if (entityList == null || entityList.isEmpty()) return new ArrayList<>();

		List<EntityType> list = new ArrayList<>();
		for (String entity : entityList) {
			if (entity == null) continue;
			EntityType entityType = Enums.getIfPresent(EntityType.class, entity).orNull();
			if (entityType == null) continue;

			if (entityType.isAlive() && entityType.isSpawnable())
				list.add(entityType);
		}
		return list;
	}
}
