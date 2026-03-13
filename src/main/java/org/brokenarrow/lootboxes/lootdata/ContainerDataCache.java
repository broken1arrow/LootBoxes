package org.brokenarrow.lootboxes.lootdata;

import org.broken.arrow.library.yaml.YamlFileManager;
import org.broken.arrow.library.yaml.utillity.ConfigurationWrapper;
import org.brokenarrow.lootboxes.Lootboxes;
import org.brokenarrow.lootboxes.builder.*;
import org.brokenarrow.lootboxes.untlity.Facing;
import org.brokenarrow.lootboxes.untlity.ServerVersion;
import org.bukkit.Location;
import org.bukkit.Material;
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
    private final Map<String, ContainerDataBuilder> cacheContainerData = new HashMap<>();
    private final ContainerLocationCache containerLocationCache = new ContainerLocationCache();
    private final ChunkDataCache chunkDataCache = new ChunkDataCache();

    public ContainerDataCache() {
        super(Lootboxes.getInstance(), "containers", false, true);
        this.setExtension("db");
    }

    public Map<String, ContainerDataBuilder> getCacheContainerData() {
        return this.cacheContainerData;
    }

    public void setContainerData(final String containerData, final ContainerDataBuilder containerDataBuilder) {
        this.cacheContainerData.put(containerData, containerDataBuilder);
        this.addCachedLocation(containerData, new HashMap<>(), new HashMap<>());
        if (!containerDataBuilder.isSpawningContainerWithCooldown())
            addContainerToSpawnTask(containerData, containerDataBuilder.getCooldown());
        final ContainerDataBuilder data = this.getCacheContainerData(containerData);
        if (data != null) {
            for (final Location location : data.getLinkedContainerData().keySet()) {
                this.getChunkDataCache().setChunkData(location);
                Lootboxes.getInstance().getSpawnContainerEffectsTask().addLocationInList(location);
            }
        }
        saveTask();
    }

    public <T> T write(final String containerKey, final Function<ContainerDataBuilder.Builder, T> containerDataBuilder) {
        ContainerDataBuilder builder = getOrCreateCacheContainerData(containerKey);
        ContainerDataBuilder.Builder containerBuilder = builder.getBuilder();
        T data = containerDataBuilder.apply(containerBuilder);
        cacheContainer(containerKey, containerBuilder);
        return data;
    }



    public void write(final String containerKey, final Consumer<ContainerDataBuilder.Builder> containerDataBuilder) {
        ContainerDataBuilder builder = getOrCreateCacheContainerData(containerKey);
        ContainerDataBuilder.Builder containerBuilder = builder.getBuilder();
        containerDataBuilder.accept(containerBuilder);
        cacheContainer(containerKey, containerBuilder);
    }

    private void cacheContainer(String containerKey, ContainerDataBuilder.Builder containerBuilder) {
        ContainerDataBuilder built = containerBuilder.build();
        this.cacheContainerData.put(containerKey, built);

        if (!built.isSpawningContainerWithCooldown())
            addContainerToSpawnTask(containerKey, built.getCooldown());
        for (final Location location : built.getLinkedContainerData().keySet()) {
            this.getChunkDataCache().setChunkData(location);
            this.getContainerLocationCache().put(location, new LocationData(containerKey, built.getKeysData()));
            final Map<Object, ParticleEffect> particleEffects = built.getParticleEffects();
            if (particleEffects != null && particleEffects.isEmpty())
                Lootboxes.getInstance().getSpawnContainerEffectsTask().addLocationInList(location);
        }
        saveTask();
    }

    public List<String> getContainerData() {
        return this.getCacheContainerData().keySet().stream().filter(Objects::nonNull).collect(Collectors.toList());
    }

    public void addCachedLocation(final String containerDataName, final Map<Location, ContainerData> map, final Map<String, KeysData> keysDataMap) {
        final Set<Location> linkedContainerData;
        final Map<String, KeysData> cacheKeysData;
        if (map.isEmpty()) {
            linkedContainerData = this.getLinkedContainers(containerDataName).keySet();
        } else
            linkedContainerData = map.keySet();
        if (keysDataMap.isEmpty()) {
            cacheKeysData = this.getCacheKeysData(containerDataName);
        } else
            cacheKeysData = keysDataMap;
        for (final Location location : linkedContainerData) {
            if (location != null) {
                this.getContainerLocationCache().put(location, new LocationData(containerDataName, cacheKeysData));
                this.getChunkDataCache().setChunkData(location);
            }
        }
    }

    public void setNewContainerData(String container, final Material material) {
        if (container.contains(" "))
            container = container.trim().replace(" ", "_");
        final ContainerDataBuilder builder = new ContainerDataBuilder.Builder()
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

    @NotNull
    public ContainerDataBuilder getOrCreateCacheContainerData(final String containerKey) {
        return this.getCacheContainerData().getOrDefault(containerKey, new ContainerDataBuilder.Builder().build());
    }

    @Nullable
    public ParticleEffect getParticleEffect(final String container, Object particle) {
        ContainerDataBuilder containerDataBuilder = this.getCacheContainerData(container);
        if (containerDataBuilder != null)
            return containerDataBuilder.getParticleEffect(particle);
        return null;
    }

    public List<?> getParticlesList(final String containerKey) {
        ContainerDataBuilder containerDataBuilder = this.getCacheContainerData(containerKey);
        if (containerDataBuilder != null) {

            Map<Object, ParticleEffect> particleEffects = containerDataBuilder.getParticleEffects();
            if (particleEffects != null)
                if (org.broken.arrow.library.menu.utility.ServerVersion.atLeast(13.0))
                    return particleEffects.values().stream().map(particle -> particle.getSpigotParticle().getParticle()).collect(Collectors.toList());
                else
                    return particleEffects.values().stream().map(ParticleEffect::getEffect).collect(Collectors.toList());
        }
        return new ArrayList<>();
    }

    public List<ParticleEffect> getParticleEffectList(final String container) {
        ContainerDataBuilder containerDataBuilder = this.getCacheContainerData(container);
        if (containerDataBuilder != null) {

            Map<Object, ParticleEffect> list = containerDataBuilder.getParticleEffects();
            if (list != null)
                return new ArrayList<>(list.values());
        }
        return new ArrayList<>();
    }

    public boolean containsParticleEffect(@NotNull final String containerData, Object particle) {
        if (particle == null) return false;

        ContainerDataBuilder containerDataBuilder = this.getCacheContainerData(containerData);
        if (containerDataBuilder == null) return false;

        Map<Object, ParticleEffect> particleEffect = containerDataBuilder.getParticleEffects();
        if (particleEffect == null || particleEffect.isEmpty()) return false;

        return particleEffect.containsKey(particle);
    }

    public boolean containsParticleEffect(@NotNull final ContainerDataBuilder containerDataBuilder, Object particle) {
        if (particle == null) return false;

        Map<Object, ParticleEffect> particleEffect = containerDataBuilder.getParticleEffects();
        if (particleEffect == null || particleEffect.isEmpty()) return false;

        return particleEffect.containsKey(particle);
    }

    public void removeParticleEffect(@NotNull final String containerData, Object particle) {
        if (particle == null) return;

        ContainerDataBuilder containerDataBuilder = this.getCacheContainerData(containerData);
        if (containerDataBuilder == null) return;

        Map<Object, ParticleEffect> particleEffect = containerDataBuilder.getParticleEffects();
        if (particleEffect.isEmpty()) return;

        particleEffect.remove(particle);
    }

    public void removeParticleEffect(@NotNull final ContainerDataBuilder containerDataBuilder, Object particle) {
        if (particle == null) return;

        Map<Object, ParticleEffect> particleEffect = containerDataBuilder.getParticleEffects();
        if (particleEffect == null || particleEffect.isEmpty()) return;

        particleEffect.remove(particle);
        addContainerToEffectList(containerDataBuilder);
    }

    public void setParticleEffects(@NotNull final String containerDataName, @NotNull Object particle, @NotNull final ParticleEffect.Builder particleBuilder) {
        ContainerDataBuilder containerDataBuilder = this.getCacheContainerData(containerDataName);
        final ContainerDataBuilder.Builder builder = containerDataBuilder.getBuilder();
        Map<Object, ParticleEffect> particleEffect = containerDataBuilder.getParticleEffects();
        if (particleEffect == null) particleEffect = new HashMap<>();

        particleEffect.put(particle, particleBuilder.build());
        builder.setParticleEffects(particleEffect);
        ContainerDataBuilder containerData = builder.build();
        this.setContainerData(containerDataName, containerData);
        addContainerToEffectList(containerData);
    }

    public ContainerLocationCache getContainerLocationCache() {
        return containerLocationCache;
    }

    public ChunkDataCache getChunkDataCache() {
        return chunkDataCache;
    }

    public Map<Location, ContainerData> getLinkedContainerData(final String container) {
        final ContainerDataBuilder containerDataBuilder = this.getCacheContainerData(container);
        if (containerDataBuilder != null) {
            if (containerDataBuilder.getLinkedContainerData() != null)
                return containerDataBuilder.getLinkedContainerData();
            else new HashMap<>();
        }

        return new HashMap<>();
    }

    public ContainerDataBuilder.Builder getCacheContainerBuilder(final String container) {
        final ContainerDataBuilder containerDataBuilder = this.getCacheContainerData(container);
        if (containerDataBuilder != null)
            return containerDataBuilder.getBuilder();

        return null;
    }

    public KeysData getCacheKey(final String container, final String keyName) {
        final ContainerDataBuilder containerDataBuilder = this.getCacheContainerData(container);
        if (containerDataBuilder != null) {
            if (keyName.startsWith("Keys_"))
                return containerDataBuilder.getKeysData().get(keyName);
            return containerDataBuilder.getKeysData().get(keyName);
        }

        return null;
    }


    public Map<Location, ContainerData> getLinkedContainers(final String containerDataCacheName) {
        final ContainerDataBuilder containerDataBuilder = this.getCacheContainerData(containerDataCacheName);
        if (containerDataBuilder != null)
            return containerDataBuilder.getLinkedContainerData();

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
        final ContainerDataBuilder containerDataBuilder = this.getCacheContainerData(containerDataCacheName);
        if (containerDataBuilder != null)
            return containerDataBuilder.getKeysData();

        return new HashMap<>();
    }

    public boolean containsKeyName(final String containerData, final String keyName) {
        final ContainerDataBuilder builder = getCacheContainerData(containerData);

        return builder != null && builder.getKeysData().get(keyName) != null;

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

    public void addContainerToEffectList(final ContainerDataBuilder containerDataBuilder) {
        Map<Location, ContainerData> linkedContainerData = containerDataBuilder.getLinkedContainerData();
        if (linkedContainerData == null || linkedContainerData.isEmpty()) return;

        for (Location location : linkedContainerData.keySet())
            Lootboxes.getInstance().getSpawnContainerEffectsTask().addLocationInList(location);
    }

    public void saveTask() {
        runtaskLater(5, this::save, true);
    }

    @Override
    protected void saveDataToFile(@NotNull final File file, @NotNull final ConfigurationWrapper configurationWrapper) {
        try {
            FileConfiguration configuration = new YamlConfiguration();
            for (final Map.Entry<String, ContainerDataBuilder> childrenKey : cacheContainerData.entrySet()) {
                if (childrenKey != null) {
                    configuration.set("Data", childrenKey.getValue());
                    this.saveToFile(new File(this.getDataFolder(), "containers/" + childrenKey.getKey() + ".db"), configuration);
                }
            }
        } catch (final Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    protected void loadSettingsFromYaml(final File file, FileConfiguration configuration) {
        ContainerDataBuilder containerDataBuilder;
        if (Lootboxes.getInstance().getServerVersion().atLeast(ServerVersion.Version.v1_13))
            containerDataBuilder = configuration.getSerializable("Data", ContainerDataBuilder.class);
        else
            containerDataBuilder = (ContainerDataBuilder) configuration.get("Data", ContainerDataBuilder.class);
        if (containerDataBuilder == null) return;

        final String containerName = this.getNameOfFile(file.toString());
        addCachedLocation(containerName, containerDataBuilder.getLinkedContainerData(), containerDataBuilder.getKeysData());

        cacheContainerData.put(containerName, containerDataBuilder);
        if (containerDataBuilder.isSpawningContainerWithCooldown())
            addContainerToSpawnTask(containerName, containerDataBuilder.getCooldown());
    }

}
