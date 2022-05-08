package org.brokenarrow.lootboxes.menus;

import org.brokenarrow.lootboxes.Lootboxes;
import org.brokenarrow.lootboxes.builder.ContainerDataBuilder;
import org.brokenarrow.lootboxes.builder.GuiTempletsYaml;
import org.brokenarrow.lootboxes.commandprompt.SeachForItem;
import org.brokenarrow.lootboxes.lootdata.ContainerDataCache;
import org.brokenarrow.lootboxes.lootdata.KeysToSave;
import org.brokenarrow.lootboxes.lootdata.LootItems;
import org.brokenarrow.lootboxes.untlity.CreateItemUtily;
import org.brokenarrow.lootboxes.untlity.LootDataSave;
import org.brokenarrow.menu.library.MenuButton;
import org.brokenarrow.menu.library.MenuHolder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class MatrialList extends MenuHolder {

	private final MenuButton backButton;
	private final MenuButton seachButton;
	private final MenuButton forward;
	private final MenuButton previous;
	private final MenuButton itemList;
	private final GuiTempletsYaml.Builder guiTemplets;
	private final LootItems lootItems = LootItems.getInstance();
	private final ContainerDataCache containerDataCache = ContainerDataCache.getInstance();

	public MatrialList(MenuKeys menuKey, String value, String container, String itemsToSearchFor) {
		super(Lootboxes.getInstance().getMatrialList().getMatrials(itemsToSearchFor));
		this.guiTemplets = new GuiTempletsYaml.Builder(getViewer(), "Matrial_List").placeholders("");
		setMenuSize(guiTemplets.build().getGuiSize());
		setTitle(guiTemplets.build().getGuiTitle());
		setFillSpace(guiTemplets.build().getFillSpace());

		seachButton = new MenuButton() {
			@Override
			public void onClickInsideMenu(Player player, Inventory inventory, ClickType clickType, ItemStack itemStack, Object o) {

				if (clickType.isLeftClick())
					new SeachForItem(MenuKeys.MATRIALLIST_MENU, menuKey, container, value).start(player);
				else
					new MatrialList(menuKey, value, container, "").menuOpen(player);
			}

			@Override
			public ItemStack getItem() {
				GuiTempletsYaml gui = guiTemplets.menuKey("Seach_button").build();

				return CreateItemUtily.of(gui.getIcon(), gui.getDisplayName(),
						gui.getLore()).makeItemStack();
			}
		};

		itemList = new MenuButton() {
			@Override
			public void onClickInsideMenu(Player player, Inventory inventory, ClickType clickType, ItemStack itemStack, Object o) {
				if (o instanceof Material) {

					if (menuKey == MenuKeys.ALTER_CONTAINER_DATA_MENU) {
						ContainerDataBuilder containerDataBuilder = containerDataCache.getCacheContainerData(container);
						if (containerDataBuilder != null) {
							ContainerDataBuilder.Builder builder = containerDataBuilder.getBuilder();
							builder.setIcon((Material) o);
							containerDataCache.setContainerData(container, builder.build());
							new ModifyContinerData.AlterContainerDataMenu(container).menuOpen(player);
						}
					}
					if (menuKey == MenuKeys.EDIT_KEYS_FOR_OPEN_MENU) {
						containerDataCache.setKeyData(KeysToSave.ITEM_TYPE, o, container, value);
						new EditKeysToOpen.EditKey(container, value).menuOpen(player);

					}
					if (menuKey == MenuKeys.CUSTOMIZEITEM_MENU) {
						lootItems.setLootData(LootDataSave.ITEM, container, value, o);
						new CustomizeItem(container, value).menuOpen(player);
					}
				}
			}

			@Override
			public ItemStack getItem() {
				return null;
			}

			@Override
			public ItemStack getItem(Object object) {

				ItemStack itemstack = null;
				if (object instanceof Material)
					itemstack = new ItemStack((Material) object);
				if (object instanceof ItemStack)
					itemstack = (ItemStack) object;
				if (itemstack == null)
					return null;
				GuiTempletsYaml gui = guiTemplets.menuKey("Item_list").placeholders("", itemstack.getType()).build();
				return CreateItemUtily.of(itemstack, gui.getDisplayName(),
						gui.getLore()).makeItemStack();
			}
		};
		previous = new MenuButton() {
			@Override
			public void onClickInsideMenu(final Player player, final Inventory menu, final ClickType click, final ItemStack clickedItem, final Object object) {

				if (click.isLeftClick()) {
					previousPage();
				}

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

			}

			@Override
			public ItemStack getItem() {
				GuiTempletsYaml gui = guiTemplets.menuKey("Forward_button").build();
				return CreateItemUtily.of(gui.getIcon(), gui.getDisplayName(),
						gui.getLore()).makeItemStack();
			}
		};
		backButton = new MenuButton() {
			@Override
			public void onClickInsideMenu(Player player, Inventory inventory, ClickType clickType, ItemStack itemStack, Object o) {
				if (menuKey == MenuKeys.ALTER_CONTAINER_DATA_MENU)
					new ModifyContinerData.AlterContainerDataMenu(container).menuOpen(player);
				if (menuKey == MenuKeys.EDIT_KEYS_FOR_OPEN_MENU)
					new EditKeysToOpen.EditKey(container, value).menuOpen(player);
				if (menuKey == MenuKeys.CUSTOMIZEITEM_MENU) {
					new CustomizeItem(container, value).menuOpen(player);
				}
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
	public MenuButton getFillButtonAt(Object o) {
		return itemList;
	}

	@Override
	public MenuButton getButtonAt(int slot) {

		if (guiTemplets.menuKey("Seach_button").build().getSlot().contains(slot))
			return seachButton;
		if (guiTemplets.menuKey("Forward_button").build().getSlot().contains(slot))
			return forward;
		if (guiTemplets.menuKey("Previous_button").build().getSlot().contains(slot))
			return previous;
		if (guiTemplets.menuKey("Back_button").build().getSlot().contains(slot))
			return backButton;

		return null;
	}
}