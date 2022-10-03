package org.brokenarrow.lootboxes.settings;

import org.brokenarrow.lootboxes.builder.SettingsData;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Settings extends YamlUtil {

	public Settings() {
		super("Settings.yml", "Settings.yml");
	}

	private final Map<String, SettingsData> settings = new HashMap<>();

	@Override
	public void reload() {
		super.reload();
	}

	public SettingsData getSettingsData() {
		return settings.get("Settings");
	}

	@Override
	protected void loadSettingsFromYaml() {

		int blocksAwayFromPlayer = customConfig.getInt("Blocks_Away_From_Player");
		int blocksBetweenContainers = customConfig.getInt("Blocks_Between_Containers");
		int amountOfBlocksBelowSurface = customConfig.getInt("Amount_Of_Blocks_Below_Surface");
		boolean spawnOnSurface = customConfig.getBoolean("Spawn_On_Surface");
		boolean warnBeforeSaveWithMetadata = customConfig.getBoolean("Warn_Before_Save_With_Metadata");
		boolean randomContinerSpawn = customConfig.getBoolean("Random_Continer_Spawn");
		boolean saveMetadataOnItem = customConfig.getBoolean("Save_Metadata_On_Item");
		boolean removeContainerWhenPlayerClose = customConfig.getBoolean("Remove_Container_When_player_close");
		boolean debug = customConfig.getBoolean("Debug");
		int increse = customConfig.getInt("Max_and_min_amount.Increse_with");
		int decrese = customConfig.getInt("Max_and_min_amount.Decrese_with");
		String language = customConfig.getString("Language");

		String linkTool = customConfig.getString("Link_tool.Item");
		String linkToolDisplayName = customConfig.getString("Link_tool.Display_name");
		List<String> linkToolLore = customConfig.getStringList("Link_tool.Lore");

		String placeContainerDisplayName = customConfig.getString("Place_container.Display_name");
		List<String> placeContainerLore = customConfig.getStringList("Place_container.Lore");
		
		SettingsData settingsData = new SettingsData.Builder()
				.setAmountOfBlocksBelowSurface(amountOfBlocksBelowSurface)
				.setBlocksAwayFromPlayer(blocksAwayFromPlayer)
				.setBlocksBetweenContainers(blocksBetweenContainers)
				.setWarnBeforeSaveWithMetadata(warnBeforeSaveWithMetadata)
				.setSaveMetadataOnItem(saveMetadataOnItem)
				.setRemoveContainerWhenPlayerClose(removeContainerWhenPlayerClose)
				.setSpawnOnSurface(spawnOnSurface)
				.setRandomContinerSpawn(randomContinerSpawn)
				.setIncrese(increse)
				.setDecrese(decrese)
				.setLanguage(language)
				.setLinkToolItem(linkTool)
				.setLinkToolDisplayName(linkToolDisplayName)
				.setLinkToolLore(linkToolLore)
				.setPlaceContainerDisplayName(placeContainerDisplayName)
				.setPlaceContainerLore(placeContainerLore)
				.setDebug(debug)
				.build();

		settings.put("Settings", settingsData);
	}
}
