package org.brokenarrow.lootboxes.lootdata;

import org.broken.arrow.library.yaml.YamlFileManager;
import org.broken.arrow.library.yaml.utillity.ConfigurationWrapper;
import org.brokenarrow.lootboxes.Lootboxes;
import org.brokenarrow.lootboxes.builder.*;
import org.brokenarrow.lootboxes.untlity.ServerVersion;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.brokenarrow.lootboxes.untlity.RunTimedTask.runtaskLater;

public class ContainerDataCache extends YamlFileManager {
    private final Map<String, LootContainerData> cacheContainerData = new HashMap<>();
    private final ContainerLocationCache containerLocationCache = new ContainerLocationCache();
    private final ChunkDataCache chunkDataCache = new ChunkDataCache();

    public ContainerDataCache() {
        super(Lootboxes.getInstance(), "lootContainers", false, true);
        this.setExtension("db");
    }

    public Map<String, LootContainerData> getCacheContainerData() {
        return this.cacheContainerData;
    }

    public void setContainerData(final String containerData, final LootContainerData lootContainerData) {
        this.cacheContainerData.put(containerData, lootContainerData);
        this.addCachedLocation(containerData, new HashMap<>(), new HashMap<>());
        if (!lootContainerData.isSpawningContainerWithCooldown())
            addContainerToSpawnTask(containerData, lootContainerData.getCooldown());
        final LootContainerData data = this.getCacheContainerData(containerData);
        if (data != null) {
            for (final BlockKey location : data.getLinkedContainerData().keySet()) {
                this.getChunkDataCache().setChunkData(location.getLocation());
                Lootboxes.getInstance().getSpawnContainerEffectsTask().addLocationInList(location.getLocation());
            }
        }
        saveTask();
    }

    public <T> T write(final String containerKey, final Function<LootContainerData, T> containerDataBuilder) {
        LootContainerData containerData = getOrCreateCacheContainerData(containerKey);
        T data = containerDataBuilder.apply(containerData);
        cacheContainer(containerKey, containerData);
        return data;
    }

    public void write(final String containerKey, final Consumer<LootContainerData> callback) {
        LootContainerData containerData = getOrCreateCacheContainerData(containerKey);
        callback.accept(containerData);
        cacheContainer(containerKey, containerData);
    }

    @Nullable
    public <T> T read(final String containerKey, final Function<LootContainerData, T> callBack) {
        LootContainerData builder = getCacheContainerData(containerKey);
        if (builder == null) return null;
        return callBack.apply(builder);
    }

    public void read(final String containerKey, final Consumer<LootContainerData> callBack) {
        LootContainerData builder = getCacheContainerData(containerKey);
        if (builder == null) return;
        callBack.accept(builder);
    }

    public List<String> getContainerData() {
        return this.getCacheContainerData().keySet().stream().filter(Objects::nonNull).collect(Collectors.toList());
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
        final LootContainerData builder = new LootContainerData()
                .setContainerDataLinkedToLootTable("")
                .setSpawningContainerWithCooldown(true)
                .setRandomLootContainer(new ContainerData())
                .setCooldown(1800)
                .setParticleEffects(new HashMap<>())
                .setEnchant(false)
                .setIcon(material)
                .setDisplayName("")
                .setLore(new ArrayList<>())
                .setContainerData(new HashMap<>())
                .setKeysData(new HashMap<>())
                .setCenterMode(CenterMode.PLAYER_FOLLOW)
                .build();

        cacheContainerData.put(container, builder);
        save(container);
        addContainerToEffectList(builder);
        this.addContainerToSpawnTask(container, 1800);
    }

    public LootContainerData getCacheContainerData(final String containerKey) {
        return this.getCacheContainerData().get(containerKey);
    }

    @NotNull
    public LootContainerData getOrCreateCacheContainerData(final String containerKey) {
        return this.getCacheContainerData().getOrDefault(containerKey, new LootContainerData());
    }

    @Nullable
    public ParticleEffect getParticleEffect(final String container, Object particle) {
        LootContainerData lootContainerData = this.getCacheContainerData(container);
        if (lootContainerData != null)
            return lootContainerData.getParticleEffect(particle);
        return null;
    }

    public List<?> getParticlesList(final String containerKey) {
        LootContainerData lootContainerData = this.getCacheContainerData(containerKey);
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
        LootContainerData lootContainerData = this.getCacheContainerData(container);
        if (lootContainerData != null) {

            Map<String, ParticleEffect> list = lootContainerData.getParticleEffects();
            if (list != null)
                return new ArrayList<>(list.values());
        }
        return new ArrayList<>();
    }

    public boolean containsParticleEffect(@NotNull final String containerData, Object particle) {
        if (particle == null) return false;

        LootContainerData lootContainerData = this.getCacheContainerData(containerData);
        if (lootContainerData == null) return false;

        Map<String, ParticleEffect> particleEffect = lootContainerData.getParticleEffects();
        if (particleEffect == null || particleEffect.isEmpty()) return false;

        if (particle instanceof Particle)
            return particleEffect.containsKey(((Particle) particle).name());
        if (particle instanceof Effect)
            return particleEffect.containsKey(((Effect) particle).name());

        return false;
    }

    public boolean containsParticleEffect(@NotNull final LootContainerData lootContainerData, final String particle) {
        if (particle == null) return false;

        @Nullable Map<String, ParticleEffect> particleEffect = lootContainerData.getParticleEffects();
        if (particleEffect == null || particleEffect.isEmpty()) return false;

        return particleEffect.containsKey(particle);
    }

    public void removeParticleEffect(@NotNull final String containerData, final String particle) {
        if (particle == null) return;

        LootContainerData lootContainerData = this.getCacheContainerData(containerData);
        if (lootContainerData == null) return;

        Map<String, ParticleEffect> particleEffect = lootContainerData.getParticleEffects();
        if (particleEffect == null || particleEffect.isEmpty()) return;

        particleEffect.remove(particle);
    }

    public void removeParticleEffect(@NotNull final LootContainerData lootContainerData, Object particle) {
        if (particle == null) return;

        @Nullable Map<String, ParticleEffect> particleEffect = lootContainerData.getParticleEffects();
        if (particleEffect == null || particleEffect.isEmpty()) return;

        if (particle instanceof Particle)
            particleEffect.remove(((Particle) particle).name());
        if (particle instanceof Effect)
            particleEffect.remove(((Effect) particle).name());

        addContainerToEffectList(lootContainerData);
    }

    public ContainerLocationCache getContainerLocationCache() {
        return containerLocationCache;
    }

    public ChunkDataCache getChunkDataCache() {
        return chunkDataCache;
    }

    public Map<BlockKey, ContainerData> getLinkedContainerData(final String container) {
        final LootContainerData lootContainerData = this.getCacheContainerData(container);
        if (lootContainerData != null) {
            if (lootContainerData.getLinkedContainerData() != null)
                return lootContainerData.getLinkedContainerData();
            else new HashMap<>();
        }

        return new HashMap<>();
    }

    public KeysData getCacheKey(final String containerKey, final String keyName) {
        final LootContainerData lootContainerData = this.getCacheContainerData(containerKey);
        if (lootContainerData != null) {
            if (keyName.startsWith("Keys_"))
                return lootContainerData.getKeysData().get(keyName);
            return lootContainerData.getKeysData().get(keyName);
        }

        return null;
    }


    public Map<BlockKey, ContainerData> getLinkedContainers(final String containerDataCacheName) {
        final LootContainerData lootContainerData = this.getCacheContainerData(containerDataCacheName);
        if (lootContainerData != null)
            return lootContainerData.getLinkedContainerData();

        return new HashMap<>();
    }

    public void removeCacheContainerData(final String containerKey) {
        cacheContainerData.remove(containerKey);
        final File folder = new File(plugin.getDataFolder(), this.getPath() + "/" + containerKey + "." + getExtension());
        folder.delete();
    }

    public KeysData removeCacheKey(final String containerDataCacheName, final String keyName) {
        saveTask();
        return getCacheKeysData(containerDataCacheName).remove(keyName);
    }

    public Map<String, KeysData> getCacheKeysData(final String containerDataCacheName) {
        final LootContainerData lootContainerData = this.getCacheContainerData(containerDataCacheName);
        if (lootContainerData != null)
            return lootContainerData.getKeysData();

        return new HashMap<>();
    }

    public boolean containsKeyName(final String containerData, final String keyName) {
        final LootContainerData builder = getCacheContainerData(containerData);

        return builder != null && builder.getKeysData().get(keyName) != null;

    }

    public List<String> getListOfKeys(final String containerDataCacheName) {
        final List<String> keyNameList = new ArrayList<>();

        Map<String, KeysData> cacheKeysData = this.getCacheKeysData(containerDataCacheName);
        if (cacheKeysData != null) {
            for (final String keyName : cacheKeysData.keySet())
                if (keyName != null) {
                    keyNameList.add(keyName);
                }
        }
        return keyNameList;
    }

    public boolean containsContainerData(final String key) {
        return this.getCacheContainerData(key) != null;
    }

    public void addContainerToSpawnTask(final String mainKey, final long cooldown) {
        Lootboxes.getInstance().getSpawnedContainers().setCachedTimeMap(mainKey, cooldown);
    }

    public void addContainerToEffectList(final LootContainerData lootContainerData) {
        Map<BlockKey, ContainerData> linkedContainerData = lootContainerData.getLinkedContainerData();
        if (linkedContainerData == null || linkedContainerData.isEmpty()) return;

        for (BlockKey location : linkedContainerData.keySet())
            Lootboxes.getInstance().getSpawnContainerEffectsTask().addLocationInList(location.getLocation());
    }

    public void saveTask() {
        runtaskLater(5, this::save, true);
    }

    @Override
    protected void saveDataToFile(@NotNull final File file, @NotNull final ConfigurationWrapper configurationWrapper) {
        try {
            LootContainerData containerData = cacheContainerData.get(this.getNameOfFile(file.toString()));
            final FileConfiguration configuration = new YamlConfiguration();
            if (containerData != null) {
                configuration.set("Data", containerData);
                this.saveToFile(file, configuration);
            }
        } catch (final Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    protected void loadSettingsFromYaml(final File file, FileConfiguration fileConfiguration) {
        LootContainerData lootContainerData;
        YamlConfiguration configuration = YamlConfiguration.loadConfiguration(file);
        if (Lootboxes.getInstance().getServerVersion().atLeast(ServerVersion.Version.v1_13))
            lootContainerData = configuration.getSerializable("Data", LootContainerData.class);
        else
            lootContainerData = (LootContainerData) configuration.get("Data", LootContainerData.class);
        if (lootContainerData == null) return;

        final String containerName = this.getNameOfFile(file.toString());
        addCachedLocation(containerName, lootContainerData.getLinkedContainerData(), lootContainerData.getKeysData());

        cacheContainerData.put(containerName, lootContainerData);
        if (lootContainerData.isSpawningContainerWithCooldown())
            addContainerToSpawnTask(containerName, lootContainerData.getCooldown());
    }

    private void cacheContainer(@NotNull final String containerKey, @NotNull final LootContainerData containerLootContainerData) {
        LootContainerData built = containerLootContainerData.build();
        this.cacheContainerData.put(containerKey, built);

        if (!built.isSpawningContainerWithCooldown())
            addContainerToSpawnTask(containerKey, built.getCooldown());
        for (final BlockKey blockKey : built.getLinkedContainerData().keySet()) {
            this.getChunkDataCache().setChunkData(blockKey.getLocation());
            this.getContainerLocationCache().put(blockKey.getLocation(), new LocationData(containerKey, built.getKeysData()));
            final Map<String, ParticleEffect> particleEffects = built.getParticleEffects();
            if (particleEffects != null && particleEffects.isEmpty())
                Lootboxes.getInstance().getSpawnContainerEffectsTask().addLocationInList(blockKey.getLocation());
        }
        save(containerKey);
        //saveTask();
    }

}
