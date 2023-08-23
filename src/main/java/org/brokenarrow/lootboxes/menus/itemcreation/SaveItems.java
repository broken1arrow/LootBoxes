package org.brokenarrow.lootboxes.menus.itemcreation;

import org.broken.arrow.menu.button.manager.library.utility.MenuButtonData;
import org.broken.arrow.menu.button.manager.library.utility.MenuTemplate;
import org.broken.arrow.menu.library.button.MenuButton;
import org.broken.arrow.menu.library.holder.MenuHolder;
import org.brokenarrow.lootboxes.Lootboxes;
import org.brokenarrow.lootboxes.lootdata.ItemData;
import org.brokenarrow.lootboxes.lootdata.LootItems;
import org.brokenarrow.lootboxes.settings.Settings;
import org.brokenarrow.lootboxes.untlity.CreateItemUtily;
import org.brokenarrow.lootboxes.untlity.TranslatePlaceHolders;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class SaveItems extends MenuHolder {
	private final LootItems lootItems = LootItems.getInstance();
	private final ItemData itemData = ItemData.getInstance();
	private final Settings settings = Lootboxes.getInstance().getSettings();
	private final MenuTemplate guiTemplate;
	private final String lootTable;

	public SaveItems(final String lootTable) {
		this.lootTable = lootTable;

		this.guiTemplate = Lootboxes.getInstance().getMenu("Save_items");
		if (guiTemplate != null) {
			setFillSpace(guiTemplate.getFillSlots());
			setMenuSize(guiTemplate.getinvSize("Save_items"));
			setTitle(() ->TranslatePlaceHolders.translatePlaceholders(guiTemplate.getMenuTitle(),""));
			setMenuOpenSound(guiTemplate.getSound());
		} else {
			setMenuSize(36);
			setTitle(() -> "could not load menu 'Save_items'.");
		}
		setSlotsYouCanAddItems(true);
	}

	@Override
	public MenuButton getButtonAt(int slot) {
		MenuButtonData button = this.guiTemplate.getMenuButton(slot);
		if (button == null) return null;
		return new MenuButton() {
			@Override
			public void onClickInsideMenu(@NotNull final Player player, @NotNull final Inventory menu, @NotNull final ClickType click, @NotNull final ItemStack clickedItem, final Object object) {
				if (run(button,menu, click))
					updateButton(this);
			}

			@Override
			public ItemStack getItem() {
				org.broken.arrow.menu.button.manager.library.utility.MenuButton menuButton = button.getPassiveButton();

				return CreateItemUtily.of(menuButton.getMaterial(),
								TranslatePlaceHolders.translatePlaceholders(player, menuButton.getDisplayName()),
								TranslatePlaceHolders.translatePlaceholdersLore(player, menuButton.getLore()))
						.makeItemStack();
			}
		};
	}

	public boolean run(MenuButtonData button,Inventory menu, ClickType click) {

		if (button.isActionTypeEqual("Save_items")) {
			//todo change to this? getCheckItemsInsideMenu()..getItemsFromSetSlots(
			final Map<Integer, ItemStack> items = Lootboxes.getInstance().getMenuApi().getCheckItemsInsideInventory().getItemsFromSetSlots(menu, null, false);
			if (items == null || items.isEmpty()) return false;

			for (final ItemStack item : items.values()) {
				if (item == null) continue;
				String fileName = "";
				if (item.hasItemMeta() && settings.getSettingsData().isSaveMetadataOnItem()) {
					if (settings.getSettingsData().isWarnBeforeSaveWithMetadata()) {
						new ConfirmIfItemHaveMetadata(items, lootTable).menuOpen(player);
						return false;
					} else {
						fileName = itemData.setCacheItemData(lootTable, item.getType() + "", item);
					}
				}
				lootItems.addItems(lootTable, item, lootTable, fileName, !fileName.isEmpty());
			}
			lootItems.saveTask(lootTable);
			new EditCreateItems(lootTable).menuOpen(player);
		}

		if (button.isActionTypeEqual("Back_button")) {
			new EditCreateItems(lootTable).menuOpen(player);
		}
		return false;
	}

}
