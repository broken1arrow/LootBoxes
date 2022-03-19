package org.brokenarrow.lootboxes.menus;

import org.brokenarrow.lootboxes.builder.GuiTempletsYaml;
import org.brokenarrow.lootboxes.lootdata.LootItems;
import org.brokenarrow.lootboxes.untlity.CreateItemUtily;
import org.brokenarrow.menu.library.MenuButton;
import org.brokenarrow.menu.library.MenuHolder;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class CustomizeItem extends MenuHolder {

	private final MenuButton backButton;
	private final MenuButton removeButton;
	private final GuiTempletsYaml.Builder guiTemplets;

	public CustomizeItem(String lootTable, String itemToEdit) {

		this.guiTemplets = new GuiTempletsYaml.Builder(getViewer(), "Edit_Items_For_LootTable");

		setMenuSize(guiTemplets.build().getGuiSize());
		setTitle(guiTemplets.build().getGuiTitle());
		setFillSpace(guiTemplets.build().getFillSpace());

		removeButton = new MenuButton() {

			@Override
			public void onClickInsideMenu(Player player, Inventory menu, ClickType click, ItemStack clickedItem, Object object) {
				LootItems.getInstance().getLootData(lootTable, itemToEdit);
			}

			@Override
			public ItemStack getItem() {
				GuiTempletsYaml gui = guiTemplets.menuKey("Back_button").build();

				return CreateItemUtily.of(gui.getIcon(),
						gui.getDisplayName(),
						gui.getLore()).makeItemStack();
			}
		};


		backButton = new MenuButton() {

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

		if (guiTemplets.menuKey("Back_button").build().getSlot().contains(slot))
			return backButton.getItem();

		return null;
	}
}
