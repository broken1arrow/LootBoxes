package org.brokenarrow.lootboxes.lootdata;

import org.broken.arrow.library.yaml.YamlFileManager;
import org.broken.arrow.library.yaml.utillity.ConfigurationWrapper;
import org.brokenarrow.lootboxes.Lootboxes;
import org.brokenarrow.lootboxes.builder.ContainerData;
import org.brokenarrow.lootboxes.builder.ContainerDataBuilder;
import org.brokenarrow.lootboxes.builder.KeysData;
import org.brokenarrow.lootboxes.builder.KeysDataWrapper;
import org.brokenarrow.lootboxes.builder.LocationData;
import org.brokenarrow.lootboxes.builder.ParticleEffect;
import org.brokenarrow.lootboxes.untlity.ServerVersion;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static org.brokenarrow.lootboxes.untlity.RunTimedTask.runtaskLater;
import static org.brokenarrow.lootboxes.untlity.errors.Valid.checkNotNull;

public class ContainerDataCache extends YamlFileManager {

	private static final ContainerDataCache instance = new ContainerDataCache();
	private final Map<String, ContainerDataBuilder> cacheContainerData = new HashMap<>();
	private final ContainerLocationCache containerLocationCache = new ContainerLocationCache();
	private final ChunkDataCache chunkDataCache= new ChunkDataCache();

	public ContainerDataCache() {
		super(Lootboxes.getInstance(), "container_data.db");
	}

	public Map<String, ContainerDataBuilder> getCacheContainerData() {
		return this.cacheContainerData;
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
				if (org.broken.arrow.library.menu.utility.ServerVersion.atLeast(org.broken.arrow.library.menu.utility.ServerVersion.V1_13))
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

/*	public Map<String, KeysData> getCacheKeys(final String containerKey) {
		final ContainerDataBuilder containerDataBuilder = this.getCacheContainerData(containerKey);
		if (containerDataBuilder != null)
			return containerDataBuilder.getKeysData();

		return new HashMap<>();
	}

	public ContainerData getLinkedContainerData(final String containerKey, final Location location) {
		final ContainerDataBuilder containerDataBuilder = this.getCacheContainerData(containerKey);
		if (containerDataBuilder != null)
			return containerDataBuilder.getLinkedContainerData().get(location);

		return null;
	}*/

	public Map<Location, ContainerData> getLinkedContainers(final String containerDataCacheName) {
		final ContainerDataBuilder containerDataBuilder = this.getCacheContainerData(containerDataCacheName);
		if (containerDataBuilder != null)
			return containerDataBuilder.getLinkedContainerData();

		return new HashMap<>();
	}

	public void removeCacheContainerData(final String container) {
		cacheContainerData.remove(container);
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

	public void setKeyData(final String containerData, String keyName, final KeysData keysData) {
		if (keyName.contains(" "))
			keyName = keyName.trim().replace(" ", "_");
		final ContainerDataBuilder containerDataBuilder = getCacheContainerData(containerData);
		checkNotNull(containerDataBuilder, "Some reason are ContainerDataBuilder for this containerData " + containerData + " is null.");
		final Map<String, org.brokenarrow.lootboxes.builder.KeysData> keysDataMap = containerDataBuilder.getKeysData();
		keysDataMap.put(keyName, keysData);
		final ContainerDataBuilder.Builder builder = containerDataBuilder.getBuilder();
		builder.setKeysData(keysDataMap);

		this.setContainerData(containerData, builder.build());
		addCachedLocation(containerData, new HashMap<>(), keysDataMap);
		if (containerDataBuilder.isSpawningContainerWithCooldown())
			addContainerToSpawnTask(containerData, containerDataBuilder.getCooldown());
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
		final ContainerDataBuilder containerDataBuilder = containerData.getBuilder().setKeysData(keyDataMap).build();

		this.setContainerData(containerKey, containerDataBuilder);
		this.addCachedLocation(containerKey, new HashMap<>(), keyDataMap);
		if (containerDataBuilder.isSpawningContainerWithCooldown())
			this.addContainerToSpawnTask(containerKey, containerDataBuilder.getCooldown());
	}

/*
	public void setKeyData(final KeysToSave keysToSave, final Object objectToSave, final String container, final String keyName) {
		Map<String, KeysData> keyDataMap = getCacheKeys(container);

		Material material = null;
		if (keysToSave == KeysToSave.ITEM_TYPE) {
			if (objectToSave instanceof String)
				material = Enums.getIfPresent(Material.class, (String) objectToSave).orNull();
			else
				material = (Material) objectToSave;

		}
		if (keyDataMap == null)
			keyDataMap = new HashMap<>();
		final KeysData oldData = keyDataMap.get(keyName);
		final KeysData data = new org.brokenarrow.lootboxes.builder.KeysData(
				keysToSave == KeysToSave.KEY_NAME ? (String) objectToSave : oldData.getKeyName(),
				keysToSave == KeysToSave.DISPLAY_NAME ? (String) objectToSave : oldData.getDisplayName(),
				keysToSave == KeysToSave.LOOT_TABLE_LINKED ? (String) objectToSave : oldData.getLootTableLinked(),
				keysToSave == KeysToSave.AMOUNT_NEEDED ? (int) objectToSave : oldData.getAmountNeeded(),
				keysToSave == KeysToSave.ITEM_TYPE ? material : oldData.getItemType(),
				keysToSave == KeysToSave.LORE ? (List<String>) objectToSave : oldData.getLore());


		keyDataMap.put(keyName, data);

		final ContainerDataBuilder.Builder builder = getCacheContainerBuilder(container);
		checkNotNull(builder, "Some reason are ContainerDataBuilder for this containerData " + container + " is null.");
		final ContainerDataBuilder containerDataBuilder = builder.setKeysData(keyDataMap).build();

		this.setContainerData(container, containerDataBuilder);
		this.addCachedLocation(container, new HashMap<>(), keyDataMap);
		if (containerDataBuilder.isSpawningContainerWithCooldown())
			addContainerToSpawnTask(container, containerDataBuilder.getCooldown());
	}
*/

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
		Map<Location, ContainerData> linkedcontainerData = containerDataBuilder.getLinkedContainerData();
		if (linkedcontainerData == null || linkedcontainerData.isEmpty()) return;

		for (Location location : linkedcontainerData.keySet())
			Lootboxes.getInstance().getSpawnContainerEffectsTask().addLocationInList(location);
	}

	public List<String> getContainerData() {
		return this.getCacheContainerData().keySet().stream().filter(Objects::nonNull).collect(Collectors.toList());
	}

	public void setContainerData(final String containerData, final ContainerDataBuilder containerDataBuilder) {

		this.cacheContainerData.put(containerData, containerDataBuilder);
		this.addCachedLocation(containerData, new HashMap<>(), new HashMap<>());
		if (!containerDataBuilder.isSpawningContainerWithCooldown())
			addContainerToSpawnTask(containerData, containerDataBuilder.getCooldown());
		final ContainerDataBuilder data = this.getCacheContainerData(containerData);
		if (data != null)
        for (final Location location : data.getLinkedContainerData().keySet()) {
            this.getChunkDataCache().setChunkData(location);
            Lootboxes.getInstance().getSpawnContainerEffectsTask().addLocationInList(location);
        }
		saveTask();
	}

	public void saveTask() {
		runtaskLater(5, this::save, true);
	}

	@Override
	protected void saveDataToFile(@NotNull final File file, @NotNull final ConfigurationWrapper configurationWrapper) {
		try {
			FileConfiguration configuration = new YamlConfiguration();
			for (final Map.Entry<String, ContainerDataBuilder> childrenKey : cacheContainerData.entrySet())
				if (childrenKey != null) {
					configuration.set("Data" + "." + childrenKey.getKey(), childrenKey.getValue());
				}
			this.saveToFile(file, configuration);
		} catch (final Exception ex) {
			ex.printStackTrace();
		}
	}

	@Override
	protected void loadSettingsFromYaml(final File file, FileConfiguration configuration) {
		final ConfigurationSection MainConfigKeys = configuration.getConfigurationSection("Data");

		if (MainConfigKeys != null)
			for (String mainKey : MainConfigKeys.getKeys(false)) {
				if (mainKey == null) continue;
				ContainerDataBuilder containerDataBuilder;
				if (Lootboxes.getInstance().getServerVersion().atLeast(ServerVersion.Version.v1_13))
					containerDataBuilder = configuration.getSerializable("Data." + mainKey, ContainerDataBuilder.class);
				else
					containerDataBuilder = (ContainerDataBuilder) configuration.get("Data." + mainKey, ContainerDataBuilder.class);
				if (containerDataBuilder == null) continue;

				if (mainKey.contains(" "))
					mainKey = mainKey.trim().replace(" ", "_");

				addCachedLocation(mainKey, containerDataBuilder.getLinkedContainerData(), containerDataBuilder.getKeysData());

				cacheContainerData.put(mainKey, containerDataBuilder);
				if (containerDataBuilder.isSpawningContainerWithCooldown())
					addContainerToSpawnTask(mainKey, containerDataBuilder.getCooldown());
			}
	}

	public static ContainerDataCache getInstance() {
		return instance;
	}


}
