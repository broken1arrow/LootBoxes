package org.brokenarrow.lootboxes.lootdata;

import org.broken.arrow.library.yaml.YamlFileManager;
import org.broken.arrow.library.yaml.utillity.ConfigurationWrapper;
import org.brokenarrow.lootboxes.Lootboxes;
import org.brokenarrow.lootboxes.builder.*;
import org.brokenarrow.lootboxes.untlity.Facing;
import org.brokenarrow.lootboxes.untlity.ServerVersion;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static org.brokenarrow.lootboxes.untlity.RunTimedTask.runtaskLater;
import static org.brokenarrow.lootboxes.untlity.errors.Valid.checkNotNull;

public class ContainerDataCacheLegacy extends YamlFileManager {

    private static final ContainerDataCacheLegacy instance = new ContainerDataCacheLegacy();
    private final Map<String, ContainerDataBuilder> cacheContainerData = new HashMap<>();
    private final ContainerLocationCache containerLocationCache = new ContainerLocationCache();
    private final ChunkDataCache chunkDataCache = new ChunkDataCache();

    public ContainerDataCacheLegacy() {
        super(Lootboxes.getInstance(), "container_data.db", true, false);
    }

    public Map<String, ContainerDataBuilder> getCacheContainerData() {
        return this.cacheContainerData;
    }

    public void addCachedLocation(final String containerDataName, final Map<BlockKey, ContainerData> map, final Map<String, KeysData> keysDataMap) {
        final Set<BlockKey> linkedContainerData;
        final Map<String, KeysData> cacheKeysData;
        if (map == null || map.isEmpty()) {
            linkedContainerData = this.getLinkedContainers(containerDataName).keySet();
        } else
            linkedContainerData = map.keySet();
        if (keysDataMap == null || keysDataMap.isEmpty()) {
            cacheKeysData = this.getCacheKeysData(containerDataName);
        } else
            cacheKeysData = keysDataMap;
        for (final BlockKey location : linkedContainerData) {
            if (location != null) {
                this.getContainerLocationCache().put(location.getLocation(), new LocationData(containerDataName, cacheKeysData));
                this.getChunkDataCache().setChunkData(location.getLocation());
            }
        }
    }

    public void setNewContainerData(String container, final Material material) {
        if (container.contains(" "))
            container = container.trim().replace(" ", "_");
        final ContainerDataBuilder builder = new ContainerDataBuilder.LootContainerBuilder()
                .setContainerDataLinkedToLootTable("")
                .setSpawningContainerWithCooldown(true)
                .setRandomLootContainerFacing(Facing.RANDOM)
                .setRandomLootContainerItem(Material.CHEST)
                .setCooldown(1800)
                .setParticleEffects(new HashMap<>())
                .setEnchant(false)
                .setIcon(material)
                .setDisplayName("")
                .setLore(new ArrayList<>())
                .setContainerData(new HashMap<>())
                .setKeysData(new HashMap<>())
                .setSpawnContainerFromWorldCenter(false)
                .setSpawnContainerFromPlayerCenter(false)
                .build();

        cacheContainerData.put(container, builder);
        saveTask();
        addContainerToEffectList(builder);
        this.addContainerToSpawnTask(container, 1800);
    }

    public ContainerDataBuilder getCacheContainerData(final String containerKey) {
        return this.getCacheContainerData().get(containerKey);
    }

    @Nullable
    public ParticleEffect getParticleEffect(final String container, Object particle) {
        ContainerDataBuilder lootContainerData = this.getCacheContainerData(container);
        if (lootContainerData != null)
            return lootContainerData.getParticleEffect(particle.toString());
        return null;
    }

    public List<?> getParticlesList(final String containerKey) {
        ContainerDataBuilder lootContainerData = this.getCacheContainerData(containerKey);
        if (lootContainerData != null) {

            Map<String, ParticleEffect> particleEffects = lootContainerData.getParticleEffects();
            if (particleEffects != null)
                if (org.broken.arrow.library.menu.utility.ServerVersion.atLeast(13.0))
                    return particleEffects.values().stream().map(particle -> particle.getSpigotParticle().getParticle()).collect(Collectors.toList());
                else
                    return particleEffects.values().stream().map(ParticleEffect::getEffect).collect(Collectors.toList());
        }
        return new ArrayList<>();
    }

    public List<ParticleEffect> getParticleEffectList(final String container) {
        ContainerDataBuilder lootContainerData = this.getCacheContainerData(container);
        if (lootContainerData != null) {

            Map<String, ParticleEffect> list = lootContainerData.getParticleEffects();
            if (list != null)
                return new ArrayList<>(list.values());
        }
        return new ArrayList<>();
    }

    public boolean containsParticleEffect(@NotNull final String containerData, String particle) {
        if (particle == null) return false;

        ContainerDataBuilder lootContainerData = this.getCacheContainerData(containerData);
        if (lootContainerData == null) return false;

        Map<String, ParticleEffect> particleEffect = lootContainerData.getParticleEffects();
        if (particleEffect == null || particleEffect.isEmpty()) return false;

        return particleEffect.containsKey(particle);
    }

    public boolean containsParticleEffect(@NotNull final LootContainerData lootContainerData, String particle) {
        if (particle == null) return false;

        @Nullable Map<String, ParticleEffect> particleEffect = lootContainerData.getParticleEffects();
        if (particleEffect == null || particleEffect.isEmpty()) return false;

        return particleEffect.containsKey(particle);
    }

    public void removeParticleEffect(@NotNull final String containerData, final String particle) {
        if (particle == null) return;

        ContainerDataBuilder lootContainerData = this.getCacheContainerData(containerData);
        if (lootContainerData == null) return;

        Map<String, ParticleEffect> particleEffect = lootContainerData.getParticleEffects();
        if (particleEffect.isEmpty()) return;

        particleEffect.remove(particle);
    }

    public void removeParticleEffect(@NotNull final ContainerDataBuilder lootContainerData, final String particle) {
        if (particle == null) return;

        Map<String, ParticleEffect> particleEffect = lootContainerData.getParticleEffects();
        if (particleEffect == null || particleEffect.isEmpty()) return;

        particleEffect.remove(particle);
        addContainerToEffectList(lootContainerData);
    }

    public void setParticleEffects(@NotNull final String containerDataName, @NotNull final String particle, @NotNull final ParticleEffect.Builder particleBuilder) {
        ContainerDataBuilder lootContainerData = this.getCacheContainerData(containerDataName);
        final ContainerDataBuilder.LootContainerBuilder lootContainerBuilder = lootContainerData.getBuilder();
        Map<String, ParticleEffect> particleEffect = lootContainerData.getParticleEffects();
        if (particleEffect == null) particleEffect = new HashMap<>();

        particleEffect.put(particle, particleBuilder.build());
        lootContainerBuilder.setParticleEffects(particleEffect);
        ContainerDataBuilder containerData = lootContainerBuilder;
        this.setContainerData(containerDataName, containerData);
        addContainerToEffectList(containerData);
    }

    public ContainerLocationCache getContainerLocationCache() {
        return containerLocationCache;
    }

    public ChunkDataCache getChunkDataCache() {
        return chunkDataCache;
    }

    public Map<BlockKey, ContainerData> getLinkedContainerData(final String container) {
        final ContainerDataBuilder lootContainerData = this.getCacheContainerData(container);
        if (lootContainerData != null) {
            if (lootContainerData.getLinkedContainerData() != null)
                return lootContainerData.getLinkedContainerData();
            else new HashMap<>();
        }

        return new HashMap<>();
    }

    public ContainerDataBuilder.LootContainerBuilder getCacheContainerBuilder(final String container) {
        final ContainerDataBuilder lootContainerData = this.getCacheContainerData(container);
        if (lootContainerData != null)
            return lootContainerData.getBuilder();

        return null;
    }

    public KeysData getCacheKey(final String container, final String keyName) {
        final ContainerDataBuilder lootContainerData = this.getCacheContainerData(container);
        if (lootContainerData != null) {
            if (keyName.startsWith("Keys_"))
                return lootContainerData.getKeysData().get(keyName);
            return lootContainerData.getKeysData().get(keyName);
        }

        return null;
    }


    public Map<BlockKey, ContainerData> getLinkedContainers(final String containerDataCacheName) {
        final ContainerDataBuilder lootContainerData = this.getCacheContainerData(containerDataCacheName);
        if (lootContainerData != null)
            return lootContainerData.getLinkedContainerData();

        return new HashMap<>();
    }

    public void removeCacheContainerData(final String container) {
        cacheContainerData.remove(container);
        saveTask();
    }

    public KeysData removeCacheKey(final String containerDataCacheName, final String keyName) {
        saveTask();
        return getCacheKeysData(containerDataCacheName).remove(keyName);
    }

    public Map<String, KeysData> getCacheKeysData(final String containerDataCacheName) {
        final ContainerDataBuilder lootContainerData = this.getCacheContainerData(containerDataCacheName);
        if (lootContainerData != null)
            return lootContainerData.getKeysData();

        return new HashMap<>();
    }

    public void setKeyData(final String containerData, String keyName, final KeysData keysData) {
        if (keyName.contains(" "))
            keyName = keyName.trim().replace(" ", "_");
        final ContainerDataBuilder lootContainerData = getCacheContainerData(containerData);
        checkNotNull(lootContainerData, "Some reason are ContainerDataBuilder for this containerData " + containerData + " is null.");
        final Map<String, org.brokenarrow.lootboxes.builder.KeysData> keysDataMap = lootContainerData.getKeysData();
        keysDataMap.put(keyName, keysData);
        final ContainerDataBuilder.LootContainerBuilder lootContainerBuilder = lootContainerData.getBuilder();
        lootContainerBuilder.setKeysData(keysDataMap);

        this.setContainerData(containerData, lootContainerBuilder.build());
        addCachedLocation(containerData, new HashMap<>(), keysDataMap);
        if (lootContainerData.isSpawningContainerWithCooldown())
            addContainerToSpawnTask(containerData, lootContainerData.getCooldown());
        saveTask();
    }

    public boolean containsKeyName(final String containerData, final String keyName) {
        final ContainerDataBuilder builder = getCacheContainerData(containerData);

        return builder != null && builder.getKeysData().get(keyName) != null;

    }

    public void setKeyData(final String containerKey, final String keyName, Consumer<KeysDataWrapper> keysDataProvider) {
        final ContainerDataBuilder containerData = getCacheContainerData(containerKey);
        checkNotNull(containerData, "Some reason are ContainerDataBuilder for this containerData " + containerKey + " not set.");

        Map<String, KeysData> keyDataMap = containerData.getKeysData();
        if (keyDataMap == null)
            keyDataMap = new HashMap<>();
        KeysData keysData = keyDataMap.get(keyName);
        if (keysData == null)
            keysData = new KeysData(keyName, "", "", 1, Material.TRIPWIRE_HOOK, new ArrayList<>());
        keysData.updateKeyData(keysDataProvider);
        final ContainerDataBuilder lootContainerData = containerData.getBuilder().setKeysData(keyDataMap).build();

        this.setContainerData(containerKey, lootContainerData);
        this.addCachedLocation(containerKey, new HashMap<>(), keyDataMap);
        if (lootContainerData.isSpawningContainerWithCooldown())
            this.addContainerToSpawnTask(containerKey, lootContainerData.getCooldown());
    }

    public List<String> getListOfKeys(final String containerDataCacheName) {
        final List<String> keyNameList = new ArrayList<>();

        for (final String keyName : this.getCacheKeysData(containerDataCacheName).keySet())
            if (keyName != null) {
                keyNameList.add(keyName);
            }
        return keyNameList;
    }

    public boolean containsContainerData(final String key) {
        return this.getCacheContainerData(key) != null;
    }

    public void addContainerToSpawnTask(final String mainKey, final long cooldown) {
        Lootboxes.getInstance().getSpawnedContainers().setCachedTimeMap(mainKey, cooldown);
    }

    public void addContainerToEffectList(final ContainerDataBuilder lootContainerData) {
        Map<BlockKey, ContainerData> linkedcontainerData = lootContainerData.getLinkedContainerData();
        if (linkedcontainerData == null || linkedcontainerData.isEmpty()) return;

        for (BlockKey location : linkedcontainerData.keySet())
            Lootboxes.getInstance().getSpawnContainerEffectsTask().addLocationInList(location.getLocation());
    }

    public List<String> getContainerData() {
        return this.getCacheContainerData().keySet().stream().filter(Objects::nonNull).collect(Collectors.toList());
    }

    public void setContainerData(final String containerData, final ContainerDataBuilder lootContainerData) {

        this.cacheContainerData.put(containerData, lootContainerData);
        this.addCachedLocation(containerData, new HashMap<>(), new HashMap<>());
        if (!lootContainerData.isSpawningContainerWithCooldown())
            addContainerToSpawnTask(containerData, lootContainerData.getCooldown());
        final ContainerDataBuilder data = this.getCacheContainerData(containerData);
        if (data != null) {
            for (final BlockKey location : data.getLinkedContainerData().keySet()) {
                this.getChunkDataCache().setChunkData(location.getLocation());
                Lootboxes.getInstance().getSpawnContainerEffectsTask().addLocationInList(location.getLocation());
            }
        }
        saveTask();
    }

    public void saveTask() {
        runtaskLater(5, this::save, true);
    }

    @Override
    protected void saveDataToFile(@NotNull final File file, @NotNull final ConfigurationWrapper configurationWrapper) {
/*		try {
			FileConfiguration configuration = new YamlConfiguration();
			for (final Map.Entry<String, ContainerDataBuilder> childrenKey : cacheContainerData.entrySet())
				if (childrenKey != null) {
					configuration.set("Data" + "." + childrenKey.getKey(), childrenKey.getValue());
				}
			this.saveToFile(file, configuration);
		} catch (final Exception ex) {
			ex.printStackTrace();
		}*/
    }

    @Override
    protected void loadSettingsFromYaml(final File file, FileConfiguration configuration) {
        final ConfigurationSection mainConfigKeys = configuration.getConfigurationSection("Data");
        if (mainConfigKeys != null)
            for (String mainKey : mainConfigKeys.getKeys(false)) {
                if (mainKey == null) continue;
                ContainerDataBuilder lootContainerData;
                if (Lootboxes.getInstance().getServerVersion().atLeast(ServerVersion.Version.v1_13))
                    lootContainerData = configuration.getSerializable("Data." + mainKey, ContainerDataBuilder.class);
                else
                    lootContainerData = (ContainerDataBuilder) configuration.get("Data." + mainKey, ContainerDataBuilder.class);
                if (lootContainerData == null) continue;

                if (mainKey.contains(" "))
                    mainKey = mainKey.trim().replace(" ", "_");

                addCachedLocation(mainKey, lootContainerData.getLinkedContainerData(), lootContainerData.getKeysData());

                cacheContainerData.put(mainKey, lootContainerData);
                if (lootContainerData.isSpawningContainerWithCooldown())
                    addContainerToSpawnTask(mainKey, lootContainerData.getCooldown());
            }
    }

    public static ContainerDataCacheLegacy getInstance() {
        return instance;
    }


}
