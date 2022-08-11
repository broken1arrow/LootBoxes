package org.brokenarrow.lootboxes.lootdata;

import com.google.common.base.Enums;
import lombok.Getter;
import org.brokenarrow.lootboxes.Lootboxes;
import org.brokenarrow.lootboxes.builder.EntityKeyData;
import org.brokenarrow.lootboxes.builder.KeyMobDropData;
import org.brokenarrow.lootboxes.untlity.filemanger.SimpleYamlHelper;
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

public class KeyDropData extends SimpleYamlHelper {

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

	public Map<String, KeyMobDropData> getKeyMobDropValues(final String fileName) {
		return cachedKeyData.get(fileName);
	}

	public Set<EntityKeyData> getEntityCache(final EntityType entityType) {
		final Set<EntityKeyData> entityKeyDataSet = new HashSet<>();
		for (final Map.Entry<String, EntityKeyData> entry : this.entityCache.entrySet()) {
			final String[] key = entry.getKey().split("#");
			if (key.length != 2) continue;
			if (!key[1].equals(entityType.name())) continue;

			final EntityKeyData entityKeyData = entityCache.get(key[0] + "#" + key[1]);
			if (entityKeyData != null)
				entityKeyDataSet.add(entityKeyData);
		}
		return entityKeyDataSet;
	}

	public boolean createKeyData(final String containerDataFileName, final String keyName) {
		final Map<String, KeyMobDropData> dropDataMap = cachedKeyData.get(containerDataFileName);
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

	public void removeKeyMobDropData(final String containerDataFileName, final String keyName) {
		final Map<String, KeyMobDropData> dropDataMap = cachedKeyData.get(containerDataFileName);
		if (dropDataMap != null) {
			dropDataMap.remove(keyName);
			saveTask(containerDataFileName);
		}
	}

	public KeyMobDropData getKeyMobDropData(final String fileName, final String keyName) {
		final Map<String, KeyMobDropData> dropDataMap = cachedKeyData.get(fileName);
		if (dropDataMap != null)
			return dropDataMap.get(keyName);
		return null;
	}

	public KeyMobDropData getKeyMobDropData(final String keyName) {
		for (final Map<String, KeyMobDropData> dropDataMap : this.cachedKeyData.values()) {
			final KeyMobDropData keysData = dropDataMap.get(keyName);
			if (keysData != null)
				return keysData;
		}
		return null;
	}

	public void putCachedKeyData(final String fileName, final String keyName, final KeyMobDropData keyMobDropData) {
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
			for (final File file : getAllFilesInPluginJar()) {
				customConfigFile = file;

				customConfig = YamlConfiguration.loadConfiguration(customConfigFile);
				getFilesData();
			}
		} else {
			for (final File file : getAllFilesInPluginJar()) {
				customConfigFile = file;
				getFilesData();
			}
		}
	}

	public boolean removeKey(final String fileName) {
		runtaskLater(5, () -> removeFile(fileName), true);
		return false;
	}

	public void saveTask(final String containerDataFileName) {
		Lootboxes.getInstance().getSaveDataTask().addToSaveCache(this, containerDataFileName);
		//runtaskLater(5, () -> save(containerDataFileName), true);
	}


	@Override
	public void saveDataToFile(final File file) {
		final String fileName = getNameOfFile(file.getName());
		customConfig = YamlConfiguration.loadConfiguration(file);
		final Map<String, KeyMobDropData> settings = this.cachedKeyData.get(fileName);
		if (settings != null) {
			customConfig.set("Keys_Data", null);
			for (final String childrenKey : settings.keySet()) {
				if (childrenKey == null) continue;
				final KeyMobDropData data = settings.get(childrenKey);
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
						for (final EntityType entityType : data.getEntityTypes()) {
							if (entityType != null) {
								customConfig.set("Keys_Data." + childrenKey + ".Entity_list", data.getEntityTypes().stream().map(Enum::name).collect(Collectors.toList()));
							}
						}
				}
			}
			try {
				customConfig.save(file);
			} catch (final IOException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	protected void loadSettingsFromYaml(final File file) {

	}


	private void getFilesData() {
		try {
			for (final File key : getFilesInPluginFolder("keysDropData")) {

				customConfig.load(key);
				final Set<String> value = customConfig.getKeys(false);
				loadSettingsFromYaml(key, value);
			}
		} catch (final IOException e) {
			e.printStackTrace();
		} catch (final InvalidConfigurationException e) {
			e.printStackTrace();
		}
	}

	protected void loadSettingsFromYaml(final File key, final Set<String> values) {
		final Map<String, KeyMobDropData> data = new HashMap<>();
		final String fileName = getNameOfFile(key.getName());
		for (final String value : values) {
			final ConfigurationSection configs = customConfig.getConfigurationSection(value);
			if (configs != null)
				for (final String childrenKey : configs.getKeys(false)) {
					final int chance = customConfig.getInt(value + "." + childrenKey + ".Chance");
					final int minimum = customConfig.getInt(value + "." + childrenKey + ".Minimum");
					final int maximum = customConfig.getInt(value + "." + childrenKey + ".Maximum");
					final List<EntityType> entityList = convertStringToEntityType(customConfig.getStringList(value + "." + childrenKey + ".Entity_list"));
					if (entityList != null && !entityList.isEmpty()) {
						for (final EntityType entityType : entityList)
							this.entityCache.put(getNameOfFile(key.getName()) + "_" + childrenKey + "#" + entityType, new EntityKeyData(childrenKey, fileName));
					}
					final KeyMobDropData.Builder builder = new KeyMobDropData.Builder();
					builder.setChance(chance)
							.setMinimum(minimum)
							.setMaximum(maximum)
							.setEntityTypes(entityList)
							.setKeyName(childrenKey)
							.setContainerDataFileName(fileName);
					data.put(childrenKey, builder.build());
				}
		}
		cachedKeyData.put(fileName, data);
	}

	private List<EntityType> convertStringToEntityType(final List<String> entityList) {
		if (entityList == null || entityList.isEmpty()) return new ArrayList<>();

		final List<EntityType> list = new ArrayList<>();
		for (final String entity : entityList) {
			if (entity == null) continue;
			final EntityType entityType = Enums.getIfPresent(EntityType.class, entity).orNull();
			if (entityType == null) continue;

			if (entityType.isAlive() && entityType.isSpawnable())
				list.add(entityType);
		}
		return list;
	}
}
