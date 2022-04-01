package org.brokenarrow.lootboxes.lootdata;

import org.brokenarrow.lootboxes.builder.ContainerDataBuilder;
import org.brokenarrow.lootboxes.settings.YamlUtil;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;

import java.util.*;

import static org.brokenarrow.lootboxes.untlity.DeSerialize.isLocation;
import static org.brokenarrow.lootboxes.untlity.errors.Valid.checkNotNull;

public class ContainerData extends YamlUtil {

	//private final AllYamlFilesInFolder yamlFiles;
//	private File customConfigFile;
//	private FileConfiguration customConfig;
	private static final ContainerData instance = new ContainerData();
	private final Map<String, ContainerDataBuilder> cacheContainerData = new HashMap<>();

	public ContainerData() {
		super("container_data.db", "container_data.db", "Data");
		//this.yamlFiles = new AllYamlFilesInFolder("container_data", true);
	}

	@Override
	public void reload() {
		super.reload();
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
				System.out.println("serilazed " + serializeData(data.getLootTableLinked(), childrenKey, "LootTable_Linked"));
				serializeData.put(childrenKey + "." + "LootTable_Linked", data.getLootTableLinked());
				serializeData.put(childrenKey + "." + "Spawning", data.isSpawning());
				serializeData.put(childrenKey + "." + "Cooldown", data.getCooldown());
				serializeData.put(childrenKey + "." + "Animation", data.getEffect());
				for (ContainerDataBuilder.KeysData keyData : data.getKeysData().values())
					serializeData.put(childrenKey + "." + "Keys" + "." + keyData.getKeyName(), keyData.getAmountNeeded());
				serializeData.put(childrenKey + "." + "Enchant", data.isEnchant());
				serializeData.put(childrenKey + "." + "Icon", data.getIcon());
				serializeData.put(childrenKey + "." + "Display_name", data.getDisplayname());
				serializeData.put(childrenKey + "." + "Lore", data.getLore());
				for (Map.Entry<Location, ContainerDataBuilder.ContainerData> containerData : data.getContainerData().entrySet()) {
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
				String animation = this.customConfig.getString("Data." + mainKey + "." + "Animation");
				boolean enchant = this.customConfig.getBoolean("Data." + mainKey + "." + "Enchant");
				String icon = this.customConfig.getString("Data." + mainKey + "." + "Icon");
				String display_name = this.customConfig.getString("Data." + mainKey + "." + "Display_name");
				List<String> lore = this.customConfig.getStringList("Data." + mainKey + "." + "Lore");
				ConfigurationSection innerConfigKeys = customConfig.getConfigurationSection("Data." + mainKey + ".Keys");
				for (String innerKey : innerConfigKeys.getKeys(false)) {
					int keys = this.customConfig.getInt("Data." + mainKey + "." + "Keys" + "." + innerKey);
					keysDataMap.put("Keys_" + innerKey, new ContainerDataBuilder.KeysData(innerKey, keys));
				}
				ConfigurationSection containersKeys = customConfig.getConfigurationSection("Data." + mainKey + ".Containers");
				for (String innerKey : containersKeys.getKeys(false)) {
					String facing = this.customConfig.getString("Data." + mainKey + "." + "Containers" + "." + innerKey + "." + "Facing");
					String containerType = this.customConfig.getString("Data." + mainKey + "." + "Containers" + "." + innerKey + "." + "Container_Type");
					checkNotNull(isLocation(innerKey), "location " + innerKey + " are not valid or null");
					containerDataMap.put(isLocation(innerKey), new ContainerDataBuilder.ContainerData(facing, containerType));
				}
				ContainerDataBuilder.Builder builder = new ContainerDataBuilder.Builder();
				builder.setLootTableLinked(lootTableLinked).setSpawning(spawning).setCooldown(cooldown).setEffect(animation)
						.setEnchant(enchant).setIcon(icon).setDisplayname(display_name).setLore(lore).setContainerData(containerDataMap)
						.setKeysData(keysDataMap);
				cacheContainerData.put(mainKey, builder.build());
			}

		save();

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
