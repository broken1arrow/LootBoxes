package org.brokenarrow.lootboxes.settings;

import org.bukkit.configuration.ConfigurationSection;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GuiTempletSettings extends YamlUtil {

	private static final GuiTempletSettings instance = new GuiTempletSettings();
	private final Map<String, Map<String, Guidata>> chacheGuiSettings = new HashMap<>();

	public GuiTempletSettings() {
		super("guitemplets.yml", "guitemplets.yml");
	}

	@Override
	public void reload() {
		super.reload();
	}


	@Override
	protected void loadSettingsFromYaml() {
		Map<String, Guidata> yamlData = new HashMap<>();
		ConfigurationSection MainConfigKeys = customConfig.getConfigurationSection("Gui_Templets");

		if (MainConfigKeys != null) {
			for (String mainKey : MainConfigKeys.getKeys(false)) {
				ConfigurationSection innerConfigKeys = customConfig.getConfigurationSection("Gui_Templets." + mainKey);
				if (innerConfigKeys != null)
					for (String innerChildrenKeys : innerConfigKeys.getKeys(false)) {
						if (!innerChildrenKeys.equals("Menu_Size") && !innerChildrenKeys.equals("Menu_Title") && !innerChildrenKeys.equals("FillSpace")) {

							int menuGuiSize = this.customConfig.getInt("Gui_Templets." + mainKey + "." + "Menu_Size");
							String menuGuiTitle = this.customConfig.getString("Gui_Templets." + mainKey + "." + "Menu_Title");
							String fillSpace = this.customConfig.getString("Gui_Templets." + mainKey + "." + "FillSpace");
							int maxAmountOfItems = this.customConfig.getInt("Gui_Templets." + mainKey + "." + "Max_Amount_Of_Items");

							String displayname = this.customConfig.getString("Gui_Templets." + mainKey + "." + innerChildrenKeys + ".Display_name");
							String slot = this.customConfig.getString("Gui_Templets." + mainKey + "." + innerChildrenKeys + ".Slot");
							String icon = this.customConfig.getString("Gui_Templets." + mainKey + "." + innerChildrenKeys + ".Icon");
							boolean glow = this.customConfig.getBoolean("Gui_Templets." + mainKey + "." + innerChildrenKeys + ".Enchant");
							List<String> lore = this.customConfig.getStringList("Gui_Templets." + mainKey + "." + innerChildrenKeys + ".Lore");

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
	/*
	public static class Guidata {
		private int menuSize;
		private String menuTitle = "";
		private String menuFillSpace;
		private int menuMaxAmountOfItems;
		private boolean glow;
		private String displayname = "";
		private String slot;
		private String icon = "";
		private List<String> lore;


		public Guidata() {
		}

		public Guidata(int menuSize, String menuTitle, String MenufillSpace, int menuMaxAmountOfItems, String displayname, String slot, String icon, List<String> lore, boolean glow) {
			this.menuSize = menuSize;
			this.menuTitle = menuTitle;
			this.menuFillSpace = MenufillSpace;
			this.menuMaxAmountOfItems = menuMaxAmountOfItems;
			this.displayname = displayname;
			this.slot = slot;
			this.icon = icon;
			this.lore = lore;
			this.glow = glow;
		}


		public static Guidata of(String menuTitle, int menuSize, String displayname, String slot, String icon, List<String> lore, String fillSpace, int menuMaxAmountOfItems) {
			final Guidata data = new Guidata();

			data.menuTitle = menuTitle;
			data.menuSize = menuSize;
			data.menuFillSpace = fillSpace;
			data.menuMaxAmountOfItems = menuMaxAmountOfItems;
			data.displayname = displayname;
			data.slot = slot;
			data.icon = icon;
			data.lore = lore;


			return data;
		}

		public static Guidata of(boolean glow, String displayname, String slot, String icon, List<String> lore) {
			final Guidata data = new Guidata();

			data.displayname = displayname;
			data.slot = slot;
			data.icon = icon;
			data.lore = lore;
			data.glow = glow;

			return data;
		}


		public int getMenuSize() {
			return menuSize;
		}

		public String getMenuTitle() {
			return menuTitle;
		}

		public boolean isGlow() {
			return glow;
		}

		public String getDisplayname() {
			return displayname;
		}

		public String getSlot() {
			return slot;
		}

		public String getIcon() {
			return icon;
		}

		public int getMenuMaxAmountOfItems() {
			return menuMaxAmountOfItems;
		}

		public String getMenuFillSpace() {
			return menuFillSpace;
		}

		public List<String> getLore() {
			return lore;
		}
	}

	public static GuiTempletSettings getInstance() {
		return instance;
	}*/
}
