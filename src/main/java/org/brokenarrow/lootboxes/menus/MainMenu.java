package org.brokenarrow.lootboxes.menus;

import org.broken.arrow.menu.library.button.MenuButton;
import org.broken.arrow.menu.library.holder.MenuHolder;
import org.brokenarrow.lootboxes.builder.GuiTempletsYaml;
import org.brokenarrow.lootboxes.lootdata.ItemData;
import org.brokenarrow.lootboxes.lootdata.LootItems;
import org.brokenarrow.lootboxes.untlity.CreateItemUtily;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

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
			public void onClickInsideMenu(@NotNull Player player, @NotNull Inventory menu, @NotNull ClickType click, @NotNull ItemStack clickedItem, Object object) {
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
			public void onClickInsideMenu(@NotNull Player player, @NotNull Inventory menu, @NotNull ClickType click, @NotNull ItemStack clickedItem, Object object) {
				new ModifyContinerData().menuOpen(player);
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
	public MenuButton getButtonAt(int slot) {

		if (guiTemplets.menuKey("Edit_LootTable").build().getSlot().contains(slot))
			return editAndCreateTable;

		if (guiTemplets.menuKey("Containers").build().getSlot().contains(slot))
			return defultSettings;

		return null;
	}

}
