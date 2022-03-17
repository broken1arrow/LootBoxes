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
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

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
				new EditAndCreateTable().menuOpen(player);
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

	public class EditAndCreateTable extends MenuHolder {
		private final MenuButton newTable;
		private final MenuButton backButton;
		private final MenuButton listOfTables;
		Map<ItemStack, ItemStack> data = new HashMap<>();

		public EditAndCreateTable() {
			super(new ArrayList<>(lootItems.getSettings().keySet()));
			setFillSpace(Arrays.asList(1, 2, 3));
			setMenuSize(45);

			newTable = new MenuButton() {
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
					new MainMenu().menuOpen(player);
				}

				@Override
				public ItemStack getItem() {
					return new ItemStack(Material.CHORUS_FRUIT);
				}
			};
			listOfTables = new MenuButton() {
				@Override
				public void onClickInsideMenu(Player player, Inventory menu, ClickType click, ItemStack clickedItem, Object object) {
					if (object instanceof ItemStack) {
						ItemStack itemStack = data.get(object);
						ItemMeta itemMeta = itemStack.getItemMeta();
						if (itemMeta != null && itemMeta.hasDisplayName())
							new EditAndCreateItems(itemMeta.getDisplayName()).menuOpen(player);
					}
				}

				@Override
				public ItemStack getItem() {
					return null;
				}

				@Override
				public ItemStack getItem(Object object) {

					if (object instanceof String) {
						if (object.equals("Global_Values")) return null;
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

			if (slot == 40)
				return newTable.getItem();
			if (slot == 44)
				return backButton.getItem();
			return null;
		}
	}

	public class EditAndCreateItems extends MenuHolder {
		private final MenuButton newItem;
		private final MenuButton listOfItems;
		private final MenuButton backButton;

		public EditAndCreateItems(String table) {
			super(lootItems.getItems(table));
			setFillSpace(Arrays.asList(1, 2, 3));
			setMenuSize(45);
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
					new EditAndCreateTable().menuOpen(player);
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
						LootItems.LootData data = lootItems.getSettings().get(table).get(object);

						if (data != null)
							if (data.isHaveMetadata()) {
								return itemData.getCacheItemData().get(data.getItemdata())[0];
							} else
								return new ItemStack((Material) object);
					}
					if (object instanceof ItemStack) {
						ItemStack item = ((ItemStack) object);
						LootItems.LootData data = lootItems.getSettings().get(table).get(item.getType());
						if (data != null) {
							if (data.isHaveMetadata())
								item = itemData.getCacheItemData().get(data.getItemdata())[0];
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

			if (slot == 40)
				return newItem.getItem();
			if (slot == 44)
				return backButton.getItem();
			return null;
		}

	}

	public ItemStack convertObjectToItemstack(Iterator<?> iterator, Class<?> clazz, Object object) {

		clazz.cast(object);
		return null;
	}
}
