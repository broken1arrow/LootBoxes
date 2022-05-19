package org.brokenarrow.lootboxes.lootdata;

import com.google.common.base.Enums;
import org.brokenarrow.lootboxes.Lootboxes;
import org.brokenarrow.lootboxes.builder.ContainerData;
import org.brokenarrow.lootboxes.builder.ContainerDataBuilder;
import org.brokenarrow.lootboxes.builder.KeysData;
import org.brokenarrow.lootboxes.builder.LocationData;
import org.brokenarrow.lootboxes.settings.YamlUtil;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;

import java.util.*;
import java.util.stream.Collectors;

import static org.brokenarrow.lootboxes.untlity.DeSerialize.isLocation;
import static org.brokenarrow.lootboxes.untlity.RunTimedTask.runtaskLater;
import static org.brokenarrow.lootboxes.untlity.errors.Valid.checkNotNull;

public class ContainerDataCache extends YamlUtil {

	//private final AllYamlFilesInFolder yamlFiles;
//	private File customConfigFile;
//	private FileConfiguration customConfig;
	private static final ContainerDataCache instance = new ContainerDataCache();
	private final Map<String, ContainerDataBuilder> cacheContainerData = new HashMap<>();
	private final Map<Location, LocationData> chachedLocations = new HashMap<>();

	public ContainerDataCache() {
		super("container_data.db", "container_data.db", "Data");
		//this.yamlFiles = new AllYamlFilesInFolder("container_data", true);
	}

	@Override
	public void reload() {
		super.reload();
	}

	public Map<String, ContainerDataBuilder> getCacheContainerData() {
		return this.cacheContainerData;
	}

	public Map<Location, LocationData> getchachedLocations() {
		return this.chachedLocations;
	}

	public LocationData getLocationData(Location location) {
		return this.chachedLocations.get(location);
	}

	public void putChachedLocations(Location location, LocationData locationData) {
		this.chachedLocations.put(location, locationData);
	}

	public void addChachedLocation(String continerDataName, Map<Location, ContainerData> map, Map<String, KeysData> keysDataMap) {
		Set<Location> linkedContainerData;
		Map<String, KeysData> cacheKeysData;
		this.chachedLocations.clear();
		if (map.isEmpty()) {
			linkedContainerData = this.getLinkedContainers(continerDataName).keySet();
		} else
			linkedContainerData = map.keySet();
		if (keysDataMap.isEmpty()) {
			cacheKeysData = this.getCacheKeysData(continerDataName);
		} else
			cacheKeysData = keysDataMap;
		for (Location location : linkedContainerData)
			putChachedLocations(location, new LocationData(continerDataName, cacheKeysData));
	}

	public void setCacheContainerDataCache(String container, Material material) {

		ContainerDataBuilder.Builder builder = new ContainerDataBuilder.Builder();
		builder.setContainerDataLinkedToLootTable("").setSpawning(true).setCooldown(1800).setParticleEffect(new ArrayList<>())
				.setEnchant(false).setIcon(material).setDisplayname("").setLore(new ArrayList<>()).setContainerData(new HashMap<>())
				.setKeysData(new HashMap<>());

		cacheContainerData.put(container, builder.build());
		saveTask();
	}

	public ContainerDataBuilder getCacheContainerData(String container) {
		return cacheContainerData.get(container);
	}

	public ContainerDataBuilder.Builder getCacheContainerBuilder(String container) {
		ContainerDataBuilder containerDataBuilder = cacheContainerData.get(container);
		if (containerDataBuilder != null)
			return containerDataBuilder.getBuilder();

		return null;
	}

	public KeysData getCacheKey(String container, String keyName) {
		ContainerDataBuilder containerDataBuilder = cacheContainerData.get(container);
		if (containerDataBuilder != null)
			return containerDataBuilder.getKeysData().get("Keys_" + keyName);

		return null;
	}

	public Map<String, KeysData> getCacheKeys(String containerDataCacheName) {
		ContainerDataBuilder containerDataBuilder = cacheContainerData.get(containerDataCacheName);
		if (containerDataBuilder != null)
			return containerDataBuilder.getKeysData();

		return new HashMap<>();
	}

	public ContainerData getLinkedContainerData(String containerDataCacheName, Location location) {
		ContainerDataBuilder containerDataBuilder = cacheContainerData.get(containerDataCacheName);
		if (containerDataBuilder != null)
			return containerDataBuilder.getLinkedContainerData().get(location);

		return null;
	}

	public Map<Location, ContainerData> getLinkedContainers(String containerDataCacheName) {
		ContainerDataBuilder containerDataBuilder = cacheContainerData.get(containerDataCacheName);
		if (containerDataBuilder != null)
			return containerDataBuilder.getLinkedContainerData();

		return new HashMap<>();
	}

	public void removeCacheContainerData(String container) {
		cacheContainerData.remove(container);
	}

	public KeysData removeCacheKey(String container, String keyName) {
		ContainerDataBuilder containerDataBuilder = cacheContainerData.get(container);
		if (containerDataBuilder != null) {
			saveTask();
			return containerDataBuilder.getKeysData().remove("Keys_" + keyName);
		}
		return null;
	}

	public Map<String, KeysData> getCacheKeysData(String container) {
		return cacheContainerData.get(container).getKeysData();
	}

	public void setKeyData(String containerData, String keyName, KeysData keysData) {

		ContainerDataBuilder containerDataBuilder = getCacheContainerData(containerData);
		checkNotNull(containerDataBuilder, "Some reason are ContainerDataBuilder for this containerData " + containerData + " null");
		Map<String, org.brokenarrow.lootboxes.builder.KeysData> keysDataMap = containerDataBuilder.getKeysData();
		keysDataMap.put("Keys_" + keyName, keysData);
		ContainerDataBuilder.Builder builder = containerDataBuilder.getBuilder();
		builder.setKeysData(keysDataMap);

		this.cacheContainerData.put(containerData, builder.build());
		addChachedLocation(containerData, new HashMap<>(), keysDataMap);
		saveTask();
	}

	public boolean containsKeyName(String containerData, String keyName) {
		ContainerDataBuilder builder = getCacheContainerData(containerData);

		return builder != null && builder.getKeysData().get("Keys_" + keyName) != null;

	}

	public void setKeyData(KeysToSave keysToSave, Object objectToSave, String container, String keyName) {
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
		KeysData oldData = keyDataMap.get("Keys_" + keyName);
		KeysData data = new org.brokenarrow.lootboxes.builder.KeysData(
				keysToSave == KeysToSave.KEY_NAME ? (String) objectToSave : oldData.getKeyName(),
				keysToSave == KeysToSave.DISPLAY_NAME ? (String) objectToSave : oldData.getDisplayName(),
				keysToSave == KeysToSave.LOOT_TABLE_LINKED ? (String) objectToSave : oldData.getLootTableLinked(),
				keysToSave == KeysToSave.AMOUNT_NEEDED ? (int) objectToSave : oldData.getAmountNeeded(),
				keysToSave == KeysToSave.ITEM_TYPE ? material : oldData.getItemType(),
				keysToSave == KeysToSave.LORE ? (List<String>) objectToSave : oldData.getLore());


		keyDataMap.put("Keys_" + keyName, data);

		ContainerDataBuilder.Builder builder = getCacheContainerBuilder(container);
		checkNotNull(builder, "Some reason are ContainerDataBuilder for this containerData " + container + " null");
		builder.setKeysData(keyDataMap);

		this.cacheContainerData.put(container, builder.build());
		addChachedLocation(container, new HashMap<>(), keyDataMap);
		saveTask();

	}

	public List<String> getListOfKeys(String container) {
		List<String> keyNameList = new ArrayList<>();

		for (String keyData : cacheContainerData.get(container).getKeysData().keySet())
			if (keyData != null) {
				keyNameList.add(cacheContainerData.get(container).getKeysData().get(keyData).getKeyName());
			}
		return keyNameList;
	}

	public boolean containsContainerData(String key) {
		return cacheContainerData.containsKey(key);
	}


	public List<String> getContainerData() {
		return cacheContainerData.keySet().stream().filter(Objects::nonNull).collect(Collectors.toList());
	}

	public void setContainerData(String containers, ContainerDataBuilder containerDataBuilder) {
		//ContainerDataBuilder lootDataMap = cacheContainerData.get(lootTable);

		cacheContainerData.put(containers, containerDataBuilder);

		addChachedLocation(containers, new HashMap<>(), new HashMap<>());
		saveTask();
	}

	public void saveTask() {
		runtaskLater(5, this::save, true);
	}

	@Override
	protected void save() {
		/*customConfig.set("Data", null);
		for (Map.Entry<?, ?> childrenKey : serialize().entrySet())
			if (childrenKey != null) {

				customConfig.set((String) childrenKey.getKey(), childrenKey.getValue());
			}*/
		super.save();
		System.out.println("saveToString() \n" + customConfig.saveToString());
	}

	@Override
	public Map<?, ?> serialize() {
		Map<String, Object> serializeData = new LinkedHashMap<>();
		for (String childrenKey : this.cacheContainerData.keySet())
			if (childrenKey != null) {
				ContainerDataBuilder data = this.cacheContainerData.get(childrenKey);
				serializeData.put(childrenKey + "." + "LootTable_Linked", data.getLootTableLinked());
				serializeData.put(childrenKey + "." + "Spawning", data.isSpawning());
				serializeData.put(childrenKey + "." + "Cooldown", data.getCooldown());
				serializeData.put(childrenKey + "." + "Animation", data.getParticleEffects());
				serializeData.put(childrenKey + "." + "Random_spawn", data.isRandomSpawn());
				for (org.brokenarrow.lootboxes.builder.KeysData keyData : data.getKeysData().values()) {
					serializeData.put(childrenKey + "." + "Keys" + "." + keyData.getKeyName() + "." + "Amount_Of_Keys", keyData.getAmountNeeded());
					serializeData.put(childrenKey + "." + "Keys" + "." + keyData.getKeyName() + "." + "Itemtype", keyData.getItemType().name());
					serializeData.put(childrenKey + "." + "Keys" + "." + keyData.getKeyName() + "." + "Display_name", keyData.getDisplayName());
					serializeData.put(childrenKey + "." + "Keys" + "." + keyData.getKeyName() + "." + "Lore", keyData.getLore());
				}
				serializeData.put(childrenKey + "." + "Enchant", data.isEnchant());
				serializeData.put(childrenKey + "." + "Icon", data.getIcon().name());
				serializeData.put(childrenKey + "." + "Display_name", data.getDisplayname());
				serializeData.put(childrenKey + "." + "Lore", data.getLore());
				for (Map.Entry<Location, org.brokenarrow.lootboxes.builder.ContainerData> containerData : data.getLinkedContainerData().entrySet()) {
					String serializeLoc = serializeLoc(containerData.getKey(), false);
					serializeData.put(childrenKey + "." + "Containers" + "." + serializeLoc + "." + "Facing", containerData.getValue().getFacing().name());
					serializeData.put(childrenKey + "." + "Containers" + "." + serializeLoc + "." + "Container_Type", containerData.getValue().getContainerType().name());
				}
			}
		return serializeData;
	}


	@Override
	protected void loadSettingsFromYaml() {
		Map<Location, ContainerData> containerDataMap = new HashMap<>();
		Map<String, KeysData> keysDataMap = new HashMap<>();
		ConfigurationSection MainConfigKeys = customConfig.getConfigurationSection("Data");
		if (MainConfigKeys != null)
			for (String mainKey : MainConfigKeys.getKeys(false)) {
				if (mainKey == null) continue;

				String lootTableLinked = this.customConfig.getString("Data." + mainKey + "." + "LootTable_Linked");
				boolean spawning = this.customConfig.getBoolean("Data." + mainKey + "." + "Spawning");
				long cooldown = this.customConfig.getLong("Data." + mainKey + "." + "Cooldown");
				List<String> animation = this.customConfig.getStringList("Data." + mainKey + "." + "Animation");
				boolean enchant = this.customConfig.getBoolean("Data." + mainKey + "." + "Enchant");
				Material icon = Enums.getIfPresent(Material.class, this.customConfig.getString("Data." + mainKey + "." + "Icon", "AIR")).orNull();
				String display_name = this.customConfig.getString("Data." + mainKey + "." + "Display_name");
				List<String> lore = this.customConfig.getStringList("Data." + mainKey + "." + "Lore");
				boolean randomSpawn = this.customConfig.getBoolean("Data." + mainKey + "." + "Random_spawn");

				ConfigurationSection innerConfigKeys = customConfig.getConfigurationSection("Data." + mainKey + ".Keys");
				for (String innerKey : innerConfigKeys.getKeys(false)) {
					int keys = this.customConfig.getInt("Data." + mainKey + "." + "Keys" + "." + innerKey + ".Amount_Of_Keys");
					String itemType = this.customConfig.getString("Data." + mainKey + "." + "Keys" + "." + innerKey + ".Itemtype");
					String displayName = this.customConfig.getString("Data." + mainKey + "." + "Keys" + "." + innerKey + ".Display_name");
					List<String> keyLore = this.customConfig.getStringList("Data." + mainKey + "." + "Keys" + "." + innerKey + ".Lore");
					keysDataMap.put("Keys_" + innerKey, new KeysData(innerKey, displayName, lootTableLinked, keys, itemType, keyLore));
				}

				ConfigurationSection containersKeys = customConfig.getConfigurationSection("Data." + mainKey + ".Containers");
				for (String innerKey : containersKeys.getKeys(false)) {
					String facing = this.customConfig.getString("Data." + mainKey + "." + "Containers" + "." + innerKey + "." + "Facing");
					String containerType = this.customConfig.getString("Data." + mainKey + "." + "Containers" + "." + innerKey + "." + "Container_Type");
					if (isLocation(innerKey) == null)
						System.out.println("location " + innerKey + " are not valid or null");
					containerDataMap.put(isLocation(innerKey), new ContainerData(facing, containerType));
				}
				addChachedLocation(mainKey, containerDataMap, keysDataMap);
				ContainerDataBuilder.Builder builder = new ContainerDataBuilder.Builder();
				builder.setContainerDataLinkedToLootTable(lootTableLinked)
						.setSpawning(spawning)
						.setCooldown(cooldown)
						.setParticleEffect(animation)
						.setEnchant(enchant)
						.setIcon(icon)
						.setDisplayname(display_name)
						.setLore(lore)
						.setRandomSpawn(randomSpawn)
						.setContainerData(containerDataMap)
						.setKeysData(keysDataMap);
				cacheContainerData.put(mainKey, builder.build());
				Lootboxes.getInstance().getSpawnedContainers().setCachedTimeMap(mainKey, cooldown);
			}
	}

	public static ContainerDataCache getInstance() {
		return instance;
	}


	/*
	private void getFilesData() {
		try {
			for (File key : yamlFiles.getYamlFiles("container_data", "db")) {

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

	}*/
}
