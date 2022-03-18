package org.brokenarrow.lootboxes.menus;

import org.brokenarrow.lootboxes.builder.GuiTempletsYaml;
import org.brokenarrow.lootboxes.commandprompt.testprompt;
import org.brokenarrow.lootboxes.lootdata.LootItems;
import org.brokenarrow.lootboxes.untlity.CreateItemUtily;
import org.brokenarrow.menu.library.MenuButton;
import org.brokenarrow.menu.library.MenuHolder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class EditCreateLootTable extends MenuHolder {

	private final LootItems lootItems = LootItems.getInstance();
	private final MenuButton createTable;
	private final MenuButton backButton;
	private final MenuButton newTable;
	private final MenuButton listOfTables;
	private final GuiTempletsYaml.Builder guiTemplets;
	Map<ItemStack, ItemStack> data = new HashMap<>();

	public EditCreateLootTable() {
		super(new ArrayList<>(LootItems.getInstance().getSettings().keySet()));
		guiTemplets = new GuiTempletsYaml.Builder(getViewer(), "LootTables");

		setMenuSize(guiTemplets.build().getGuiSize());
		setTitle(guiTemplets.build().getGuiTitle());
		setFillSpace(guiTemplets.build().getFillSpace());
		createTable = new MenuButton() {
			@Override
			public void onClickInsideMenu(Player player, Inventory menu, ClickType click, ItemStack clickedItem, Object object) {
//new SimpleConversation();
				new testprompt().start(player);
			}

			@Override
			public ItemStack getItem() {
				GuiTempletsYaml gui = guiTemplets.menuKey("Create_Table").build();

				return CreateItemUtily.of(gui.getIcon(),
						gui.getDisplayName(),
						gui.getLore()).makeItemStack();
			}
		};

		newTable = new MenuButton() {
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
				new MainMenu().menuOpen(player);
			}

			@Override
			public ItemStack getItem() {
				GuiTempletsYaml gui = guiTemplets.menuKey("Back_button").build();

				return CreateItemUtily.of(gui.getIcon(),
						gui.getDisplayName(),
						gui.getLore()).makeItemStack();
			}
		};
		listOfTables = new MenuButton() {
			@Override
			public void onClickInsideMenu(Player player, Inventory menu, ClickType click, ItemStack clickedItem, Object object) {
				if (object instanceof ItemStack) {
					ItemStack itemStack = data.get(object);
					ItemMeta itemMeta = itemStack.getItemMeta();
					if (itemMeta != null && itemMeta.hasDisplayName())
						new EditCreateItems(itemMeta.getDisplayName()).menuOpen(player);
				}
			}

			@Override
			public ItemStack getItem() {
				return null;
			}

			@Override
			public ItemStack getItem(Object object) {

				if (object instanceof String) {
					if (object.equals("Global_Values")) return null;
					ItemStack itemStack = new ItemStack(Material.PAPER);
					ItemMeta itemMeta = itemStack.getItemMeta();
					itemMeta.setDisplayName((String) object);
					itemStack.setItemMeta(itemMeta);
					data.put(itemStack, itemStack);
					return itemStack;
				} else if (object instanceof ItemStack)
					return data.get(object);
				return null;
			}
		};
	}

	@Override
	public ItemStack getFillItemsAt(Object o) {
		return listOfTables.getItem(o);

	}

	@Override
	public ItemStack getItemAt(int slot) {

		if (guiTemplets.menuKey("Create_Table").build().getSlot().contains(slot))
			return createTable.getItem();
		if (guiTemplets.menuKey("Create_Table").build().getSlot().contains(slot))
			return newTable.getItem();
		if (guiTemplets.menuKey("Back_button").build().getSlot().contains(slot))
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
