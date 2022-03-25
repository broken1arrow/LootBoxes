package org.brokenarrow.lootboxes.settings;

import org.brokenarrow.lootboxes.Lootboxes;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public abstract class YamlUtil {

	protected File customConfigFile;

	protected FileConfiguration customConfig;
	protected String fileName, resourcePath;

	public YamlUtil(String fileName, String resourcePath) {
		this.fileName = fileName;
		this.resourcePath = resourcePath;
	}


	protected void reload() {
		if (customConfigFile == null) {
			customConfigFile = new File(Lootboxes.getInstance().getDataFolder(), fileName);
			customConfig = YamlConfiguration.loadConfiguration(customConfigFile);

		}
		if (!this.customConfigFile.exists() && resourcePath != null) {
			this.customConfigFile.getParentFile().mkdirs();
			Lootboxes.getInstance().saveResource(resourcePath, false);

		}

		try {
			this.customConfig = new YamlConfiguration();
			this.customConfig.load(this.customConfigFile);
			loadSettingsFromYaml();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	protected FileConfiguration load() {
		if (customConfig == null) reload();
		return customConfig;
	}

	protected void save() {
		/*if (customConfig == null || customConfigFile == null) {
			return;
		}
		try {
			load().save(customConfigFile);
		} catch (IOException ex) {
			ex.printStackTrace();
		}*/
	}

	protected abstract void loadSettingsFromYaml();

	public void setListQueue(String player, Object... listQueue) {
	}

}