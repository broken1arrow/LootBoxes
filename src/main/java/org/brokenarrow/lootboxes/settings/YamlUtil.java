package org.brokenarrow.lootboxes.settings;

import org.brokenarrow.lootboxes.Lootboxes;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

public abstract class YamlUtil {


	protected File customConfigFile;
	private final String yamlMainpath;
	protected FileConfiguration customConfig;
	protected String fileName, resourcePath;

	public YamlUtil(String fileName, String resourcePath) {
		this(fileName, resourcePath, "");
	}

	public YamlUtil(String fileName, String resourcePath, String yamlMainpath) {
		this.fileName = fileName;
		this.resourcePath = resourcePath;
		this.yamlMainpath = yamlMainpath;

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
		if (customConfig == null || customConfigFile == null) {
			return;
		}
		try {
			customConfig.set(yamlMainpath, null);
			for (Map.Entry<?, ?> childrenKey : serialize().entrySet())
				if (childrenKey != null) {

					customConfig.set(yamlMainpath + "." + childrenKey.getKey(), childrenKey.getValue());
				}
			//load().save(customConfigFile);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public static String serializeLoc(final Location loc, boolean addPitch) {
		if (!addPitch)
			return loc.getWorld().getName() + " " + loc.getBlockX() + " " + loc.getBlockY() + " " + loc.getBlockZ();

		return loc.getWorld().getName() + " " + loc.getX() + " " + loc.getY() + " " + loc.getZ() + (loc.getPitch() != 0F || loc.getYaw() != 0F ? " " + loc.getYaw() + " " + loc.getPitch() : "");
	}

	public Map<?, ?> serialize() {
		return null;
	}

	public static Map<?, ?> serializeData(Object valueTosave, String... childrenPath) {
		Map<String, Object> serializeData = new LinkedHashMap<>();
		serializeData.put("Data." + (childrenPath != null && childrenPath.length > 0 ? Arrays.toString(childrenPath).replace(",", ".") : ""), valueTosave);

		return serializeData;
	}

	protected abstract void loadSettingsFromYaml();

	public void setListQueue(String player, Object... listQueue) {
	}

}