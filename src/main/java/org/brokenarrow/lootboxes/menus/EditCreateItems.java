package org.brokenarrow.lootboxes.menus;

import org.brokenarrow.lootboxes.Lootboxes;
import org.brokenarrow.lootboxes.builder.GuiTempletsYaml;
import org.brokenarrow.lootboxes.lootdata.ItemData;
import org.brokenarrow.lootboxes.lootdata.LootItems;
import org.brokenarrow.lootboxes.settings.Settings;
import org.brokenarrow.lootboxes.untlity.CreateItemUtily;
import org.brokenarrow.menu.library.CheckItemsInsideInventory;
import org.brokenarrow.menu.library.MenuButton;
import org.brokenarrow.menu.library.MenuHolder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class EditCreateItems extends MenuHolder {
	private final MenuButton saveItems;
	private final MenuButton backButton;
	private final MenuButton newItem;
	private final MenuButton listOfItems;
	private final LootItems lootItems = LootItems.getInstance();
	private final ItemData itemData = ItemData.getInstance();
	private final Settings settings = Lootboxes.getInstance().getSettings();
	private final GuiTempletsYaml.Builder guiTemplets;

	public EditCreateItems(String lootTable) {
		super(LootItems.getInstance().getItems(lootTable));
		guiTemplets = new GuiTempletsYaml.Builder(getViewer(), "Edit_Items_For_LootTable");

		setMenuSize(guiTemplets.build().getGuiSize());
		setTitle(guiTemplets.build().getGuiTitle());
		setFillSpace(guiTemplets.build().getFillSpace());

		Map<ItemStack, ItemStack> cacheItemData = new HashMap<>();
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

				if (object instanceof ItemStack) {
					ItemStack itemStack = cacheItemData.get(object);
					player.getInventory().addItem(itemStack);
				}
			}

			@Override
			public ItemStack getItem() {

				return null;
			}

			@Override
			public ItemStack getItem(Object object) {

				if (object instanceof Material) {
					LootItems.LootData data = lootItems.getSettings().get(lootTable).get(object);
					if (data != null) {
						ItemStack itemStack;
						if (data.isHaveMetadata()) {
							itemStack = itemData.getCacheItemData().get(data.getItemdata());
						} else
							itemStack = new ItemStack((Material) object);
						if (itemStack == null) return null;
						GuiTempletsYaml gui = guiTemplets.menuKey("Item_List").placeholders(
								data.isHaveMetadata() && itemStack.hasItemMeta() && itemStack.getItemMeta().hasDisplayName() ? itemStack.getItemMeta().getDisplayName() : itemStack.getType().toString().toLowerCase(Locale.ROOT),
								data.getChance(),
								data.getMinimum(),
								data.getMaximum(),
								data.isHaveMetadata()
						).build();

						ItemStack guiItem = CreateItemUtily.of(itemStack.clone(),
								gui.getDisplayName(),
								gui.getLore()).makeItemStack();
						cacheItemData.put(guiItem, itemStack);
						return guiItem;
					}
				}
				if (object instanceof ItemStack) {
					ItemStack item = ((ItemStack) object);
					System.out.println("item " + item);
					if (cacheItemData.get(item) == null) return null;
					LootItems.LootData data = lootItems.getSettings().get(lootTable).get(item.getType());

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

		if (guiTemplets.menuKey("Save_Items").build().getSlot().contains(slot))
			return saveItems.getItem();
		if (guiTemplets.menuKey("Save_Items").build().getSlot().contains(slot))
			return newItem.getItem();
		if (guiTemplets.menuKey("Back_button").build().getSlot().contains(slot))
			return backButton.getItem();

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
						lootItems.addItems(lootTable, item, fileName, !fileName.isEmpty());
					}
					Bukkit.getScheduler().runTaskLaterAsynchronously(Lootboxes.getInstance(), () -> {
						itemData.save();
						lootItems.save();
					}, 5);
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
		public ItemStack getItemAt(int slot) {

			if (guiTemplets.menuKey("Save_Items_button").build().getSlot().contains(slot))
				return saveItems.getItem();
			if (guiTemplets.menuKey("Back_button").build().getSlot().contains(slot))
				return this.backButton.getItem();
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
						String fileName = "";
						if (item == null) continue;

						if (click.isLeftClick() && item.hasItemMeta()) {
							fileName = itemData.setCacheItemData(item.getType() + "", item);

						}

						lootItems.addItems(lootTable, item, fileName, !fileName.isEmpty());
					}
					Bukkit.getScheduler().runTaskLaterAsynchronously(Lootboxes.getInstance(), () -> {
						itemData.save();
						lootItems.save();
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
		public ItemStack getItemAt(int slot) {

			if (guiTemplets.menuKey("Confirm_Save").build().getSlot().contains(slot))
				return confirmSave.getItem();

			if (guiTemplets.menuKey("Back_button").build().getSlot().contains(slot))
				return this.backButton.getItem();
			return null;
		}
	}
}
