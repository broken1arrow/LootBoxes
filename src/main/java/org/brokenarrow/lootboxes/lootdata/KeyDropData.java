package org.brokenarrow.lootboxes.lootdata;

import com.google.common.base.Enums;
import org.broken.arrow.library.yaml.YamlFileManager;
import org.broken.arrow.library.yaml.utillity.ConfigurationWrapper;
import org.brokenarrow.lootboxes.Lootboxes;
import org.brokenarrow.lootboxes.builder.EntityKeyData;
import org.brokenarrow.lootboxes.builder.KeyMobDropData;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Level;
import java.util.stream.Collectors;

import static org.brokenarrow.lootboxes.untlity.RunTimedTask.runtaskLater;

public class KeyDropData extends YamlFileManager {

	private static final KeyDropData instance = new KeyDropData();
	private final Map<String, Map<String, KeyMobDropData>> cachedKeyData = new HashMap<>();
	private final Map<String, EntityKeyData> entityCache = new HashMap<>();

	public KeyDropData() {
		super(Lootboxes.getInstance(), "keys/keysDropData.yml");

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
			//final EntityKeyData entityKeyData = entityCache.get(key[0] + "#" + key[1]);
			if (entry.getValue() != null)
				entityKeyDataSet.add(entry.getValue());
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

	@Nonnull
	public KeyMobDropData getKeyMobDropData(final String fileName, final String keyName) {
		final Map<String, KeyMobDropData> dropDataMap = cachedKeyData.get(fileName);
		if (dropDataMap != null) {
			KeyMobDropData keyMobDropData = dropDataMap.get(keyName);
			if (keyMobDropData != null)
				return keyMobDropData;
			else {
				return new KeyMobDropData.Builder().setContainerDataFileName(fileName).build();
			}
		}
		return new KeyMobDropData.Builder().setContainerDataFileName(fileName).build();
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

	public boolean removeKey(final String fileName) {
		runtaskLater(5, () -> removeFile(fileName), true);
		return false;
	}

	public void saveTask(final String containerDataFileName) {
		Lootboxes.getInstance().getSaveDataTask().addToSaveCache(this, "keysDropData");
		//runtaskLater(5, () -> save(containerDataFileName), true);
	}

	@Override
	protected void saveDataToFile(@NotNull final File file, @NotNull final ConfigurationWrapper configurationWrapper) {
		FileConfiguration configuration = YamlConfiguration.loadConfiguration(file);
		configuration.set("Keys_Data", null);
		for (final Entry<String, Map<String, KeyMobDropData>> settings : this.cachedKeyData.entrySet()) {
			for (final Entry<String, KeyMobDropData> mobDropDataEntry : settings.getValue().entrySet()) {
				if (mobDropDataEntry == null) continue;
				String containerDataKey = settings.getKey();
				if (containerDataKey.isEmpty()) continue;

				final String childrenKey = mobDropDataEntry.getKey();
				final KeyMobDropData data = mobDropDataEntry.getValue();
				if (data == null) {
					configuration.set("Keys_Data." + containerDataKey + "." + childrenKey + ".Chance", 2);
					configuration.set("Keys_Data." + containerDataKey + "." + childrenKey + ".Minimum", 1);
					configuration.set("Keys_Data." + containerDataKey + "." + childrenKey + ".Maximum", 1);
					configuration.set("Keys_Data." + containerDataKey + "." + childrenKey + ".Entity_list", new ArrayList<>());
				} else {
					configuration.set("Keys_Data." + containerDataKey + "." + childrenKey + ".Chance", data.getChance());
					configuration.set("Keys_Data." + containerDataKey + "." + childrenKey + ".Minimum", data.getMinimum());
					configuration.set("Keys_Data." + containerDataKey + "." + childrenKey + ".Maximum", data.getMaximum());
					if (data.getEntityTypes() == null || data.getEntityTypes().isEmpty())
						configuration.set("Keys_Data." + containerDataKey + "." + childrenKey + ".Entity_list", new ArrayList<>());
					else
						for (final EntityType entityType : data.getEntityTypes()) {
							if (entityType != null) {
								configuration.set("Keys_Data." + containerDataKey + "." + childrenKey + ".Entity_list", data.getEntityTypes().stream().map(Enum::name).collect(Collectors.toList()));
							}
						}
				}
			}
		}
		try {
			configuration.save(file);
		} catch (final IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void loadSettingsFromYaml(final File file, FileConfiguration configuration) {

		final String fileName = getNameOfFile(file.getName());
		final ConfigurationSection configs = configuration.getConfigurationSection("Keys_Data");
		if (configs != null)
			for (final String containerDataKey : configs.getKeys(false)) {
				final ConfigurationSection keys = configuration.getConfigurationSection("Keys_Data." + containerDataKey);
				if (keys == null) continue;

				final Map<String, KeyMobDropData> data = new HashMap<>();
				for (final String childrenKey : keys.getKeys(false)) {
					final int chance = configuration.getInt("Keys_Data." + containerDataKey + "." + childrenKey + ".Chance");
					final int minimum = configuration.getInt("Keys_Data." + containerDataKey + "." + "." + childrenKey + ".Minimum");
					final int maximum = configuration.getInt("Keys_Data." + containerDataKey + "." + "." + childrenKey + ".Maximum");
					final List<EntityType> entityList = convertStringToEntityType(configuration.getStringList("Keys_Data." + containerDataKey + "." + "." + childrenKey + ".Entity_list"));
					if (entityList != null && !entityList.isEmpty()) {
						for (final EntityType entityType : entityList)
							this.entityCache.put(containerDataKey + "_" + childrenKey + "#" + entityType, new EntityKeyData(childrenKey, containerDataKey));
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
				cachedKeyData.put(containerDataKey, data);
			}
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

	public static KeyDropData getInstance() {
		return instance;
	}
}
