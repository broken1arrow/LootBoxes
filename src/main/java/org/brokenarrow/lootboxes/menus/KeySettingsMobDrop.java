package org.brokenarrow.lootboxes.menus;

import org.brokenarrow.lootboxes.Lootboxes;
import org.brokenarrow.lootboxes.builder.GuiTempletsYaml;
import org.brokenarrow.lootboxes.builder.KeyMobDropData;
import org.brokenarrow.lootboxes.builder.SettingsData;
import org.brokenarrow.lootboxes.lootdata.KeyDropData;
import org.brokenarrow.lootboxes.lootdata.LootItems;
import org.brokenarrow.lootboxes.untlity.CreateItemUtily;
import org.brokenarrow.menu.library.MenuButton;
import org.brokenarrow.menu.library.MenuHolder;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import static org.brokenarrow.lootboxes.menus.MenuKeys.KEY_SETTINGS_MOBDROP;

public class KeySettingsMobDrop extends MenuHolder {
	private final MenuButton backButton;
	private final MenuButton removeButton;
	private final MenuButton setMobsDropThisKey;
	private final MenuButton enchantItem;
	private final MenuButton changeChance;
	private final MenuButton changeMiniAmount;
	private final MenuButton changeMaxAmount;
	private final GuiTempletsYaml.Builder guiTemplets;
	private final LootItems lootItems = LootItems.getInstance();
	private final KeyDropData keyDropData = KeyDropData.getInstance();
	private final SettingsData settingsData = Lootboxes.getInstance().getSettings().getSettings();

	public KeySettingsMobDrop(String continerData, String keyName) {

		this.guiTemplets = new GuiTempletsYaml.Builder(getViewer(), "Key_Settings_MobDrop").placeholders(keyName, getPageNumber());

		setMenuSize(guiTemplets.build().getGuiSize());
		setTitle(guiTemplets.build().getGuiTitle());
		//setFillSpace(guiTemplets.build().getFillSpace());

		this.setMobsDropThisKey = new MenuButton() {
			@Override
			public void onClickInsideMenu(Player player, Inventory inventory, ClickType clickType, ItemStack itemStack, Object o) {
				new EntityTypeListMenu(KEY_SETTINGS_MOBDROP, continerData, keyName, "").menuOpen(player);
			}

			@Override
			public ItemStack getItem() {
				KeyMobDropData data = keyDropData.getKeyMobDropData(continerData, keyName);
				GuiTempletsYaml gui = guiTemplets.menuKey("Mobs_some_drop_this_key").placeholders(data != null ? data.getKeyName() : "").build();

				return CreateItemUtily.of(gui.getIcon(),
						gui.getDisplayName(),
						gui.getLore()).makeItemStack();
			}
		};
		enchantItem = new MenuButton() {
			@Override
			public void onClickInsideMenu(Player player, Inventory inventory, ClickType clickType, ItemStack itemStack, Object o) {
				new CustomizeItem.EnchantMents(continerData, keyName, "").menuOpen(player);
			}

			@Override
			public ItemStack getItem() {
				GuiTempletsYaml gui = guiTemplets.menuKey("Enchant_Item").build();

				return CreateItemUtily.of(gui.getIcon(),
						gui.getDisplayName(),
						gui.getLore()).makeItemStack();
			}
		};
		this.changeChance = new MenuButton() {
			@Override
			public void onClickInsideMenu(Player player, Inventory inventory, ClickType clickType, ItemStack itemStack, Object o) {
				KeyMobDropData data = keyDropData.getKeyMobDropData(continerData, keyName);
				KeyMobDropData.Builder builder = data.getBuilder();

				int amount = 0;
				if (clickType == ClickType.LEFT)
					amount += 1;
				if (clickType == ClickType.RIGHT)
					amount -= 1;
				if (clickType == ClickType.SHIFT_LEFT)
					amount += settingsData.getIncrese();
				if (clickType == ClickType.SHIFT_RIGHT)
					amount -= settingsData.getDecrese();
				int chance = data.getChance() + amount;
				if (chance > 100)
					chance = 100;
				if (chance < 0)
					chance = 0;
				builder.setChance(chance);
				keyDropData.putCachedKeyData(continerData, keyName, builder.build());
				updateButtons();
			}

			@Override
			public ItemStack getItem() {
				KeyMobDropData data = keyDropData.getKeyMobDropData(continerData, keyName);

				GuiTempletsYaml gui = guiTemplets.menuKey("Change_Chance").placeholders(data != null ? data.getChance() : 0, settingsData.getIncrese(), settingsData.getDecrese()).build();

				return CreateItemUtily.of(gui.getIcon(),
						gui.getDisplayName(),
						gui.getLore()).makeItemStack();
			}
		};
		this.changeMiniAmount = new MenuButton() {
			@Override
			public void onClickInsideMenu(Player player, Inventory inventory, ClickType clickType, ItemStack itemStack, Object o) {
				KeyMobDropData data = keyDropData.getKeyMobDropData(continerData, keyName);
				KeyMobDropData.Builder builder = data.getBuilder();

				int amount = 0;
				if (clickType == ClickType.LEFT)
					amount += 1;
				if (clickType == ClickType.RIGHT)
					amount -= 1;
				if (clickType == ClickType.SHIFT_LEFT)
					amount += settingsData.getIncrese();
				if (clickType == ClickType.SHIFT_RIGHT)
					amount -= settingsData.getDecrese();
				int minimum = data.getMinimum() + amount;

				if (minimum < 0)
					minimum = 0;
				builder.setMinimum(minimum);
				keyDropData.putCachedKeyData(continerData, keyName, builder.build());
				updateButtons();
			}

			@Override
			public ItemStack getItem() {
				KeyMobDropData data = keyDropData.getKeyMobDropData(continerData, keyName);
				GuiTempletsYaml gui = guiTemplets.menuKey("Change_Minimum").placeholders(data != null ? data.getMinimum() : 0, settingsData.getIncrese(), settingsData.getDecrese()).build();

				return CreateItemUtily.of(gui.getIcon(),
						gui.getDisplayName(),
						gui.getLore()).makeItemStack();
			}
		};
		this.changeMaxAmount = new MenuButton() {
			@Override
			public void onClickInsideMenu(Player player, Inventory inventory, ClickType clickType, ItemStack itemStack, Object o) {
				KeyMobDropData data = keyDropData.getKeyMobDropData(continerData, keyName);
				KeyMobDropData.Builder builder = data.getBuilder();

				int amount = 0;
				if (clickType == ClickType.LEFT)
					amount += 1;
				if (clickType == ClickType.RIGHT)
					amount -= 1;
				if (clickType == ClickType.SHIFT_LEFT)
					amount += settingsData.getIncrese();
				if (clickType == ClickType.SHIFT_RIGHT)
					amount -= settingsData.getDecrese();
				int maximum = data.getMaximum() + amount;

				if (maximum < 0)
					maximum = 0;
				builder.setMaximum(maximum);
				keyDropData.putCachedKeyData(continerData, keyName, builder.build());
				updateButtons();
			}

			@Override
			public ItemStack getItem() {
				KeyMobDropData data = keyDropData.getKeyMobDropData(continerData, keyName);
				GuiTempletsYaml gui = guiTemplets.menuKey("Change_Maximum").placeholders(data != null ? data.getMaximum() : 0, settingsData.getIncrese(), settingsData.getDecrese()).build();

				return CreateItemUtily.of(gui.getIcon(),
						gui.getDisplayName(),
						gui.getLore()).makeItemStack();
			}
		};
		removeButton = new MenuButton() {

			@Override
			public void onClickInsideMenu(Player player, Inventory menu, ClickType click, ItemStack clickedItem, Object object) {
				//lootItems.getLootData(lootTable, itemToEdit);
				//lootItems.setLootData();
			}

			@Override
			public ItemStack getItem() {
				GuiTempletsYaml gui = guiTemplets.menuKey("Remove_Button").build();

				return CreateItemUtily.of(gui.getIcon(),
						gui.getDisplayName(),
						gui.getLore()).makeItemStack();
			}
		};


		backButton = new MenuButton() {

			@Override
			public void onClickInsideMenu(Player player, Inventory menu, ClickType click, ItemStack clickedItem, Object object) {
				new EditKeysToOpen.EditKey(continerData, keyName).menuOpen(player);
			}

			@Override
			public ItemStack getItem() {
				GuiTempletsYaml gui = guiTemplets.menuKey("Back_button").build();

				return CreateItemUtily.of(gui.getIcon(),
						gui.getDisplayName(),
						gui.getLore()).makeItemStack();
			}
		};


	}

	@Override
	public ItemStack getItemAt(int slot) {

		if (guiTemplets.menuKey("Mobs_some_drop_this_key").build().getSlot().contains(slot))
			return setMobsDropThisKey.getItem();
		if (guiTemplets.menuKey("Change_Minimum").build().getSlot().contains(slot))
			return this.changeMiniAmount.getItem();
		if (guiTemplets.menuKey("Change_Maximum").build().getSlot().contains(slot))
			return this.changeMaxAmount.getItem();
		if (guiTemplets.menuKey("Enchant_Item").build().getSlot().contains(slot))
			return enchantItem.getItem();
		if (guiTemplets.menuKey("Change_Chance").build().getSlot().contains(slot))
			return changeChance.getItem();
		if (guiTemplets.menuKey("Remove_Button").build().getSlot().contains(slot))
			return removeButton.getItem();
		if (guiTemplets.menuKey("Back_button").build().getSlot().contains(slot))
			return backButton.getItem();

		return null;
	}

}
