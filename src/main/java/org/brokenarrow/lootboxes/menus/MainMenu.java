package org.brokenarrow.lootboxes.menus;

import brokenarrow.menu.lib.MenuButton;
import brokenarrow.menu.lib.MenuHolder;
import org.brokenarrow.lootboxes.lootdata.ItemData;
import org.brokenarrow.lootboxes.lootdata.LootItems;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class MainMenu extends MenuHolder {

	private final MenuButton editAndCreateTable;
	private final MenuButton defultSettings;
	private final LootItems lootItems = LootItems.getInstance();
	private final ItemData itemData = ItemData.getInstance();

	public MainMenu() {

		editAndCreateTable = new MenuButton() {
			@Override
			public void onClickInsideMenu(Player player, Inventory menu, ClickType click, ItemStack clickedItem, Object object) {
				new EditAndCreateTable().menuOpen(player);
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

	public class EditAndCreateTable extends MenuHolder {
		private final MenuButton newTable;
		private final MenuButton listOfTables;
		Map<ItemStack, ItemStack> data = new HashMap<>();

		public EditAndCreateTable() {
			super(new ArrayList<>(lootItems.getSettings().keySet()));

			newTable = new MenuButton() {
				@Override
				public void onClickInsideMenu(Player player, Inventory menu, ClickType click, ItemStack clickedItem, Object object) {

				}

				@Override
				public ItemStack getItem() {
					return null;
				}
			};
			listOfTables = new MenuButton() {
				@Override
				public void onClickInsideMenu(Player player, Inventory menu, ClickType click, ItemStack clickedItem, Object object) {
					if (object instanceof ItemStack) {
						ItemStack itemStack = data.get(object);
						ItemMeta itemMeta = itemStack.getItemMeta();
						if (itemMeta != null && itemMeta.hasDisplayName())
							new EditAndCreateItems(itemMeta.getDisplayName());
					}
				}

				@Override
				public ItemStack getItem() {
					return null;
				}

				@Override
				public ItemStack getItem(Object object) {

					if (object instanceof String) {
						ItemStack itemStack = new ItemStack(Material.PAPER);
						ItemMeta itemMeta = itemStack.getItemMeta();
						itemMeta.setDisplayName((String) object);
						itemStack.setItemMeta(itemMeta);
						data.put(itemStack, itemStack);
						return itemStack;
					} else if (object instanceof ItemStack)
						return data.get(object);
					return null;
				}
			};
		}

		@Override
		public ItemStack getFillItemsAt(Object o) {
			return listOfTables.getItem(o);

		}

		@Override
		public ItemStack getItemAt(int slot) {

			if (slot == 5)
				return newTable.getItem();
			return null;
		}
	}

	public class EditAndCreateItems extends MenuHolder {
		private final MenuButton newItem;
		private final MenuButton listOfItems;

		public EditAndCreateItems(String table) {
			super(new ArrayList<>(lootItems.getSettings().get(table).keySet()));

			newItem = new MenuButton() {
				@Override
				public void onClickInsideMenu(Player player, Inventory menu, ClickType click, ItemStack clickedItem, Object object) {

				}

				@Override
				public ItemStack getItem() {
					return null;
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
						LootItems.LootData data = lootItems.getSettings().get(table).get(object);
						if (data != null)
							if (data.isHaveMetadata())
								return itemData.getCacheItemData().get(data.getItemdata())[0];
							else
								return new ItemStack((Material) object);
					}
					if (object instanceof ItemStack) {
						ItemStack item = ((ItemStack) object);
						LootItems.LootData data = lootItems.getSettings().get(table).get(item.getType());
						if (data != null)
							return item;
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

			if (slot == 5)
				return newItem.getItem();
			return null;
		}

	}

	public ItemStack convertObjectToItemstack(Iterator<?> iterator, Class<?> clazz, Object object) {

		clazz.cast(object);
		return null;
	}
}
