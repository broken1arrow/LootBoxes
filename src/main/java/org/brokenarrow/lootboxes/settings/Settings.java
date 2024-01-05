package org.brokenarrow.lootboxes.settings;

import org.broken.arrow.yaml.library.YamlFileManager;
import org.broken.arrow.yaml.library.utillity.ConfigurationWrapper;
import org.brokenarrow.lootboxes.Lootboxes;
import org.brokenarrow.lootboxes.builder.SettingsData;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.List;

public class Settings extends YamlFileManager {

	public Settings() {
		super(Lootboxes.getInstance(), "Settings.yml", true, true);
		setVersion(1);
	}

	private SettingsData settings;


	@Override
	protected void saveDataToFile(@NotNull final File file, @NotNull final ConfigurationWrapper configurationWrapper) {

	}

	public SettingsData getSettingsData() {
		return settings;
	}

	@Override
	protected void loadSettingsFromYaml(final File file, FileConfiguration configuration) {
		update();
		int blocksAwayFromPlayer = configuration.getInt("Blocks_Away_From_Player");
		int blocksBetweenContainers = configuration.getInt("Blocks_Between_Containers");
		int amountOfBlocksBelowSurface = configuration.getInt("Amount_Of_Blocks_Below_Surface");
		boolean spawnOnSurface = configuration.getBoolean("Spawn_On_Surface");
		boolean warnBeforeSaveWithMetadata = configuration.getBoolean("Warn_Before_Save_With_Metadata");
		boolean randomContainerSpawn = configuration.getBoolean("Random_Continer_Spawn");
		boolean saveMetadataOnItem = configuration.getBoolean("Save_Metadata_On_Item");
		boolean removeContainerWhenPlayerClose = configuration.getBoolean("Remove_Container_When_player_close");
		boolean debug = configuration.getBoolean("Debug");
		boolean singleMenu = configuration.getBoolean("Single_menu_file");

		int increase = configuration.getInt("Max_and_min_amount.Increse_with");
		int decrease = configuration.getInt("Max_and_min_amount.Decrese_with");
		String language = configuration.getString("Language");

		String linkTool = configuration.getString("Link_tool.Item");
		String linkToolDisplayName = configuration.getString("Link_tool.Display_name");
		List<String> linkToolLore = configuration.getStringList("Link_tool.Lore");

		String placeContainerDisplayName = configuration.getString("Place_container.Display_name");
		List<String> placeContainerLore = configuration.getStringList("Place_container.Lore");

		settings = new SettingsData.Builder()
				.setAmountOfBlocksBelowSurface(amountOfBlocksBelowSurface)
				.setBlocksAwayFromPlayer(blocksAwayFromPlayer)
				.setBlocksBetweenContainers(blocksBetweenContainers)
				.setWarnBeforeSaveWithMetadata(warnBeforeSaveWithMetadata)
				.setSaveMetadataOnItem(saveMetadataOnItem)
				.setRemoveContainerWhenPlayerClose(removeContainerWhenPlayerClose)
				.setSpawnOnSurface(spawnOnSurface)
				.setRandomContainerSpawn(randomContainerSpawn)
				.setIncrease(increase)
				.setDecrease(decrease)
				.setLanguage(language)
				.setLinkToolItem(linkTool)
				.setLinkToolDisplayName(linkToolDisplayName)
				.setLinkToolLore(linkToolLore)
				.setPlaceContainerDisplayName(placeContainerDisplayName)
				.setPlaceContainerLore(placeContainerLore)
				.setDebug(debug)
				.setSingleMenuFile(singleMenu)
				.build();
	}
}
