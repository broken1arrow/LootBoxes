package org.brokenarrow.lootboxes.lootdata;

import com.google.common.base.Enums;
import org.broken.arrow.library.yaml.YamlFileManager;
import org.broken.arrow.library.yaml.utillity.ConfigurationWrapper;
import org.brokenarrow.lootboxes.Lootboxes;
import org.brokenarrow.lootboxes.builder.KeyMobDropData;
import org.brokenarrow.lootboxes.builder.KeysData;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import static org.brokenarrow.lootboxes.untlity.RunTimedTask.runtaskLater;

public class KeyDropData extends YamlFileManager {

    private static final KeyDropData instance = new KeyDropData();
    private final Map<EntityType, Map<String, KeyMobDropData>> cachedKeyData = new HashMap<>();

    public KeyDropData() {
        //super(Lootboxes.getInstance(), "keys/keysDropData.yml");
        super(Lootboxes.getInstance(), "keys", false, true);
    }

    public Map<EntityType, Map<String, KeyMobDropData>> getCachedKeyData() {
        return cachedKeyData;
    }

    public Map<String, KeyMobDropData> getKeyMobDropValues(final EntityType entityType) {
        return cachedKeyData.get(entityType);
    }

    @Nullable
    public Map<String, KeyMobDropData> getEntityCache(final EntityType entityType) {
        return this.cachedKeyData.get(entityType);
    }

    public boolean createMobLootData(@Nonnull final EntityType entityType, @Nonnull final String lootContainerKey, @Nonnull final String keyUniqueName) {
        Map<String, KeyMobDropData> dropDataMap = cachedKeyData.get(entityType);
        if (dropDataMap != null) {
            if (dropDataMap.containsKey(keyUniqueName)) {
                // Lootboxes.getInstance().getLogger().log(Level.WARNING, "This key is duplicate " + keyUniqueName + ". chose different name");
                return false;
            } else
                dropDataMap.put(keyUniqueName, new KeyMobDropData.Builder().setLootContainerKey(lootContainerKey).setKeyName(keyUniqueName).build());
        } else {
            dropDataMap = new HashMap<>();
            dropDataMap.put(keyUniqueName, new KeyMobDropData.Builder().setLootContainerKey(lootContainerKey).setKeyName(keyUniqueName).build());
        }
        cachedKeyData.put(entityType, dropDataMap);
        saveTask(entityType);
        return true;
    }

    public void removeKeyFromMob(final EntityType entityType, final String keyUniqueName) {
        final Map<String, KeyMobDropData> dropDataMap = cachedKeyData.get(entityType);
        if (dropDataMap != null) {
            dropDataMap.remove(keyUniqueName);
            saveTask(entityType);
        }
    }

    @Nonnull
    public KeyMobDropData getOrCreateMobDropForKey(@Nonnull final EntityType entityType, @Nonnull final String lootContainerKey, @Nonnull final String keyName) {
        final Map<String, KeyMobDropData> dropDataMap = cachedKeyData.get(entityType);
        if (dropDataMap != null) {
            KeyMobDropData keyMobDropData = dropDataMap.get(keyName);
            if (keyMobDropData != null)
                return keyMobDropData;
            else {
                return new KeyMobDropData.Builder().setLootContainerKey(lootContainerKey).setKeyName(keyName).build();
            }
        }
        return new KeyMobDropData.Builder().setLootContainerKey(lootContainerKey).setKeyName(keyName).build();
    }

    @Nullable
    public KeyMobDropData getMobDropFromKeyName(final String keyName) {
        for (final Map<String, KeyMobDropData> dropDataMap : this.cachedKeyData.values()) {
            final KeyMobDropData keysData = dropDataMap.get(keyName);
            if (keysData != null)
                return keysData;
        }
        return null;
    }

    public void putCachedKeyData(final EntityType entityType, final String keyName, final KeyMobDropData keyMobDropData) {
        Map<String, KeyMobDropData> dropDataMap = cachedKeyData.get(entityType);
        if (dropDataMap == null) {
            dropDataMap = new HashMap<>();
        }
        dropDataMap.put(keyName, keyMobDropData);
        this.cachedKeyData.put(entityType, dropDataMap);
        saveTask(entityType);
    }

    public boolean removeKey(final EntityType entityType) {
        runtaskLater(5, () -> removeFile(entityType.name()), true);
        return false;
    }

    public void saveTask(final EntityType entityType) {
        Lootboxes.getInstance().getSaveDataTask().addToSaveCache(this, entityType.name().toLowerCase());
        //runtaskLater(5, () -> save(containerDataFileName), true);
    }

    @Override
    protected void saveDataToFile(@NotNull final File file, @NotNull final ConfigurationWrapper configurationWrapper) {
        final String fileName = getNameOfFile(file.getName());
        final EntityType entityType = Enums.getIfPresent(EntityType.class, fileName.toUpperCase()).orNull();
        final Map<String, KeyMobDropData> dropDataMap = this.cachedKeyData.get(entityType);
        if (dropDataMap == null) return;

        FileConfiguration configuration = new YamlConfiguration();
        for (final Entry<String, KeyMobDropData> mobDropDataEntry : dropDataMap.entrySet()) {
            if (mobDropDataEntry == null) continue;
            final String childrenKey = mobDropDataEntry.getKey();
            final KeyMobDropData data = mobDropDataEntry.getValue();
            if (data == null) {
                continue;
            }
            final String containerKey = data.getLootContainerKey();
            configuration.set("Keys_Data." + childrenKey + ".Chance", data.getChance());
            configuration.set("Keys_Data." + childrenKey + ".Minimum", data.getMinimum());
            configuration.set("Keys_Data." + childrenKey + ".Maximum", data.getMaximum());
            configuration.set("Keys_Data." + childrenKey + ".Loot_container_key", data.getLootContainerKey());
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

        if (configs != null) {
            for (final String containerDataKey : configs.getKeys(false)) {
                final ConfigurationSection keys = configuration.getConfigurationSection("Keys_Data." + containerDataKey);
                if (keys == null) continue;

                final Map<String, KeyMobDropData> data = new HashMap<>();
                List<EntityType> entityTypeFromList = getEntityType(configuration, containerDataKey, fileName, keys, data);
                if (entityTypeFromList == null) {
                    final int chance = configuration.getInt("Keys_Data." + containerDataKey + ".Chance");
                    final int minimum = configuration.getInt("Keys_Data." + containerDataKey + ".Minimum");
                    final int maximum = configuration.getInt("Keys_Data." + containerDataKey + ".Maximum");
                    final String containerKey = configuration.getString("Keys_Data." + containerDataKey + ".Loot_container_key");

                    final KeyMobDropData.Builder builder = new KeyMobDropData.Builder();
                    builder.setChance(chance)
                            .setMinimum(minimum)
                            .setMaximum(maximum)
                            .setKeyName(containerDataKey)
                            .setLootContainerKey(containerKey);
                    data.put(containerDataKey, builder.build());
                }


                EntityType entityType = Enums.getIfPresent(EntityType.class, fileName.toUpperCase()).orNull();
                if (entityType == null) {
                    if (entityTypeFromList != null) {
                        entityTypeFromList.forEach(entity -> cachedKeyData.put(entity, data));
                    }
                    continue;
                }
                cachedKeyData.put(entityType, data);
            }
        }
    }

    private @Nullable List<EntityType> getEntityType(FileConfiguration configuration, String containerDataKey, String fileName, ConfigurationSection keys, Map<String, KeyMobDropData> data) {
        List<EntityType> entityTypeFromList = null;
        if (!fileName.equals("keysDropData"))
            return entityTypeFromList;

        for (final String childrenKey : keys.getKeys(false)) {
            final int chance = configuration.getInt("Keys_Data." + containerDataKey + "." + childrenKey + ".Chance");
            final int minimum = configuration.getInt("Keys_Data." + containerDataKey + "." + "." + childrenKey + ".Minimum");
            final int maximum = configuration.getInt("Keys_Data." + containerDataKey + "." + "." + childrenKey + ".Maximum");
            final List<EntityType> entityList = convertStringToEntityType(configuration.getStringList("Keys_Data." + containerDataKey + "." + "." + childrenKey + ".Entity_list"));

            if (entityList != null && !entityList.isEmpty()) {
                entityTypeFromList = entityList;
                Lootboxes.getInstance().getContainerDataCache().write(containerDataKey, containerData -> {
                    KeysData keyData = containerData.getKeysData(childrenKey);
                    if (keyData != null) {
                        keyData.setEntityTypes(entityList);
                    }
                });
            }

            final KeyMobDropData.Builder builder = new KeyMobDropData.Builder();
            builder.setChance(chance)
                    .setMinimum(minimum)
                    .setMaximum(maximum)
                    .setKeyName(childrenKey)
                    .setLootContainerKey(containerDataKey);
            data.put(childrenKey, builder.build());
        }
        return entityTypeFromList;
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
