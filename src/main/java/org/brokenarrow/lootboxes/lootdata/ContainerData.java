package org.brokenarrow.lootboxes.lootdata;

import org.brokenarrow.lootboxes.settings.GuiTempletSettings;
import org.brokenarrow.lootboxes.settings.YamlUtil;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ContainerData extends YamlUtil {

	//private final AllYamlFilesInFolder yamlFiles;
//	private File customConfigFile;
//	private FileConfiguration customConfig;
	private static final ContainerData instance = new ContainerData();
	private final Map<String, Map<String, ItemStack>> cacheContainerData = new HashMap<>();

	public ContainerData() {
		super("container_data.db", "container_data.db");
		//this.yamlFiles = new AllYamlFilesInFolder("container_data", true);
	}

	@Override
	public void reload() {
		super.reload();
	}

	@Override
	protected void loadSettingsFromYaml() {
		Map<String, GuiTempletSettings.Guidata> yamlData = new HashMap<>();
		ConfigurationSection MainConfigKeys = customConfig.getConfigurationSection("Data");
		if (MainConfigKeys != null)
			for (String mainKey : MainConfigKeys.getKeys(false)) {
				if (mainKey == null) continue;

				String lootTableLinked = this.customConfig.getString("Data." + mainKey + "." + "LootTable_Linked");
				boolean Spawning = this.customConfig.getBoolean("Data." + mainKey + "." + "Spawning");
				long Cooldown = this.customConfig.getLong("Data." + mainKey + "." + "Cooldown");
				String animation = this.customConfig.getString("Data." + mainKey + "." + "Animation");
				boolean enchant = this.customConfig.getBoolean("Data." + mainKey + "." + "Enchant");
				String icon = this.customConfig.getString("Data." + mainKey + "." + "Icon");
				String display_name = this.customConfig.getString("Data." + mainKey + "." + "Display_name");
				List<String> lore = this.customConfig.getStringList("Data." + mainKey + "." + "Lore");
				ConfigurationSection innerConfigKeys = customConfig.getConfigurationSection("Data." + mainKey + ".Keys");
				for (String innerKey : innerConfigKeys.getKeys(false)) {
					String keys = this.customConfig.getString("Data." + mainKey + "." + "Keys" + "." + innerKey);
					System.out.println("InnerKey  " + innerKey);
					System.out.println("keys " + keys);
				}
				ConfigurationSection containersKeys = customConfig.getConfigurationSection("Data." + mainKey + ".Containers");
				for (String innerKey : containersKeys.getKeys(false)) {
					String facing = this.customConfig.getString("Data." + mainKey + "." + "Containers" + "." + innerKey + "." + "Facing");
					String containerType = this.customConfig.getString("Data." + mainKey + "." + "Containers" + "." + innerKey + "." + "Container_Type");
					System.out.println("InnerKey  " + innerKey);
					System.out.println("Facing " + facing);
					System.out.println("ContainerType " + containerType);
				}
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
