package org.brokenarrow.lootboxes.menus.itemcreation;

import org.broken.arrow.library.menu.button.manager.utility.MenuButtonData;
import org.broken.arrow.library.menu.button.manager.utility.MenuTemplate;
import org.broken.arrow.library.menu.button.MenuButton;
import org.broken.arrow.library.menu.holder.MenuHolder;
import org.brokenarrow.lootboxes.Lootboxes;
import org.brokenarrow.lootboxes.lootdata.ItemData;
import org.brokenarrow.lootboxes.lootdata.LootItems;
import org.brokenarrow.lootboxes.untlity.CreateItemUtily;
import org.brokenarrow.lootboxes.untlity.TranslatePlaceHolders;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class ConfirmIfItemHaveMetadata extends MenuHolder {

	private  MenuButton confirmSave;
	private MenuButton backButton;
	private final LootItems lootItems = LootItems.getInstance();
	private final ItemData itemData = ItemData.getInstance();
	private final MenuTemplate guiTemplate;
	private final Map<Integer, ItemStack> items;
	private final String lootTable;

	public ConfirmIfItemHaveMetadata(final Map<Integer, ItemStack> items, final String lootTable) {
		this.items = items;
		this.lootTable = lootTable;
		this.guiTemplate = Lootboxes.getInstance().getMenu("Confirm_if_item_have_metadata");

		setUseColorConversion(true);
		setIgnoreItemCheck(true);

		if (guiTemplate != null) {
			setFillSpace(guiTemplate.getFillSlots());
			setMenuSize(guiTemplate.getinvSize("Confirm_if_item_have_metadata"));
			setTitle(() ->TranslatePlaceHolders.translatePlaceholders(guiTemplate.getMenuTitle(),""));
			setMenuOpenSound(guiTemplate.getSound());
			this.setUseColorConversion(true);
		} else {
			setMenuSize(36);
			setTitle(() -> "could not load menu 'Confirm_If_Item_Have_Metadata'.");
		}

	}


	@Override
	public MenuButton getButtonAt(int slot) {
		MenuButtonData button = this.guiTemplate.getMenuButton(slot);
		if (button == null) return null;
		return new MenuButton() {
			@Override
			public void onClickInsideMenu(@NotNull final Player player, @NotNull final Inventory menu, @NotNull final ClickType click, @NotNull final ItemStack clickedItem) {
				if (run(button, click))
					updateButton(this);
			}

			@Override
			public ItemStack getItem() {
				org.broken.arrow.library.menu.button.manager.utility.MenuButton menuButton = button.getPassiveButton();

				return CreateItemUtily.of(menuButton.isGlow(),menuButton.getMaterial(),
								TranslatePlaceHolders.translatePlaceholders(player, menuButton.getDisplayName()),
								TranslatePlaceHolders.translatePlaceholdersLore(player, menuButton.getLore()))
						.makeItemStack();
			}
		};
	}

	public boolean run(MenuButtonData button, ClickType click) {

		if (button.isActionTypeEqual("Confirm_save")) {
			for (final ItemStack item : items.values()) {
				String itemDataPath = "";
				if (item == null) continue;

				if (click.isLeftClick() && item.hasItemMeta()) {
					//final LootData data = lootItems.getLootData(lootTable, item.getType() + "");
					itemDataPath = itemData.setCacheItemData(	lootTable, item.getType() + "", item);
				}
				lootItems.addItems(lootTable, item, lootTable, itemDataPath, !itemDataPath.isEmpty());
			}
			itemData.saveTask(lootTable);
			lootItems.saveTask(lootTable);
			new EditCreateItems(lootTable).menuOpen(player);
		}

		if (button.isActionTypeEqual("Back_button")) {
			new EditCreateItems(lootTable).menuOpen(player);
		}
		return false;
	}

}