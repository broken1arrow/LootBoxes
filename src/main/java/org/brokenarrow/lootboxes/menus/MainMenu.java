package org.brokenarrow.lootboxes.menus;

import org.brokenarrow.lootboxes.lootdata.ItemData;
import org.brokenarrow.lootboxes.lootdata.LootItems;
import org.brokenarrow.menu.library.MenuButton;
import org.brokenarrow.menu.library.MenuHolder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Iterator;

public class MainMenu extends MenuHolder {

	private final MenuButton editAndCreateTable;
	private final MenuButton defultSettings;
	private final LootItems lootItems = LootItems.getInstance();
	private final ItemData itemData = ItemData.getInstance();

	public MainMenu() {
		setMenuSize(45);
		editAndCreateTable = new MenuButton() {
			@Override
			public void onClickInsideMenu(Player player, Inventory menu, ClickType click, ItemStack clickedItem, Object object) {
				new EditCreateLootTable().menuOpen(player);
			}

			@Override
			public ItemStack getItem() {
				return new ItemStack(Material.HOPPER);
			}
		};
		defultSettings = new MenuButton() {
			@Override
			public void onClickInsideMenu(Player player, Inventory menu, ClickType click, ItemStack clickedItem, Object object) {

			}

			@Override
			public ItemStack getItem() {
				return new ItemStack(Material.CHEST);
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


	public ItemStack convertObjectToItemstack(Iterator<?> iterator, Class<?> clazz, Object object) {

		clazz.cast(object);
		return null;
	}
}
