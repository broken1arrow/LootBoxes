package org.brokenarrow.lootboxes.menus.itemcreation;

import org.broken.arrow.menu.button.manager.library.utility.MenuButtonData;
import org.broken.arrow.menu.button.manager.library.utility.MenuTemplate;
import org.broken.arrow.menu.library.button.MenuButton;
import org.broken.arrow.menu.library.holder.MenuHolder;
import org.brokenarrow.lootboxes.Lootboxes;
import org.brokenarrow.lootboxes.builder.GuiTempletsYaml;
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
	private GuiTempletsYaml.Builder guiTemplets;
	private final LootItems lootItems = LootItems.getInstance();
	private final ItemData itemData = ItemData.getInstance();
	private final MenuTemplate guiTemplate;
	private final Map<Integer, ItemStack> items;
	private final String lootTable;

	public ConfirmIfItemHaveMetadata(final Map<Integer, ItemStack> items, final String lootTable) {
		//guiTemplets = new GuiTempletsYaml.Builder(getViewer(), "Confirm_if_item_have_metadata");
		this.items = items;
		this.lootTable = lootTable;
		this.guiTemplate = Lootboxes.getInstance().getMenu("Confirm_if_item_have_metadata");
		if (guiTemplate != null) {
			setFillSpace(guiTemplate.getFillSlots());
			setMenuSize(guiTemplate.getinvSize("Confirm_if_item_have_metadata"));
			setTitle(guiTemplate::getMenuTitle);
			setMenuOpenSound(guiTemplate.getSound());
		} else {
			setMenuSize(36);
			setTitle(() -> "could not load menu 'Confirm_If_Item_Have_Metadata'.");
		}

		confirmSave = new MenuButton() {
			@Override
			public void onClickInsideMenu(final @NotNull Player player, final @NotNull Inventory menu, final @NotNull ClickType click, final @NotNull ItemStack clickedItem, final Object object) {
				for (final ItemStack item : items.values()) {
					String itemdataPath = "";
					if (item == null) continue;

					if (click.isLeftClick() && item.hasItemMeta()) {
						//lootItems. getSettings().get(lootTable).get()
						itemdataPath = itemData.setCacheItemData(lootTable, item.getType() + "", item);

					}
					lootItems.addItems(lootTable, item, itemData.getItemDataPath(lootTable), itemdataPath, !itemdataPath.isEmpty());
				}
				itemData.saveTask(lootTable);
				lootItems.saveTask(lootTable);
				new EditCreateItems(lootTable).menuOpen(player);
			}

			@Override
			public ItemStack getItem() {
				final GuiTempletsYaml gui = guiTemplets.menuKey("Confirm_Save").build();

				return CreateItemUtily.of(gui.getIcon(),
						gui.getDisplayName(),
						gui.getLore()).makeItemStack();
			}
		};
		this.backButton = new MenuButton() {

			@Override
			public void onClickInsideMenu(final @NotNull Player player, final @NotNull Inventory menu, final @NotNull ClickType click, final @NotNull ItemStack clickedItem, final Object object) {
				new EditCreateItems(lootTable).menuOpen(player);
			}

			@Override
			public ItemStack getItem() {
				final GuiTempletsYaml gui = guiTemplets.menuKey("Back_button").build();

				return CreateItemUtily.of(gui.getIcon(),
						gui.getDisplayName(),
						gui.getLore()).makeItemStack();
			}
		};
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
					//lootItems. getSettings().get(lootTable).get()
					itemDataPath = itemData.setCacheItemData(lootTable, item.getType() + "", item);

				}
				lootItems.addItems(lootTable, item, itemData.getItemDataPath(lootTable), itemDataPath, !itemDataPath.isEmpty());
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

	public MenuButton getButtonAts(final int slot) {

		if (guiTemplets.menuKey("Confirm_Save").build().getSlot().contains(slot))
			return confirmSave;

		if (guiTemplets.menuKey("Back_button").build().getSlot().contains(slot))
			return this.backButton;
		return null;
	}
}