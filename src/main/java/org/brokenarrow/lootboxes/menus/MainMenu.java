package org.brokenarrow.lootboxes.menus;

import brokenarrow.menu.lib.MenuButton;
import brokenarrow.menu.lib.MenuHolder;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class MainMenu extends MenuHolder {

	private final MenuButton editAndCreateTable;
	private final MenuButton defultSettings;

	public MainMenu() {

		editAndCreateTable = new MenuButton() {
			@Override
			public void onClickInsideMenu(Player player, Inventory menu, ClickType click, ItemStack clickedItem, Object object) {

			}

			@Override
			public ItemStack getItem() {
				return null;
			}
		};
		defultSettings = new MenuButton() {
			@Override
			public void onClickInsideMenu(Player player, Inventory menu, ClickType click, ItemStack clickedItem, Object object) {

			}

			@Override
			public ItemStack getItem() {
				return null;
			}
		};

	}

	@Override
	public ItemStack getItemAt(int slot) {

		if (slot == 5)
			return editAndCreateTable.getItem();
		if (slot == 6)
			return defultSettings.getItem();

		return null;
	}
}
