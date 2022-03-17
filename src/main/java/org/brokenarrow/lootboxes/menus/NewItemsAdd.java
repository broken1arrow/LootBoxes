package org.brokenarrow.lootboxes.menus;

import org.brokenarrow.lootboxes.lootdata.ItemData;
import org.brokenarrow.lootboxes.lootdata.LootItems;
import org.brokenarrow.menu.library.CheckItemsInsideInventory;
import org.brokenarrow.menu.library.MenuButton;
import org.brokenarrow.menu.library.MenuHolder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Map;

public class NewItemsAdd extends MenuHolder {
	private final MenuButton saveItems;
	private final MenuButton backButton;
	private final LootItems lootItems = LootItems.getInstance();

	public NewItemsAdd(List<Integer> slots, String lootTable) {
		setMenuSize(45);

		saveItems = new MenuButton() {
			@Override
			public void onClickInsideMenu(Player player, Inventory menu, ClickType click, ItemStack clickedItem, Object object) {
				Map<Integer, ItemStack> items = new CheckItemsInsideInventory().getItemsExceptBottomBar(menu, null, false);
				for (ItemStack item : items.values()) {
					if (item.hasItemMeta()) {
						new ConfirmIfItemHaveMetadata(items, lootTable).menuOpen(player);
						return;
					}
					lootItems.addItems(lootTable, item, "", true);
				}
			}

			@Override
			public ItemStack getItem() {
				return null;
			}
		};
		backButton = new MenuButton() {

			@Override
			public void onClickInsideMenu(Player player, Inventory menu, ClickType click, ItemStack clickedItem, Object object) {
				new MainMenu().menuOpen(player);
			}

			@Override
			public ItemStack getItem() {
				return new ItemStack(Material.CHORUS_FRUIT);
			}
		};

	}

	@Override
	public ItemStack getItemAt(int slot) {
		if (slot == 40)
			return saveItems.getItem();
		if (slot == 44)
			return backButton.getItem();
		return null;
	}

	public class ConfirmIfItemHaveMetadata extends MenuHolder {
		private final ItemData itemData = ItemData.getInstance();
		private final MenuButton confirmSave;

		public ConfirmIfItemHaveMetadata(Map<Integer, ItemStack> items, String lootTable) {
			setMenuSize(5);
			confirmSave = new MenuButton() {
				@Override
				public void onClickInsideMenu(Player player, Inventory menu, ClickType click, ItemStack clickedItem, Object object) {
					for (ItemStack item : items.values()) {
						String fileName = "";
						if (item.hasItemMeta()) {
							fileName = item.getType() + "";
							itemData.setCacheItemData(fileName, item);

						}

						lootItems.addItems(lootTable, item, fileName, !fileName.isEmpty());
					}
				}

				@Override
				public ItemStack getItem() {
					return null;
				}
			};

		}

		@Override
		public ItemStack getItemAt(int slot) {
			if (slot == 40)
				return confirmSave.getItem();

			return null;
		}
	}
}
