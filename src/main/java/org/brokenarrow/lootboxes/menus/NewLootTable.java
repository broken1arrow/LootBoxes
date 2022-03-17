package org.brokenarrow.lootboxes.menus;

import org.brokenarrow.lootboxes.commandprompt.testprompt;
import org.brokenarrow.lootboxes.lootdata.LootItems;
import org.brokenarrow.menu.library.MenuButton;
import org.brokenarrow.menu.library.MenuHolder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

public class NewLootTable extends MenuHolder {

	private final LootItems lootItems = LootItems.getInstance();
	private final MenuButton createTable;
	private final MenuButton backButton;

	public NewLootTable() {
		setMenuSize(45);
		createTable = new MenuButton() {
			@Override
			public void onClickInsideMenu(Player player, Inventory menu, ClickType click, ItemStack clickedItem, Object object) {
//new SimpleConversation();
				new testprompt().start(player);
			}

			@Override
			public ItemStack getItem() {
				return null;
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

	}

	@Override
	public ItemStack getItemAt(int slot) {
		if (slot == 40)
			return createTable.getItem();
		if (slot == 44)
			return backButton.getItem();
		return null;
	}

	public class ConfirmIfItemHaveMetadata extends MenuHolder {

		private final MenuButton confirmSave;

		public ConfirmIfItemHaveMetadata(Map<Integer, ItemStack> item) {
			setMenuSize(5);
			confirmSave = new MenuButton() {
				@Override
				public void onClickInsideMenu(Player player, Inventory menu, ClickType click, ItemStack clickedItem, Object object) {

				}

				@Override
				public ItemStack getItem() {
					return null;
				}
			};

		}

		@Override
		public ItemStack getItemAt(int slot) {
			if (slot == 40)
				return confirmSave.getItem();

			return null;
		}
	}

}
