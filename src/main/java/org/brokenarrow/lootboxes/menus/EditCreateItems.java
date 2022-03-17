package org.brokenarrow.lootboxes.menus;

import org.brokenarrow.lootboxes.Lootboxes;
import org.brokenarrow.lootboxes.lootdata.ItemData;
import org.brokenarrow.lootboxes.lootdata.LootItems;
import org.brokenarrow.lootboxes.settings.Settings;
import org.brokenarrow.menu.library.CheckItemsInsideInventory;
import org.brokenarrow.menu.library.MenuButton;
import org.brokenarrow.menu.library.MenuHolder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.Map;

public class EditCreateItems extends MenuHolder {
	private final MenuButton saveItems;
	private final MenuButton backButton;
	private final MenuButton newItem;
	private final MenuButton listOfItems;
	private final LootItems lootItems = LootItems.getInstance();
	private final ItemData itemData = ItemData.getInstance();
	private final Settings settings = Lootboxes.getInstance().getSettings();

	public EditCreateItems(String lootTable) {
		super(LootItems.getInstance().getItems(lootTable));
		setFillSpace(Arrays.asList(1, 2, 3));
		setMenuSize(45);

		saveItems = new MenuButton() {
			@Override
			public void onClickInsideMenu(Player player, Inventory menu, ClickType click, ItemStack clickedItem, Object object) {
				Map<Integer, ItemStack> items = new CheckItemsInsideInventory().getItemsExceptBottomBar(menu, null, false);
				for (ItemStack item : items.values()) {
					String fileName = "";
					if (item.hasItemMeta() && settings.getSettings().isSaveMetadataOnItem()) {
						if (settings.getSettings().isWarnBeforeSaveWithMetadata()) {
							new ConfirmIfItemHaveMetadata(items, lootTable).menuOpen(player);
							return;
						} else {
							fileName = item.getType() + "";
							itemData.setCacheItemData(fileName, item);
						}
					}
					lootItems.addItems(lootTable, item, fileName, !fileName.isEmpty());
				}
				Bukkit.getScheduler().runTaskLaterAsynchronously(Lootboxes.getInstance(), () -> {
					itemData.save();
					lootItems.save();
				}, 5);

			}

			@Override
			public ItemStack getItem() {
				return new ItemStack(Material.CHAIN);
			}
		};
		newItem = new MenuButton() {
			@Override
			public void onClickInsideMenu(Player player, Inventory menu, ClickType click, ItemStack clickedItem, Object object) {

			}

			@Override
			public ItemStack getItem() {
				return new ItemStack(Material.CHAIN);
			}
		};
		backButton = new MenuButton() {

			@Override
			public void onClickInsideMenu(Player player, Inventory menu, ClickType click, ItemStack clickedItem, Object object) {
				new EditCreateLootTable().menuOpen(player);
			}

			@Override
			public ItemStack getItem() {
				return new ItemStack(Material.CHORUS_FRUIT);
			}
		};
		listOfItems = new MenuButton() {
			@Override
			public void onClickInsideMenu(Player player, Inventory menu, ClickType click, ItemStack clickedItem, Object object) {

			}

			@Override
			public ItemStack getItem() {

				return null;
			}

			@Override
			public ItemStack getItem(Object object) {

				if (object instanceof Material) {
					LootItems.LootData data = lootItems.getSettings().get(lootTable).get(object);

					if (data != null)
						if (data.isHaveMetadata()) {
							return itemData.getCacheItemData().get(data.getItemdata());
						} else
							return new ItemStack((Material) object);
				}
				if (object instanceof ItemStack) {
					ItemStack item = ((ItemStack) object);
					LootItems.LootData data = lootItems.getSettings().get(lootTable).get(item.getType());
					if (data != null) {
						if (data.isHaveMetadata())
							item = itemData.getCacheItemData().get(data.getItemdata());
						return item;
					}
				}
				return null;
			}
		};

	}

	@Override
	public ItemStack getFillItemsAt(Object o) {
		return listOfItems.getItem(o);

	}

	@Override
	public ItemStack getItemAt(int slot) {

		if (slot == 38)
			return saveItems.getItem();
		if (slot == 40)
			return newItem.getItem();
		if (slot == 44)
			return backButton.getItem();

		return null;
	}

	public class ConfirmIfItemHaveMetadata extends MenuHolder {

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
