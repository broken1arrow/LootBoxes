package org.brokenarrow.lootboxes.menus;

import org.brokenarrow.lootboxes.Lootboxes;
import org.brokenarrow.lootboxes.builder.GuiTempletsYaml;
import org.brokenarrow.lootboxes.builder.LootData;
import org.brokenarrow.lootboxes.lootdata.LootItems;
import org.brokenarrow.lootboxes.settings.Settings;
import org.brokenarrow.lootboxes.untlity.CreateItemUtily;
import org.brokenarrow.menu.library.CheckItemsInsideInventory;
import org.brokenarrow.menu.library.MenuButton;
import org.brokenarrow.menu.library.MenuHolder;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class EditCreateItems extends MenuHolder {
	private final MenuButton saveItems;
	private final MenuButton backButton;
	private final MenuButton newItem;
	private final MenuButton listOfItems;
	private final MenuButton forward;
	private final MenuButton previous;
	private final LootItems lootItems = LootItems.getInstance();
	private final org.brokenarrow.lootboxes.lootdata.ItemData itemData = org.brokenarrow.lootboxes.lootdata.ItemData.getInstance();
	private final Settings settings = Lootboxes.getInstance().getSettings();
	private final GuiTempletsYaml.Builder guiTemplets;

	public EditCreateItems(String lootTable) {
		super(LootItems.getInstance().getItems(lootTable));
		guiTemplets = new GuiTempletsYaml.Builder(getViewer(), "Edit_Items_For_LootTable").placeholders("");

		setMenuSize(guiTemplets.build().getGuiSize());
		setTitle(guiTemplets.build().getGuiTitle());
		setFillSpace(guiTemplets.build().getFillSpace());

		Map<ItemStack, ItemData> cacheItemData = new HashMap<>();
		saveItems = new MenuButton() {
			@Override
			public void onClickInsideMenu(Player player, Inventory menu, ClickType click, ItemStack clickedItem, Object object) {
				new SaveItems(lootTable).menuOpen(player);

			}

			@Override
			public ItemStack getItem() {
				GuiTempletsYaml gui = guiTemplets.menuKey("Save_Items").build();

				return CreateItemUtily.of(gui.getIcon(),
						gui.getDisplayName(),
						gui.getLore()).makeItemStack();
			}
		};
		newItem = new MenuButton() {
			@Override
			public void onClickInsideMenu(Player player, Inventory menu, ClickType click, ItemStack clickedItem, Object object) {

			}

			@Override
			public ItemStack getItem() {
				return null;
			}
		};
		backButton = new MenuButton() {

			@Override
			public void onClickInsideMenu(Player player, Inventory menu, ClickType click, ItemStack clickedItem, Object object) {
				new EditCreateLootTable().menuOpen(player);
			}

			@Override
			public ItemStack getItem() {
				GuiTempletsYaml gui = guiTemplets.menuKey("Back_button").build();

				return CreateItemUtily.of(gui.getIcon(),
						gui.getDisplayName(),
						gui.getLore()).makeItemStack();
			}
		};
		listOfItems = new MenuButton() {
			@Override
			public void onClickInsideMenu(Player player, Inventory menu, ClickType click, ItemStack clickedItem, Object object) {

				if (object instanceof String) {
					ItemData itemData = cacheItemData.get(clickedItem);
					if (click.isRightClick())
						player.getInventory().addItem(itemData.getItemStack());
					else
						new CustomizeItem(lootTable, itemData.getItemPathKey()).menuOpen(player);
				}
			}

			@Override
			public ItemStack getItem() {

				return null;
			}

			@Override
			public ItemStack getItem(Object object) {

				if (object instanceof String) {
					LootData data = lootItems.getCachedTableContents(lootTable).get(object);
					if (data != null) {
						ItemStack itemStack;
						if (data.isHaveMetadata()) {
							itemStack = itemData.getCacheItemData(data.getItemdataFileName(), data.getItemdataPath());
						} else
							itemStack = new ItemStack(data.getMaterial());
						if (itemStack == null) return null;
						ItemStack clonedItem = itemStack.clone();
						GuiTempletsYaml gui = guiTemplets.menuKey("Item_List").placeholders(
								data.isHaveMetadata() && clonedItem.hasItemMeta() && clonedItem.getItemMeta().hasDisplayName() ? clonedItem.getItemMeta().getDisplayName() : clonedItem.getType().toString().toLowerCase(Locale.ROOT),
								data.getChance(),
								data.getMinimum(),
								data.getMaximum(),
								data.isHaveMetadata()
						).build();
						ItemMeta itemMeta = clonedItem.getItemMeta();
						clonedItem.setItemMeta(itemMeta);

						ItemStack guiItem = CreateItemUtily.of(clonedItem,
								gui.getDisplayName(),
								gui.getLore()).isShowEnchantments(true).makeItemStack();
						cacheItemData.put(guiItem, new ItemData(itemStack, (String) object));
						return guiItem;
					}
				}
				if (object instanceof ItemStack) {
					ItemStack item = ((ItemStack) object);
					if (cacheItemData.get(item) == null) return null;

					return item;
				}
				return null;
			}
		};
		previous = new MenuButton() {
			@Override
			public void onClickInsideMenu(final Player player, final Inventory menu, final ClickType click, final ItemStack clickedItem, final Object object) {

				if (click.isLeftClick()) {
					previousPage();
				}
			}

			@Override
			public ItemStack getItem() {
				GuiTempletsYaml gui = guiTemplets.menuKey("Previous_button").build();

				return CreateItemUtily.of(gui.getIcon(), gui.getDisplayName(),
						gui.getLore()).makeItemStack();
			}
		};
		forward = new MenuButton() {
			@Override
			public void onClickInsideMenu(final Player player, final Inventory menu, final ClickType click, final ItemStack clickedItem, final Object object) {
				if (click.isLeftClick()) {
					nextPage();
				}
			}

			@Override
			public ItemStack getItem() {
				GuiTempletsYaml gui = guiTemplets.menuKey("Forward_button").build();
				return CreateItemUtily.of(gui.getIcon(), gui.getDisplayName(),
						gui.getLore()).makeItemStack();
			}
		};
	}

	@Override
	public MenuButton getFillButtonAt(Object o) {
		return listOfItems;

	}

	@Override
	public MenuButton getButtonAt(int slot) {

		if (guiTemplets.menuKey("Forward_button").build().getSlot().contains(slot))
			return forward;
		if (guiTemplets.menuKey("Previous_button").build().getSlot().contains(slot))
			return previous;
		if (guiTemplets.menuKey("Save_Items").build().getSlot().contains(slot))
			return saveItems;
		if (guiTemplets.menuKey("Save_Items").build().getSlot().contains(slot))
			return newItem;
		if (guiTemplets.menuKey("Back_button").build().getSlot().contains(slot))
			return backButton;

		return null;
	}

	public class SaveItems extends MenuHolder {
		private final MenuButton saveItems;
		private final MenuButton backButton;
		private final GuiTempletsYaml.Builder guiTemplets;

		public SaveItems(String lootTable) {
			guiTemplets = new GuiTempletsYaml.Builder(getViewer(), "SaveItems");

			setMenuSize(guiTemplets.build().getGuiSize());
			setTitle(guiTemplets.build().getGuiTitle());
			setFillSpace(guiTemplets.build().getFillSpace());
			/*setFillSpace(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9));
			setMenuSize(45);*/
			setSlotsYouCanAddItems(true);

			saveItems = new MenuButton() {
				@Override
				public void onClickInsideMenu(Player player, Inventory menu, ClickType click, ItemStack clickedItem, Object object) {
					Map<Integer, ItemStack> items = new CheckItemsInsideInventory().getItemsExceptBottomBar(menu, null, false);
					if (items == null || items.isEmpty()) return;

					for (ItemStack item : items.values()) {
						if (item == null) continue;
						String fileName = "";
						if (item.hasItemMeta() && settings.getSettings().isSaveMetadataOnItem()) {
							if (settings.getSettings().isWarnBeforeSaveWithMetadata()) {
								new ConfirmIfItemHaveMetadata(items, lootTable).menuOpen(player);
								return;
							} else {
								fileName = itemData.setCacheItemData(item.getType() + "", item);
							}
						}
						lootItems.addItems(lootTable, item, itemData.getFileName(), fileName, !fileName.isEmpty());
					}
					new EditCreateItems(lootTable).menuOpen(player);

				}

				@Override
				public ItemStack getItem() {
					GuiTempletsYaml gui = guiTemplets.menuKey("Save_Items_button").build();

					return CreateItemUtily.of(gui.getIcon(),
							gui.getDisplayName(),
							gui.getLore()).makeItemStack();
				}
			};
			this.backButton = new MenuButton() {

				@Override
				public void onClickInsideMenu(Player player, Inventory menu, ClickType click, ItemStack clickedItem, Object object) {
					new EditCreateItems(lootTable).menuOpen(player);
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
		public MenuButton getButtonAt(int slot) {

			if (guiTemplets.menuKey("Save_Items_button").build().getSlot().contains(slot))
				return saveItems;
			if (guiTemplets.menuKey("Back_button").build().getSlot().contains(slot))
				return this.backButton;
			return null;
		}
	}

	public class ConfirmIfItemHaveMetadata extends MenuHolder {

		private final MenuButton confirmSave;
		private final MenuButton backButton;
		private final GuiTempletsYaml.Builder guiTemplets;

		public ConfirmIfItemHaveMetadata(Map<Integer, ItemStack> items, String lootTable) {
			guiTemplets = new GuiTempletsYaml.Builder(getViewer(), "Confirm_If_Item_Have_Metadata");

			setMenuSize(guiTemplets.build().getGuiSize());
			setTitle(guiTemplets.build().getGuiTitle());


			confirmSave = new MenuButton() {
				@Override
				public void onClickInsideMenu(Player player, Inventory menu, ClickType click, ItemStack clickedItem, Object object) {
					for (ItemStack item : items.values()) {
						String itemdataPath = "";
						if (item == null) continue;

						if (click.isLeftClick() && item.hasItemMeta()) {
							//lootItems. getSettings().get(lootTable).get()
							itemdataPath = itemData.setCacheItemData(item.getType() + "", item);

						}

						lootItems.addItems(lootTable, item, itemData.getFileName(), itemdataPath, !itemdataPath.isEmpty());
					}
					Bukkit.getScheduler().runTaskLaterAsynchronously(Lootboxes.getInstance(), () -> {
						itemData.save();
						lootItems.save(lootTable);
					}, 5);
					new EditCreateItems(lootTable).menuOpen(player);
				}

				@Override
				public ItemStack getItem() {
					GuiTempletsYaml gui = guiTemplets.menuKey("Confirm_Save").build();

					return CreateItemUtily.of(gui.getIcon(),
							gui.getDisplayName(),
							gui.getLore()).makeItemStack();
				}
			};
			this.backButton = new MenuButton() {

				@Override
				public void onClickInsideMenu(Player player, Inventory menu, ClickType click, ItemStack clickedItem, Object object) {
					new EditCreateItems(lootTable).menuOpen(player);
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
		public MenuButton getButtonAt(int slot) {

			if (guiTemplets.menuKey("Confirm_Save").build().getSlot().contains(slot))
				return confirmSave;

			if (guiTemplets.menuKey("Back_button").build().getSlot().contains(slot))
				return this.backButton;
			return null;
		}
	}

	public static class ItemData {
		private final ItemStack itemStack;
		private final String itemPathKey;

		public ItemData(ItemStack itemStack, String itemPathKey) {
			this.itemStack = itemStack;
			this.itemPathKey = itemPathKey;
		}

		public ItemStack getItemStack() {
			return itemStack;
		}

		public String getItemPathKey() {
			return itemPathKey;
		}
	}
}
