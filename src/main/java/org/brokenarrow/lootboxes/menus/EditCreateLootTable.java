package org.brokenarrow.lootboxes.menus;

import org.brokenarrow.lootboxes.builder.GuiTempletsYaml;
import org.brokenarrow.lootboxes.commandprompt.testprompt;
import org.brokenarrow.lootboxes.lootdata.LootItems;
import org.brokenarrow.lootboxes.untlity.CreateItemUtily;
import org.brokenarrow.menu.library.MenuButton;
import org.brokenarrow.menu.library.MenuHolder;
import org.brokenarrow.menu.library.NMS.UpdateTittleContainers;
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
	private final MenuButton forward;
	private final MenuButton previous;
	private final GuiTempletsYaml.Builder guiTemplets;
	Map<ItemStack, ItemStack> data = new HashMap<>();

	public EditCreateLootTable() {
		super(new ArrayList<>(LootItems.getInstance().getCachedLoot().keySet()));
		guiTemplets = new GuiTempletsYaml.Builder(getViewer(), "LootTables").placeholders(getPageNumber());

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
		
				if (object instanceof String) {
				/*	ItemStack itemStack = data.get(object);
					ItemMeta itemMeta = itemStack.getItemMeta();
					if (itemMeta != null && itemMeta.hasDisplayName())*/
					new EditCreateItems((String) object).menuOpen(player);
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
		previous = new MenuButton() {
			@Override
			public void onClickInsideMenu(final Player player, final Inventory menu, final ClickType click, final ItemStack clickedItem, final Object object) {

				if (click.isLeftClick()) {
					previousPage();
				}

				UpdateTittleContainers.update(player, guiTemplets.build().getGuiTitle("LootTables", getPageNumber()), Material.CHEST, getMenu().getSize());
				updateButtons();
			}

			@Override
			public ItemStack getItem() {
				GuiTempletsYaml gui = guiTemplets.menuKey("Previous_button").build();

				return CreateItemUtily.of(gui.getIcon(), gui.getDisplayName(),
						gui.getLore()).makeItemStack();
			}
		};
		forward = new MenuButton() {
			@Override
			public void onClickInsideMenu(final Player player, final Inventory menu, final ClickType click, final ItemStack clickedItem, final Object object) {
				if (click.isLeftClick()) {
					nextPage();
				}
				UpdateTittleContainers.update(player, guiTemplets.build().getGuiTitle("LootTables", getPageNumber()), Material.CHEST, getMenu().getSize());
				updateButtons();
			}

			@Override
			public ItemStack getItem() {
				GuiTempletsYaml gui = guiTemplets.menuKey("Forward_button").build();
				return CreateItemUtily.of(gui.getIcon(), gui.getDisplayName(),
						gui.getLore()).makeItemStack();
			}
		};
	}

	@Override
	public ItemStack getFillItemsAt(Object o) {
		return listOfTables.getItem(o);

	}

	@Override
	public ItemStack getItemAt(int slot) {

		if (guiTemplets.menuKey("Forward_button").build().getSlot().contains(slot))
			return forward.getItem();
		if (guiTemplets.menuKey("Previous_button").build().getSlot().contains(slot))
			return previous.getItem();
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
