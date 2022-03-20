package org.brokenarrow.lootboxes.menus;

import org.brokenarrow.lootboxes.builder.GuiTempletsYaml;
import org.brokenarrow.lootboxes.lootdata.ItemData;
import org.brokenarrow.lootboxes.lootdata.LootItems;
import org.brokenarrow.lootboxes.untlity.CreateItemUtily;
import org.brokenarrow.menu.library.MenuButton;
import org.brokenarrow.menu.library.MenuHolder;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Iterator;

public class MainMenu extends MenuHolder {

	private final MenuButton editAndCreateTable;
	private final MenuButton defultSettings;
	private final LootItems lootItems = LootItems.getInstance();
	private final ItemData itemData = ItemData.getInstance();
	private final GuiTempletsYaml.Builder guiTemplets;

	public MainMenu() {
		guiTemplets = new GuiTempletsYaml.Builder(getViewer(), "Main_Menu");

		setMenuSize(guiTemplets.build().getGuiSize());
		setTitle(guiTemplets.build().getGuiTitle());

		editAndCreateTable = new MenuButton() {
			@Override
			public void onClickInsideMenu(Player player, Inventory menu, ClickType click, ItemStack clickedItem, Object object) {
				new EditCreateLootTable().menuOpen(player);
			}

			@Override
			public ItemStack getItem() {
				GuiTempletsYaml gui = guiTemplets.menuKey("Edit_LootTable").build();

				return CreateItemUtily.of(gui.getIcon(),
						gui.getDisplayName(),
						gui.getLore()).makeItemStack();
			}
		};
		defultSettings = new MenuButton() {
			@Override
			public void onClickInsideMenu(Player player, Inventory menu, ClickType click, ItemStack clickedItem, Object object) {

			}

			@Override
			public ItemStack getItem() {
				GuiTempletsYaml gui = guiTemplets.menuKey("Containers").build();

				return CreateItemUtily.of(gui.getIcon(),
						gui.getDisplayName(),
						gui.getLore()).makeItemStack();
			}
		};

	}

	@Override
	public ItemStack getItemAt(int slot) {

		if (guiTemplets.menuKey("Edit_LootTable").build().getSlot().contains(slot))
			return editAndCreateTable.getItem();

		if (guiTemplets.menuKey("Containers").build().getSlot().contains(slot))
			return defultSettings.getItem();

		return null;
	}


	public ItemStack convertObjectToItemstack(Iterator<?> iterator, Class<?> clazz, Object object) {

		clazz.cast(object);
		return null;
	}
}
