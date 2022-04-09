package org.brokenarrow.lootboxes.lootdata;

import com.google.common.base.Enums;
import org.brokenarrow.lootboxes.Lootboxes;
import org.brokenarrow.lootboxes.builder.ContainerDataBuilder;
import org.brokenarrow.lootboxes.settings.YamlUtil;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;

import java.util.*;
import java.util.stream.Collectors;

import static org.brokenarrow.lootboxes.untlity.DeSerialize.isLocation;
import static org.brokenarrow.lootboxes.untlity.RunTimedTask.runtaskLater;
import static org.brokenarrow.lootboxes.untlity.errors.Valid.checkNotNull;

public class ContainerData extends YamlUtil {

	//private final AllYamlFilesInFolder yamlFiles;
//	private File customConfigFile;
//	private FileConfiguration customConfig;
	private static final ContainerData instance = new ContainerData();
	private final Map<String, ContainerDataBuilder> cacheContainerData = new HashMap<>();
	private final Map<Location, ContainerDataBuilder.ContainerData> linkedContainerData = new HashMap<>();

	public ContainerData() {
		super("container_data.db", "container_data.db", "Data");
		//this.yamlFiles = new AllYamlFilesInFolder("container_data", true);
	}

	@Override
	public void reload() {
		super.reload();
	}

	public Map<String, ContainerDataBuilder> getCacheContainerData() {
		return cacheContainerData;
	}

	public Map<String, ContainerDataBuilder> getCacheLinkedContainerData() {
		for (String key : cacheContainerData.keySet()) {
			ContainerDataBuilder containerDataBuilder = cacheContainerData.get(key);
			if (containerDataBuilder == null) continue;

			for (Map.Entry<Location, ContainerDataBuilder.ContainerData> entry : containerDataBuilder.getLinkedContainerData().entrySet()) {
				linkedContainerData.put(entry.getKey(), entry.getValue());
			}
		}
		return null;
	}

	public void putCacheContainerData(String container, Material material) {

		ContainerDataBuilder.Builder builder = new ContainerDataBuilder.Builder();
		builder.setContainerDataLinkedToLootTable("").setSpawning(true).setCooldown(1800).setParticleEffect(new ArrayList<>())
				.setEnchant(false).setIcon(material).setDisplayname("").setLore(new ArrayList<>()).setContainerData(new HashMap<>())
				.setKeysData(new HashMap<>());

		cacheContainerData.put(container, builder.build());
		runtaskLater(5, this::save, true);
		System.out.println("cacheContainerData " + cacheContainerData);
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

	public ContainerDataBuilder.KeysData getCacheKeys(String container, String keyName) {
		ContainerDataBuilder containerDataBuilder = cacheContainerData.get(container);
		if (containerDataBuilder != null)
			return containerDataBuilder.getKeysData().get("Keys_" + keyName);

		return null;
	}

	public ContainerDataBuilder.KeysData removeCacheKey(String container, String keyName) {
		ContainerDataBuilder containerDataBuilder = cacheContainerData.get(container);
		if (containerDataBuilder != null) {
			runtaskLater(5, this::save, true);
			return containerDataBuilder.getKeysData().remove("Keys_" + keyName);
		}
		return null;
	}

	public Map<String, ContainerDataBuilder.KeysData> getCacheKeysData(String container) {
		return cacheContainerData.get(container).getKeysData();
	}

	public void setKeyData(String containerData, String keyName, ContainerDataBuilder.KeysData keysData) {

		ContainerDataBuilder containerDataBuilder = getCacheContainerData(containerData);
		checkNotNull(containerDataBuilder, "Some reason are ContainerDataBuilder for this containerData " + containerData + " null");
		Map<String, ContainerDataBuilder.KeysData> keysDataMap = containerDataBuilder.getKeysData();
		keysDataMap.put("Keys_" + keyName, keysData);
		ContainerDataBuilder.Builder builder = containerDataBuilder.getBuilder();
		builder.setKeysData(keysDataMap);

		this.cacheContainerData.put(containerData, builder.build());
		runtaskLater(5, this::save, true);
	}

	public boolean containsKeyName(String containerData, String keyName) {
		ContainerDataBuilder builder = getCacheContainerData(containerData);

		return builder != null && builder.getKeysData().get("Keys_" + keyName) != null;

	}

	public void setKeyData(KeysData keysData, Object objectToSave, String container, String keyName) {
		Map<String, ContainerDataBuilder.KeysData> keysDataMap = new HashMap<>();
		ContainerDataBuilder.KeysData keyData = getCacheKeys(container, keyName);
		if (keyData != null) {
			Material material = null;
			if (keysData == KeysData.ITEM_TYPE) {
				if (objectToSave instanceof String)
					material = Enums.getIfPresent(Material.class, (String) objectToSave).orNull();
				else
					material = (Material) objectToSave;
			}
			ContainerDataBuilder.KeysData data = new ContainerDataBuilder.KeysData(
					keysData == KeysData.KEY_NAME ? (String) objectToSave : keyData.getKeyName(),
					keysData == KeysData.DISPLAY_NAME ? (String) objectToSave : keyData.getDisplayName(),
					keysData == KeysData.LOOT_TABLE_LINKED ? (String) objectToSave : keyData.getDisplayName(),
					keysData == KeysData.AMOUNT_NEEDED ? (int) objectToSave : keyData.getAmountNeeded(),
					keysData == KeysData.ITEM_TYPE ? material : keyData.getItemType(),
					keysData == KeysData.LORE ? (List<String>) objectToSave : keyData.getLore());
			keysDataMap.put("Keys_" + keyName, data);

			ContainerDataBuilder.Builder builder = getCacheContainerBuilder(container);
			checkNotNull(builder, "Some reason are ContainerDataBuilder for this containerData " + container + " null");
			builder.setKeysData(keysDataMap);

			this.cacheContainerData.put(container, builder.build());
			runtaskLater(5, this::save, true);
		}
	}

	public List<String> getListOfKeys(String container) {
		List<String> keyNameList = new ArrayList<>();

		for (String keyData : cacheContainerData.get(container).getKeysData().keySet())
			if (keyData != null) {
				System.out.println("keyData " + keyData);
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
				for (ContainerDataBuilder.KeysData keyData : data.getKeysData().values()) {
					serializeData.put(childrenKey + "." + "Keys" + "." + keyData.getKeyName() + "." + "Amount_Of_Keys", keyData.getAmountNeeded());
					serializeData.put(childrenKey + "." + "Keys" + "." + keyData.getKeyName() + "." + "Itemtype", keyData.getItemType().name());
					serializeData.put(childrenKey + "." + "Keys" + "." + keyData.getKeyName() + "." + "Display_name", keyData.getDisplayName());
					serializeData.put(childrenKey + "." + "Keys" + "." + keyData.getKeyName() + "." + "Lore", keyData.getLore());
				}
				serializeData.put(childrenKey + "." + "Enchant", data.isEnchant());
				serializeData.put(childrenKey + "." + "Icon", data.getIcon().name());
				serializeData.put(childrenKey + "." + "Display_name", data.getDisplayname());
				serializeData.put(childrenKey + "." + "Lore", data.getLore());
				for (Map.Entry<Location, ContainerDataBuilder.ContainerData> containerData : data.getLinkedContainerData().entrySet()) {
					String serializeLoc = serializeLoc(containerData.getKey(), false);
					serializeData.put(childrenKey + "." + "Containers" + "." + serializeLoc + "." + "Facing", containerData.getValue().getFacing().name());
					serializeData.put(childrenKey + "." + "Containers" + "." + serializeLoc + "." + "Container_Type", containerData.getValue().getContainerType().name());
				}
			}
		return serializeData;
	}

	public static String toStringFormatted(Map<?, ?> serialize) {
		final List<String> lines = new ArrayList<>();

		lines.add("{");

		for (final Map.Entry<?, ?> entry : serialize.entrySet()) {
			final Object value = entry.getValue();

			if (value != null && !value.toString().equals("[]") && !value.toString().equals("{}") && !value.toString().isEmpty() && !value.toString().equals("0.0") && !value.toString().equals("false")) {
				System.out.println("to GenericString " + value.getClass().toGenericString());
				System.out.println("to String " + value.getClass().toString());
				lines.add("\t'" + entry.getKey() + "' = '" + value + "'");
			}
		}

		lines.add("}");

		return String.join("\n", lines);
	}

	@Override
	protected void loadSettingsFromYaml() {
		Map<Location, ContainerDataBuilder.ContainerData> containerDataMap = new HashMap<>();
		Map<String, ContainerDataBuilder.KeysData> keysDataMap = new HashMap<>();
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

				ConfigurationSection innerConfigKeys = customConfig.getConfigurationSection("Data." + mainKey + ".Keys");
				for (String innerKey : innerConfigKeys.getKeys(false)) {
					int keys = this.customConfig.getInt("Data." + mainKey + "." + "Keys" + "." + innerKey + ".Amount_Of_Keys");
					String itemType = this.customConfig.getString("Data." + mainKey + "." + "Keys" + "." + innerKey + ".Itemtype");
					String displayName = this.customConfig.getString("Data." + mainKey + "." + "Keys" + "." + innerKey + ".Display_name");
					List<String> keyLore = this.customConfig.getStringList("Data." + mainKey + "." + "Keys" + "." + innerKey + ".Lore");
					keysDataMap.put("Keys_" + innerKey, new ContainerDataBuilder.KeysData(innerKey, displayName, lootTableLinked, keys, itemType, keyLore));
				}

				ConfigurationSection containersKeys = customConfig.getConfigurationSection("Data." + mainKey + ".Containers");
				for (String innerKey : containersKeys.getKeys(false)) {
					String facing = this.customConfig.getString("Data." + mainKey + "." + "Containers" + "." + innerKey + "." + "Facing");
					String containerType = this.customConfig.getString("Data." + mainKey + "." + "Containers" + "." + innerKey + "." + "Container_Type");
					checkNotNull(isLocation(innerKey), "location " + innerKey + " are not valid or null");
					containerDataMap.put(isLocation(innerKey), new ContainerDataBuilder.ContainerData(facing, containerType));
				}
				ContainerDataBuilder.Builder builder = new ContainerDataBuilder.Builder();
				builder.setContainerDataLinkedToLootTable(lootTableLinked).setSpawning(spawning).setCooldown(cooldown).setParticleEffect(animation)
						.setEnchant(enchant).setIcon(icon).setDisplayname(display_name).setLore(lore).setContainerData(containerDataMap)
						.setKeysData(keysDataMap);
				cacheContainerData.put(mainKey, builder.build());
				Lootboxes.getInstance().getSpawnedContainers().setCachedTimeMap(mainKey, cooldown);
			}

	}

	public static ContainerData getInstance() {
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
