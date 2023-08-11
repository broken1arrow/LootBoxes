package org.brokenarrow.lootboxes.menus.itemcreation;

import org.broken.arrow.menu.button.manager.library.utility.MenuButtonData;
import org.broken.arrow.menu.button.manager.library.utility.MenuTemplate;
import org.broken.arrow.menu.library.button.MenuButton;
import org.broken.arrow.menu.library.holder.MenuHolder;
import org.brokenarrow.lootboxes.Lootboxes;
import org.brokenarrow.lootboxes.builder.LootData;
import org.brokenarrow.lootboxes.lootdata.LootItems;
import org.brokenarrow.lootboxes.menus.CustomizeItem;
import org.brokenarrow.lootboxes.menus.loottable.EditCreateLootTable;
import org.brokenarrow.lootboxes.untlity.CreateItemUtily;
import org.brokenarrow.lootboxes.untlity.TranslatePlaceHolders;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

import static org.brokenarrow.lootboxes.untlity.BountifyStrings.bountifyCapitalized;
import static org.brokenarrow.lootboxes.untlity.TranslatePlaceHolders.getPlaceholders;

public class EditCreateItems extends MenuHolder {
	private final LootItems lootItems = LootItems.getInstance();
	private final org.brokenarrow.lootboxes.lootdata.ItemData itemData = org.brokenarrow.lootboxes.lootdata.ItemData.getInstance();
	private final MenuTemplate guiTemplate;
	private final String lootTable;

	public EditCreateItems(final String lootTable) {
		super(LootItems.getInstance().getItems(lootTable));
		this.lootTable = lootTable;
		this.guiTemplate = Lootboxes.getInstance().getMenu("Edit_items_for_loot_table");
		if (guiTemplate != null) {
			setFillSpace(guiTemplate.getFillSlots());
			setMenuSize(guiTemplate.getinvSize("Edit_items_for_loot_table"));
			setTitle(() ->TranslatePlaceHolders.translatePlaceholders(guiTemplate.getMenuTitle(),""));
			setMenuOpenSound(guiTemplate.getSound());
		} else {
			setMenuSize(36);
			setTitle(() -> "could not load menu 'Edit_items_for_loot_table'.");
		}
	}

	@Override
	public MenuButton getFillButtonAt(@NotNull Object object) {
		MenuButtonData button = this.guiTemplate.getMenuButton(-1);
		if (button == null) return null;
		final Map<ItemStack, ItemData> cacheItemData = new HashMap<>();

		return new MenuButton() {
			@Override
			public void onClickInsideMenu(@NotNull final Player player, @NotNull final Inventory menu, @NotNull final ClickType click, @NotNull final ItemStack clickedItem, final Object object) {
				if (object instanceof String) {
					final ItemData itemData = cacheItemData.get(clickedItem);
					if (click.isRightClick())
						player.getInventory().addItem(itemData.getItemStack());
					else
						new CustomizeItem(lootTable, itemData.getItemPathKey()).menuOpen(player);
				}
			}

			@Override
			public ItemStack getItem() {

				org.broken.arrow.menu.button.manager.library.utility.MenuButton menuButton = button.getPassiveButton();

				if (object instanceof String) {
					final LootData data = lootItems.getCachedTableContents(lootTable).get(object);
					if (data != null) {
						final ItemStack itemStack;
						if (data.isHaveMetadata()) {
							itemStack = itemData.getCacheItemData(data.getItemdataFileName(), data.getItemdataPath());
						} else
							itemStack = new ItemStack(data.getMaterial());
						if (itemStack == null) return null;
						final ItemStack clonedItem = itemStack.clone();
						//final GuiTempletsYaml gui = guiTemplets.menuKey("Item_List").placeholders(
						Object[] placeholders = getPlaceholders("",
								data.isHaveMetadata() && clonedItem.hasItemMeta() && clonedItem.getItemMeta().hasDisplayName() ? clonedItem.getItemMeta().getDisplayName() : bountifyCapitalized(clonedItem.getType()),
								data.getChance(),
								data.getMinimum(),
								data.getMaximum(),
								data.isHaveMetadata());
						String displayName = TranslatePlaceHolders.translatePlaceholders(player, menuButton.getDisplayName(), placeholders);
						final ItemMeta itemMeta = clonedItem.getItemMeta();
						clonedItem.setItemMeta(itemMeta);

						final ItemStack guiItem = CreateItemUtily.of(clonedItem,
										displayName,
										TranslatePlaceHolders.translatePlaceholdersLore(player, menuButton.getLore(),placeholders ))
								.setShowEnchantments(true).makeItemStack();
						cacheItemData.put(guiItem, new ItemData(itemStack, (String) object));
						return guiItem;
					}
				}
				if (object instanceof ItemStack) {
					final ItemStack item = ((ItemStack) object);
					if (cacheItemData.get(item) == null) return null;
					return item;
				}
				return null;
			}
		};
		//return listOfItems;
	}

	@Override
	public MenuButton getButtonAt(int slot) {
		MenuButtonData button = this.guiTemplate.getMenuButton(slot);
		if (button == null) return null;
		return new MenuButton() {
			@Override
			public void onClickInsideMenu(@NotNull final Player player, @NotNull final Inventory menu, @NotNull final ClickType click, @NotNull final ItemStack clickedItem, final Object object) {
				if (run(button, click))
					updateButton(this);
			}

			@Override
			public ItemStack getItem() {
				org.broken.arrow.menu.button.manager.library.utility.MenuButton menuButton = button.getPassiveButton();

				return CreateItemUtily.of(menuButton.getMaterial(),
								TranslatePlaceHolders.translatePlaceholders(player, menuButton.getDisplayName()),
								TranslatePlaceHolders.translatePlaceholdersLore(player, menuButton.getLore()))
						.setGlow(menuButton.isGlow())
						.makeItemStack();
			}
		};
	}

	public boolean run(MenuButtonData button, ClickType click) {

		if (button.isActionTypeEqual("Save_items")) {
			new SaveItems(lootTable).menuOpen(player);
		}
		if (button.isActionTypeEqual("Forward_button")) {
			if (click.isLeftClick()) {
				nextPage();
			}
		}
		if (button.isActionTypeEqual("Previous_button")) {
			if (click.isLeftClick()) {
				previousPage();
			}
		}
		if (button.isActionTypeEqual("Search")) {}

		if (button.isActionTypeEqual("Back_button")) {
			new EditCreateLootTable().menuOpen(player);
		}
		return false;
	}

	public static class ItemData {
		private final ItemStack itemStack;
		private final String itemPathKey;

		public ItemData(final ItemStack itemStack, final String itemPathKey) {
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
