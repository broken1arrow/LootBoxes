package org.brokenarrow.lootboxes.settings;

import org.broken.arrow.yaml.library.YamlFileManager;
import org.brokenarrow.lootboxes.Lootboxes;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class GuiTempletSettings extends YamlFileManager {

	private static final GuiTempletSettings instance = new GuiTempletSettings();
	private final Map<String, Map<String, Guidata>> chacheGuiSettings = new HashMap<>();

	public GuiTempletSettings() {
		super(Lootboxes.getInstance(), "language/guitemplets_" + Lootboxes.getInstance().getSettings().getSettingsData().getLanguage() + ".yml");

	}

	@Override
	protected void loadSettingsFromYaml(final File file, FileConfiguration configuration) {
		Map<String, Guidata> yamlData = new HashMap<>();
		FileConfiguration customConfig = configuration;
		ConfigurationSection MainConfigKeys = customConfig.getConfigurationSection("Gui_Templets");

		if (MainConfigKeys != null) {
			for (String mainKey : MainConfigKeys.getKeys(false)) {
				ConfigurationSection innerConfigKeys = customConfig.getConfigurationSection("Gui_Templets." + mainKey);
				if (innerConfigKeys != null)
					for (String innerChildrenKeys : innerConfigKeys.getKeys(false)) {
						if (!innerChildrenKeys.equals("Menu_Size") && !innerChildrenKeys.equals("Menu_Title") && !innerChildrenKeys.equals("FillSpace")) {

							int menuGuiSize = customConfig.getInt("Gui_Templets." + mainKey + "." + "Menu_Size");
							String menuGuiTitle = customConfig.getString("Gui_Templets." + mainKey + "." + "Menu_Title");
							String fillSpace = customConfig.getString("Gui_Templets." + mainKey + "." + "FillSpace");
							int maxAmountOfItems = customConfig.getInt("Gui_Templets." + mainKey + "." + "Max_Amount_Of_Items");

							String displayname = customConfig.getString("Gui_Templets." + mainKey + "." + innerChildrenKeys + ".Display_name");
							String slot = customConfig.getString("Gui_Templets." + mainKey + "." + innerChildrenKeys + ".Slot");
							String icon = customConfig.getString("Gui_Templets." + mainKey + "." + innerChildrenKeys + ".Icon");
							boolean glow = customConfig.getBoolean("Gui_Templets." + mainKey + "." + innerChildrenKeys + ".Enchant");
							List<String> lore = customConfig.getStringList("Gui_Templets." + mainKey + "." + innerChildrenKeys + ".Lore");

							Guidata.Builder builder = new Guidata.Builder()
									.setDisplayname(displayname)
									.setGlow(glow)
									.setIcon(icon)
									.setLore(lore)
									.setSlot(slot);
							yamlData.put(mainKey + "_" + innerChildrenKeys, builder.build());
							builder = new Guidata.Builder()
									.setMenuFillSpace(fillSpace)
									.setMenuMaxAmountOfItems(maxAmountOfItems)
									.setMenuSize(menuGuiSize)
									.setMenuTitle(menuGuiTitle);
							yamlData.put(mainKey, builder.build());

						}
					}
				if (!yamlData.isEmpty()) {
					setDataYamlfile(mainKey, yamlData);
				}
			}
		}
		if (chacheGuiSettings.isEmpty()) return;
		convertMenu();
	}

	public void convertMenu() {
		File file = new File(plugin.getDataFolder() + "/language", "menus_" + Lootboxes.getInstance().getSettings().getSettingsData().getLanguage() + ".yml");
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		FileConfiguration config = YamlConfiguration.loadConfiguration(file);
		for (Entry<String, Map<String, Guidata>> entry : chacheGuiSettings.entrySet()) {
			String menuName = entry.getKey();
			Map<String, Guidata> buttons = entry.getValue();
			Guidata mainButton = buttons.get(menuName);
			String name = menuName;
			if (menuName.equals("Choose_Container")) {
				name = "Choose_container";
			}
			if (menuName.equals("Random_loot_container_menu")) {
				name = "Random_loot_container";
			}
			if (menuName.equals("Container_Linked_List")) {
				name = "Container_linked_list";
			}
			if (menuName.equals("CustomizeItem")) {
				name = "Customize_item";
			}
			if (menuName.equalsIgnoreCase("Enchantments")) {
				name = "Enchantments_list";
			}
			if (menuName.equalsIgnoreCase("EntityType_List_Menu")) {
				name = "EntityType_list";
			}
			if (menuName.equalsIgnoreCase("List_of_loottables")) {
				name = "List_of_loot_tables";
			}
			if (menuName.equalsIgnoreCase("Main_menu")) {
				name = "Main_menu";
			}
			if (menuName.equalsIgnoreCase("Matrial_List")) {
				name = "Material_list";
			}
			if (menuName.equalsIgnoreCase("Particle_Animantion")) {
				name = "Particle_animation";
			}
			if (menuName.equalsIgnoreCase("Particle_Settings")) {
				name = "Particle_settings";
			}
			if (menuName.equalsIgnoreCase("Alter_ContainerData_Menu")) {
				name = "Alter_container_data";
			}
			if (menuName.equalsIgnoreCase("Container_data")) {
				name = "Containers_list";
			}
			if (menuName.equalsIgnoreCase("Confirm_if_item_have_metadata")) {
				name = "Confirm_if_item_have_metadata";
			}
			if (menuName.equalsIgnoreCase("Edit_Items_For_LootTable")) {
				name = "Edit_items_for_loot_table";
			}
			if (menuName.equalsIgnoreCase("Save_items")) {
				name = "Save_items";
			}
			if (menuName.equalsIgnoreCase("Edit_key")) {
				name = "Edit_key";
			}
			if (menuName.equalsIgnoreCase("Edit_keys_to_open")) {
				name = "Edit_keys_to_open";
			}
			if (menuName.equalsIgnoreCase("Key_Settings_MobDrop")) {
				name = "Key_settings_mob_drop";
			}
			if (menuName.equalsIgnoreCase("Save_new_keys")) {
				name = "Save_new_keys";
			}
			if (menuName.equalsIgnoreCase("LootTables")) {
				name = "Loot_tables";
			}
			if (menuName.equalsIgnoreCase("Edit_loot_table")) {
				name = "Edit_loot_table";
			}
			if (menuName.equalsIgnoreCase("Settings_container_data")) {
				name = "Settings_container_data";
			}
			if (menuName.equalsIgnoreCase("SaveItems")) {
				name = "Save_items";
			}
			config.set("Menus." + name + ".menu_settings.name", mainButton.getMenuTitle());
			if (mainButton.getMenuFillSpace() != null)
				config.set("Menus." + name + ".menu_settings.fill-space", mainButton.getMenuFillSpace());
			config.set("Menus." + name + ".menu_settings.size", mainButton.getMenuSize());
			config.set("Menus." + name + ".menu_settings.size", mainButton.getMenuSize());
			sortMap(config, name, menuName, buttons);
		}
		try {
			config.save(file);
		} catch (IOException exception) {
			exception.printStackTrace();
		}
	}

	public void sortMap(FileConfiguration config, String name, String menuName, Map<String, Guidata> buttons) {

		for (Entry<String, Guidata> buttonsEntry : buttons.entrySet()) {
			if (!buttonsEntry.getKey().contains(menuName)) continue;
			Guidata guidata = buttonsEntry.getValue();
			String slot = guidata.getSlot();
			if (slot == null) {
				continue;
			}
			if (slot.equals("0"))
				slot = "-1";
			List<String> loreList = new ArrayList<>();
			guidata.getLore().forEach(lore -> loreList.add(lore == null || lore.isEmpty() ? "&6" : lore));
			String path = "Menus." + name + ".buttons." + slot;
			boolean containsPath = false;
			if (config.contains(path + ".passive") && buttonsEntry.getKey().contains("not")) {
				List<String> passiveLoreList = new ArrayList<>();
				guidata.getLore().forEach(lore -> passiveLoreList.add(lore == null || lore.isEmpty() ? "&6" : lore));
				config.set(path + ".passive.material", guidata.getIcon());
				config.set(path + ".passive.name", guidata.getDisplayname());
				config.set(path + ".passive.glow", guidata.isGlow());
				config.set(path + ".passive.lore", passiveLoreList);
				containsPath = true;
			}
			if (config.contains(path + ".active")&& !buttonsEntry.getKey().contains("not")) {
				List<String> activeLoreList = new ArrayList<>();
				guidata.getLore().forEach(lore -> activeLoreList.add(lore == null || lore.isEmpty() ? "&6" : lore));
				config.set(path + ".active.material", guidata.getIcon());
				config.set(path + ".active.name", guidata.getDisplayname());
				config.set(path + ".active.glow", guidata.isGlow());
				config.set(path + ".active.lore", activeLoreList );
				containsPath = true;
			}
			if(!containsPath) {
				config.set(path + ".material", guidata.getIcon());
				config.set(path + ".name", guidata.getDisplayname());
				config.set(path + ".glow", guidata.isGlow());
				config.set(path + ".lore", loreList);
			}
		}
	}

	public void setDataYamlfile(String mainkey, Map<String, Guidata> values) {
		this.chacheGuiSettings.put(mainkey, values);

	}

	public Map<String, Guidata> getGuiValues(String childrenKey) {
		return this.chacheGuiSettings.get(childrenKey);
	}

	public static GuiTempletSettings getInstance() {
		return instance;
	}

	@Override
	protected void saveDataToFile(final File file) {

	}

}
