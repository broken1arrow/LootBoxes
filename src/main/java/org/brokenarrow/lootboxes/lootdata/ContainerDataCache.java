package org.brokenarrow.lootboxes.lootdata;

import com.google.common.base.Enums;
import org.broken.arrow.yaml.library.YamlFileManager;
import org.brokenarrow.lootboxes.Lootboxes;
import org.brokenarrow.lootboxes.builder.ContainerData;
import org.brokenarrow.lootboxes.builder.ContainerDataBuilder;
import org.brokenarrow.lootboxes.builder.KeysData;
import org.brokenarrow.lootboxes.builder.LocationData;
import org.brokenarrow.lootboxes.builder.ParticleEffect;
import org.brokenarrow.lootboxes.untlity.ServerVersion;
import org.bukkit.Chunk;
import org.bukkit.ChunkSnapshot;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
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
import java.util.stream.Collectors;

import static org.brokenarrow.lootboxes.untlity.RunTimedTask.runtaskLater;
import static org.brokenarrow.lootboxes.untlity.errors.Valid.checkNotNull;

public class ContainerDataCache extends YamlFileManager {

	private static final ContainerDataCache instance = new ContainerDataCache();
	private final Map<String, ContainerDataBuilder> cacheContainerData = new HashMap<>();
	private final Map<Location, LocationData> chachedLocations = new HashMap<>();
	private final Map<String, List<Location>> chunkData = new HashMap<>();

	public ContainerDataCache() {
		super(Lootboxes.getInstance(), "container_data.db");
	}


	public Map<String, ContainerDataBuilder> getCacheContainerData() {
		return this.cacheContainerData;
	}

	public Map<Location, LocationData> getchachedLocations() {
		return this.chachedLocations;
	}

	public LocationData getLocationData(final Location location) {
		return this.chachedLocations.get(location);
	}

	public void putChachedLocations(final Location location, final LocationData locationData) {
		this.chachedLocations.put(location, locationData);
	}

	public void addChachedLocation(final String continerDataName, final Map<Location, ContainerData> map, final Map<String, KeysData> keysDataMap) {
		final Set<Location> linkedContainerData;
		final Map<String, KeysData> cacheKeysData;
		if (map.isEmpty()) {
			linkedContainerData = this.getLinkedContainers(continerDataName).keySet();
		} else
			linkedContainerData = map.keySet();
		if (keysDataMap.isEmpty()) {
			cacheKeysData = this.getCacheKeysData(continerDataName);
		} else
			cacheKeysData = keysDataMap;
		for (final Location location : linkedContainerData) {
			if (location != null) {
				putChachedLocations(location, new LocationData(continerDataName, cacheKeysData));
				setChunkData(location);
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
				.setDisplayname("")
				.setLore(new ArrayList<>())
				.setContainerData(new HashMap<>())
				.setKeysData(new HashMap<>())
				.setSpawnContainerFromWorldCenter(true)
				.build();

		cacheContainerData.put(container, builder);
		saveTask();
		addContainerToEffectList(builder);
		this.addContainerToSpawnTask(container, 1800);
	}

	public ContainerDataBuilder getCacheContainerData(final String container) {
		return this.getCacheContainerData().get(container);
	}

	@Nullable
	public ParticleEffect getParticleEffect(final String container, Object particle) {
		ContainerDataBuilder containerDataBuilder = this.getCacheContainerData(container);
		if (containerDataBuilder != null)
			return containerDataBuilder.getParticleEffect(particle);
		return null;
	}

	public List<Particle> getParticlesList(final String container) {
		ContainerDataBuilder containerDataBuilder = this.getCacheContainerData(container);
		if (containerDataBuilder != null) {

			Map<Object, ParticleEffect> list = containerDataBuilder.getParticleEffects();
			return list.values().stream().map(ParticleEffect::getParticle).collect(Collectors.toList());
		}
		return new ArrayList<>();
	}

	public List<ParticleEffect> getParticleEffectList(final String container) {
		ContainerDataBuilder containerDataBuilder = this.getCacheContainerData(container);
		if (containerDataBuilder != null) {

			Map<Object, ParticleEffect> list = containerDataBuilder.getParticleEffects();
			return new ArrayList<>(list.values());
		}
		return new ArrayList<>();
	}

	public boolean containsParticleEffect(@NotNull final String containerData, Object particle) {
		if (particle == null) return false;

		ContainerDataBuilder containerDataBuilder = this.getCacheContainerData(containerData);
		if (containerDataBuilder == null) return false;

		Map<Object, ParticleEffect> particleEffect = containerDataBuilder.getParticleEffects();
		if (particleEffect.isEmpty()) return false;

		return particleEffect.containsKey(particle);
	}

	public boolean containsParticleEffect(@NotNull final ContainerDataBuilder containerDataBuilder, Object particle) {
		if (particle == null) return false;

		Map<Object, ParticleEffect> particleEffect = containerDataBuilder.getParticleEffects();
		if (particleEffect.isEmpty()) return false;

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
		if (particleEffect.isEmpty()) return;

		particleEffect.remove(particle);
		addContainerToEffectList(containerDataBuilder);
	}

	public void setParticleEffects(@NotNull final String containerDataName, @NotNull Object particle, @NotNull final ParticleEffect.Builder particleBuilder) {
		ContainerDataBuilder containerDataBuilder = this.getCacheContainerData(containerDataName);
		final ContainerDataBuilder.Builder builder = containerDataBuilder.getBuilder();
		final Map<Object, ParticleEffect> particleEffect = containerDataBuilder.getParticleEffects();

		particleEffect.put(particle, particleBuilder.build());
		builder.setParticleEffects(particleEffect);
		ContainerDataBuilder containerData = builder.build();
		this.setContainerData(containerDataName, containerData);
		addContainerToEffectList(containerData);
	}

	/**
	 * Set list of locations contected to same chunk.
	 *
	 * @param location were the container is placed.
	 */
	public void setChunkData(final Location location) {

		final int x = location.getBlockX() >> 4;
		final int z = location.getBlockZ() >> 4;
		final List<Location> list = new ArrayList<>();
		final List<Location> locationList = this.chunkData.get(x + "=" + z);
		if (locationList != null) {
			if (!locationList.isEmpty())
				list.addAll(locationList);
		}
		if (!list.contains(location))
			list.add(location);
		this.chunkData.put(x + "=" + z, list);
	}

	/**
	 * Get list of locations where continers are located
	 * in the chunk.
	 *
	 * @param x the container is placed in.
	 * @return list of locations.
	 */
	public List<Location> getChunkData(final int x, final int z) {
		return this.chunkData.get(x + "=" + z);
	}

	/**
	 * Get list of locations where continers are located
	 * in the chunk.
	 *
	 * @param chunk the container is placed in.
	 * @return list of locations.
	 */
	public List<Location> getChunkData(final Object chunk) {
		Integer x = null;
		Integer z = null;
		if (chunk instanceof Chunk) {
			x = ((Chunk) chunk).getX();
			z = ((Chunk) chunk).getZ();
		}
		if (chunk instanceof ChunkSnapshot) {
			x = ((ChunkSnapshot) chunk).getX();
			z = ((ChunkSnapshot) chunk).getZ();
		}
		if (x == null || z == null)
			return null;
		return this.chunkData.get(x + "=" + z);
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

	public Map<String, KeysData> getCacheKeys(final String containerDataCacheName) {
		final ContainerDataBuilder containerDataBuilder = this.getCacheContainerData(containerDataCacheName);
		if (containerDataBuilder != null)
			return containerDataBuilder.getKeysData();

		return new HashMap<>();
	}

	public ContainerData getLinkedContainerData(final String containerDataCacheName, final Location location) {
		final ContainerDataBuilder containerDataBuilder = this.getCacheContainerData(containerDataCacheName);
		if (containerDataBuilder != null)
			return containerDataBuilder.getLinkedContainerData().get(location);

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
		checkNotNull(containerDataBuilder, "Some reason are ContainerDataBuilder for this containerData " + containerData + " null");
		final Map<String, org.brokenarrow.lootboxes.builder.KeysData> keysDataMap = containerDataBuilder.getKeysData();
		keysDataMap.put(keyName, keysData);
		final ContainerDataBuilder.Builder builder = containerDataBuilder.getBuilder();
		builder.setKeysData(keysDataMap);

		this.setContainerData(containerData, builder.build());
		addChachedLocation(containerData, new HashMap<>(), keysDataMap);
		if (containerDataBuilder.isSpawningContainerWithCooldown())
			addContainerToSpawnTask(containerData, containerDataBuilder.getCooldown());
		saveTask();
	}

	public boolean containsKeyName(final String containerData, final String keyName) {
		final ContainerDataBuilder builder = getCacheContainerData(containerData);

		return builder != null && builder.getKeysData().get(keyName) != null;

	}

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
		checkNotNull(builder, "Some reason are ContainerDataBuilder for this containerData " + container + " null");
		final ContainerDataBuilder containerDataBuilder = builder.setKeysData(keyDataMap).build();

		this.setContainerData(container, containerDataBuilder);
		this.addChachedLocation(container, new HashMap<>(), keyDataMap);
		if (containerDataBuilder.isSpawningContainerWithCooldown())
			addContainerToSpawnTask(container, containerDataBuilder.getCooldown());
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
		Map<Location, ContainerData> linkedcontainerData = containerDataBuilder.getLinkedContainerData();
		if (linkedcontainerData == null || linkedcontainerData.isEmpty()) return;

		for (Location location : linkedcontainerData.keySet())
			Lootboxes.getInstance().getSpawnContainerEffectsTask().addLocationInList(location);
	}

	public List<String> getContainerData() {
		return this.getCacheContainerData().keySet().stream().filter(Objects::nonNull).collect(Collectors.toList());
	}

	public void setContainerData(final String containerData, final ContainerDataBuilder containerDataBuilder) {

		cacheContainerData.put(containerData, containerDataBuilder);
		addChachedLocation(containerData, new HashMap<>(), new HashMap<>());
		if (!containerDataBuilder.isSpawningContainerWithCooldown())
			addContainerToSpawnTask(containerData, containerDataBuilder.getCooldown());
		final ContainerDataBuilder data = this.getCacheContainerData(containerData);
		if (data != null)
			for (final Location location : data.getLinkedContainerData().keySet()) {
				setChunkData(location);
				Lootboxes.getInstance().getSpawnContainerEffectsTask().addLocationInList(location);
			}

		saveTask();
	}

	public void saveTask() {
		runtaskLater(5, this::save, true);
	}

	@Override
	public void saveDataToFile(final File file) {
		//if (!isSingleFile() && !file.getName().equals(getFileName())) return;
		try {
			FileConfiguration configuration = new YamlConfiguration();
			//configuration.set("Data", null);
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
					containerDataBuilder = configuration.getObject("Data." + mainKey, ContainerDataBuilder.class);
				else
					containerDataBuilder = configuration.getSerializable("Data." + mainKey, ContainerDataBuilder.class);
				if (containerDataBuilder == null) continue;

				if (mainKey.contains(" "))
					mainKey = mainKey.trim().replace(" ", "_");

				addChachedLocation(mainKey, containerDataBuilder.getLinkedContainerData(), containerDataBuilder.getKeysData());

				cacheContainerData.put(mainKey, containerDataBuilder);
				if (containerDataBuilder.isSpawningContainerWithCooldown())
					addContainerToSpawnTask(mainKey, containerDataBuilder.getCooldown());
			}
	}

	public static Map<Object, ParticleEffect> convertToParticleEffect(List<?> objectList) {
		Map<Object, ParticleEffect> map = new HashMap<>();
		if (objectList == null || objectList.isEmpty()) return map;

		for (final Object particle : objectList) {
			final ParticleEffect.Builder builder = new ParticleEffect.Builder();
			if (particle instanceof Particle) {

				final Particle part = (Particle) particle;
				builder.setParticle(part).setDataType(part.getDataType());
			} else if (particle instanceof Effect) {

				final Effect part = (Effect) particle;
				builder.setEffect(part).setDataType(part.getData());
			}
			map.put(particle, builder.build());
		}

		return map;
	}

	public static ContainerDataCache getInstance() {
		return instance;
	}


}
