package org.brokenarrow.lootboxes.lootdata;

import com.google.common.base.Enums;
import org.brokenarrow.lootboxes.Lootboxes;
import org.brokenarrow.lootboxes.builder.*;
import org.brokenarrow.lootboxes.settings.SimpleYamlHelper;
import org.bukkit.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

import static org.brokenarrow.lootboxes.untlity.ConvetParticlesUntlity.convertStringList;
import static org.brokenarrow.lootboxes.untlity.RunTimedTask.runtaskLater;
import static org.brokenarrow.lootboxes.untlity.SerializeUtlity.isLocation;
import static org.brokenarrow.lootboxes.untlity.errors.Valid.checkNotNull;

public class ContainerDataCache extends SimpleYamlHelper {

	//private final SimpleYamlHelper yamlFiles;
//	private File customConfigFile;
//	private FileConfiguration customConfig;
	private static final ContainerDataCache instance = new ContainerDataCache();
	private final Map<String, ContainerDataBuilder> cacheContainerData = new HashMap<>();
	private final Map<Location, LocationData> chachedLocations = new HashMap<>();
	private final Map<String, List<Location>> chunkData = new HashMap<>();

	public ContainerDataCache() {
		super("container_data.db", "Data", true, true);
		//this.yamlFiles = new SimpleYamlHelper("container_data", true);
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

	public void setCacheContainerDataCache(String container, final Material material) {
		if (container.contains(" "))
			container = container.trim().replace(" ", "_");
		final ContainerDataBuilder.Builder builder = new ContainerDataBuilder.Builder();
		builder.setContainerDataLinkedToLootTable("").setSpawningContainerWithCooldown(true).setCooldown(1800).setParticleEffects(new ArrayList<>())
				.setEnchant(false).setIcon(material).setDisplayname("").setLore(new ArrayList<>()).setContainerData(new HashMap<>())
				.setKeysData(new HashMap<>());

		cacheContainerData.put(container, builder.build());
		saveTask();
		addContainerToSpawnTask(container, 1800);
	}

	public ContainerDataBuilder getCacheContainerData(final String container) {
		return cacheContainerData.get(container);
	}

	@Nullable
	public ParticleEffect getParticleEffect(final String container, Object particle) {
		ContainerDataBuilder containerDataBuilder = cacheContainerData.get(container);
		if (containerDataBuilder != null)
			return containerDataBuilder.getParticleEffect(particle);
		return null;
	}

	public List<Particle> getParticlesList(final String container) {
		ContainerDataBuilder containerDataBuilder = cacheContainerData.get(container);
		if (containerDataBuilder != null) {
			List<ParticleEffect> list = containerDataBuilder.getParticles();
			if (list != null)
				return list.stream().map(ParticleEffect::getParticle).collect(Collectors.toList());
		}
		return new ArrayList<>();
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
	public List<Location> getChunkData(final Chunk chunk) {
		final int x = chunk.getX();
		final int z = chunk.getZ();
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
		final ContainerDataBuilder containerDataBuilder = cacheContainerData.get(container);
		if (containerDataBuilder != null)
			return containerDataBuilder.getBuilder();

		return null;
	}

	public KeysData getCacheKey(final String container, final String keyName) {
		final ContainerDataBuilder containerDataBuilder = cacheContainerData.get(container);
		if (containerDataBuilder != null) {
			if (keyName.startsWith("Keys_"))
				return containerDataBuilder.getKeysData().get(keyName);
			return containerDataBuilder.getKeysData().get(keyName);
		}

		return null;
	}

	public Map<String, KeysData> getCacheKeys(final String containerDataCacheName) {
		final ContainerDataBuilder containerDataBuilder = cacheContainerData.get(containerDataCacheName);
		if (containerDataBuilder != null)
			return containerDataBuilder.getKeysData();

		return new HashMap<>();
	}

	public ContainerData getLinkedContainerData(final String containerDataCacheName, final Location location) {
		final ContainerDataBuilder containerDataBuilder = cacheContainerData.get(containerDataCacheName);
		if (containerDataBuilder != null)
			return containerDataBuilder.getLinkedContainerData().get(location);

		return null;
	}

	public Map<Location, ContainerData> getLinkedContainers(final String containerDataCacheName) {
		final ContainerDataBuilder containerDataBuilder = cacheContainerData.get(containerDataCacheName);
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
		final ContainerDataBuilder containerDataBuilder = cacheContainerData.get(containerDataCacheName);
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

		this.cacheContainerData.put(containerData, builder.build());
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

		this.cacheContainerData.put(container, containerDataBuilder);
		addChachedLocation(container, new HashMap<>(), keyDataMap);
		if (containerDataBuilder.isSpawningContainerWithCooldown())
			addContainerToSpawnTask(container, containerDataBuilder.getCooldown());
		saveTask();

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
		return cacheContainerData.containsKey(key);
	}

	public void addContainerToSpawnTask(final String mainKey, final long cooldown) {
		Lootboxes.getInstance().getSpawnedContainers().setCachedTimeMap(mainKey, cooldown);
	}

	public List<String> getContainerData() {
		return cacheContainerData.keySet().stream().filter(Objects::nonNull).collect(Collectors.toList());
	}

	public void setContainerData(final String containerData, final ContainerDataBuilder containerDataBuilder) {
		//ContainerDataBuilder lootDataMap = cacheContainerData.get(lootTable);
		cacheContainerData.put(containerData, containerDataBuilder);
		System.out.println("setContainerData " + containerDataBuilder);
		addChachedLocation(containerData, new HashMap<>(), new HashMap<>());
		if (!containerDataBuilder.isSpawningContainerWithCooldown())
			addContainerToSpawnTask(containerData, containerDataBuilder.getCooldown());
		final ContainerDataBuilder data = this.getCacheContainerData(containerData);
		if (data != null)
			for (final Location location : data.getLinkedContainerData().keySet())
				setChunkData(location);
		saveTask();
	}

	public void saveTask() {
		runtaskLater(5, this::save, true);
	}

	@Override
	public void saveDataToFile(final File file) {

	}

	@Override
	public Map<?, ?> serialize() {
		if (this.cacheContainerData.isEmpty()) return null;

		final Map<String, Object> serializeData = new LinkedHashMap<>();
		for (final String childrenKey : this.cacheContainerData.keySet())
			if (childrenKey != null) {
				final ContainerDataBuilder data = this.cacheContainerData.get(childrenKey);
				serializeData.put(childrenKey, data);
			}
		return serializeData;
	}


	@Override
	protected void loadSettingsFromYaml(final File file) {
		final FileConfiguration customConfig = getCustomConfig();
		final ConfigurationSection MainConfigKeys = customConfig.getConfigurationSection("Data");
		if (MainConfigKeys != null)
			for (String mainKey : MainConfigKeys.getKeys(false)) {
				if (mainKey == null) continue;
				final ContainerDataBuilder containerDataBuilder = customConfig.getObject("Data." + mainKey, ContainerDataBuilder.class);
				final ContainerDataBuilder builder;
				System.out.println("containerDataBuilder " + containerDataBuilder);
				if (containerDataBuilder != null) {
					builder = containerDataBuilder;
				} else {
					final Map<Location, ContainerData> containerDataMap = new HashMap<>();
					final Map<String, KeysData> keysDataMap = new HashMap<>();
					final String lootTableLinked = customConfig.getString("Data." + mainKey + "." + "LootTable_Linked");
					final boolean spawningContainerWithCooldown = customConfig.getBoolean("Data." + mainKey + "." + "Spawning");
					final long cooldown = customConfig.getLong("Data." + mainKey + "." + "Cooldown");
					final List<String> animation = customConfig.getStringList("Data." + mainKey + "." + "Animation");
					final boolean enchant = customConfig.getBoolean("Data." + mainKey + "." + "Enchant");
					final Material icon = Enums.getIfPresent(Material.class, customConfig.getString("Data." + mainKey + "." + "Icon", "AIR")).orNull();
					final String display_name = customConfig.getString("Data." + mainKey + "." + "Display_name");
					final List<String> lore = customConfig.getStringList("Data." + mainKey + "." + "Lore");
					final boolean randomSpawn = customConfig.getBoolean("Data." + mainKey + "." + "Random_spawn");


					final ConfigurationSection innerConfigKeys = customConfig.getConfigurationSection("Data." + mainKey + ".Keys");
					if (innerConfigKeys == null) {
						System.out.println("Keys " + mainKey + " are not valid or null");
					} else
						for (String innerKey : innerConfigKeys.getKeys(false)) {

							final int keys = customConfig.getInt("Data." + mainKey + "." + "Keys" + "." + innerKey + ".Amount_Of_Keys");
							final String itemType = customConfig.getString("Data." + mainKey + "." + "Keys" + "." + innerKey + ".Itemtype");
							final String displayName = customConfig.getString("Data." + mainKey + "." + "Keys" + "." + innerKey + ".Display_name");
							final List<String> keyLore = customConfig.getStringList("Data." + mainKey + "." + "Keys" + "." + innerKey + ".Lore");

							if (innerKey.contains(" "))
								innerKey = innerKey.trim().replace(" ", "_");
							final KeysData keysData = new KeysData(innerKey, displayName, lootTableLinked, keys, itemType, keyLore);

							keysDataMap.put(innerKey, keysData);
						}

					final ConfigurationSection containersKeys = customConfig.getConfigurationSection("Data." + mainKey + ".Containers");
					if (containersKeys == null) {
						System.out.println("Containers " + mainKey + " are not valid or null");
					} else
						for (final String innerKey : containersKeys.getKeys(false)) {

							final String facing = customConfig.getString("Data." + mainKey + "." + "Containers" + "." + innerKey + "." + "Facing");
							final String containerType = customConfig.getString("Data." + mainKey + "." + "Containers" + "." + innerKey + "." + "Container_Type");
							final ContainerData containerData = new ContainerData(facing, containerType);

							if (isLocation(innerKey) == null)
								System.out.println("location " + innerKey + " are not valid or null");
							containerDataMap.put(isLocation(innerKey), containerData);
						}
					builder = new ContainerDataBuilder.Builder()
							.setContainerDataLinkedToLootTable(lootTableLinked)
							.setSpawningContainerWithCooldown(spawningContainerWithCooldown)
							.setCooldown(cooldown)
							.setParticleEffects(convertToParticleEffect(convertStringList(animation)))
							.setEnchant(enchant)
							.setIcon(icon)
							.setDisplayname(display_name)
							.setLore(lore)
							.setRandomSpawn(randomSpawn)
							.setContainerData(containerDataMap)
							.setKeysData(keysDataMap).build();
				}
				if (mainKey.contains(" "))
					mainKey = mainKey.trim().replace(" ", "_");
				addChachedLocation(mainKey, builder.getLinkedContainerData(), builder.getKeysData());


				cacheContainerData.put(mainKey, builder);
				if (builder.isSpawningContainerWithCooldown())
					addContainerToSpawnTask(mainKey, builder.getCooldown());

			}
	}

	public List<ParticleEffect> convertToParticleEffect(List<?> objectList) {
		List<ParticleEffect> list = new ArrayList<>();
		if (objectList == null || objectList.isEmpty()) return list;

		for (final Object particle : objectList) {
			final ParticleEffect.Builder builder = new ParticleEffect.Builder();
			if (particle instanceof Particle) {

				final Particle part = (Particle) particle;
				builder.setParticle(part).setDataType(part.getDataType());
			} else if (particle instanceof Effect) {

				final Effect part = (Effect) particle;
				builder.setEffect(part).setDataType(part.getData());
			}
			list.add(builder.build());
		}

		return list;
	}

	public static ContainerDataCache getInstance() {
		return instance;
	}


}
