package org.brokenarrow.lootboxes.menus;

import org.brokenarrow.lootboxes.Lootboxes;
import org.brokenarrow.lootboxes.builder.GuiTempletsYaml;
import org.brokenarrow.lootboxes.builder.LootData;
import org.brokenarrow.lootboxes.commandprompt.SeachForItem;
import org.brokenarrow.lootboxes.lootdata.LootItems;
import org.brokenarrow.lootboxes.untlity.CreateItemUtily;
import org.brokenarrow.lootboxes.untlity.LootDataSave;
import org.brokenarrow.menu.library.MenuButton;
import org.brokenarrow.menu.library.MenuHolder;
import org.brokenarrow.menu.library.NMS.UpdateTittleContainers;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class CustomizeItem extends MenuHolder {

	private final MenuButton backButton;
	private final MenuButton removeButton;
	private final MenuButton changeItem;
	private final GuiTempletsYaml.Builder guiTemplets;
	private final LootItems lootItems = LootItems.getInstance();

	public CustomizeItem(String lootTable, String itemToEdit) {

		this.guiTemplets = new GuiTempletsYaml.Builder(getViewer(), "CustomizeItem").placeholders(getPageNumber());

		setMenuSize(guiTemplets.build().getGuiSize());
		setTitle(guiTemplets.build().getGuiTitle());
		//setFillSpace(guiTemplets.build().getFillSpace());
		LootData data = lootItems.getLootData(lootTable, itemToEdit);
		changeItem = new MenuButton() {
			@Override
			public void onClickInsideMenu(Player player, Inventory inventory, ClickType clickType, ItemStack itemStack, Object o) {
				new ChangeItem(lootTable, itemToEdit, "").menuOpen(player);
			}

			@Override
			public ItemStack getItem() {
				GuiTempletsYaml gui = guiTemplets.menuKey("Change_Item").build();

				return CreateItemUtily.of(gui.getIcon(),
						gui.getDisplayName(),
						gui.getLore()).makeItemStack();
			}
		};

		removeButton = new MenuButton() {

			@Override
			public void onClickInsideMenu(Player player, Inventory menu, ClickType click, ItemStack clickedItem, Object object) {
				lootItems.getLootData(lootTable, itemToEdit);
				//lootItems.setLootData();
				lootItems.removeItem(lootTable, itemToEdit);
			}

			@Override
			public ItemStack getItem() {
				GuiTempletsYaml gui = guiTemplets.menuKey("Remove_Button").build();

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

		if (guiTemplets.menuKey("Change_Item").build().getSlot().contains(slot))
			return changeItem.getItem();
		if (guiTemplets.menuKey("Remove_Button").build().getSlot().contains(slot))
			return removeButton.getItem();
		if (guiTemplets.menuKey("Back_button").build().getSlot().contains(slot))
			return backButton.getItem();

		return null;
	}

	public static class ChangeItem extends MenuHolder {

		private final MenuButton backButton;
		private final MenuButton seachButton;
		private final MenuButton forward;
		private final MenuButton previous;
		private final MenuButton itemList;
		private final GuiTempletsYaml.Builder guiTemplets;
		private final LootItems lootItems = LootItems.getInstance();

		public ChangeItem(String lootTable, String itemToEdit, String itemsToSearchFor) {
			super(Lootboxes.getInstance().getMatrialList().getMatrials(itemsToSearchFor));
			this.guiTemplets = new GuiTempletsYaml.Builder(getViewer(), "ChangeItem").placeholders(getPageNumber());
			setMenuSize(guiTemplets.build().getGuiSize());
			setTitle(guiTemplets.build().getGuiTitle());
			setFillSpace(guiTemplets.build().getFillSpace());

			seachButton = new MenuButton() {
				@Override
				public void onClickInsideMenu(Player player, Inventory inventory, ClickType clickType, ItemStack itemStack, Object o) {

					if (clickType.isLeftClick())
						new SeachForItem(lootTable, itemToEdit).start(player);
					else
						new CustomizeItem.ChangeItem(lootTable, itemToEdit, "").menuOpen(player);
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

					if (o instanceof ItemStack) {
						Material material = ((ItemStack) o).getType();
						lootItems.setLootData(LootDataSave.ITEM, lootTable, itemToEdit, material);
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
					GuiTempletsYaml gui = guiTemplets.menuKey("Change_Item").placeholders("", itemstack.getType()).build();
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

					UpdateTittleContainers.update(player, guiTemplets.build().getGuiTitle("ChangeItem", getPageNumber()), Material.CHEST, getMenu().getSize());
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
					UpdateTittleContainers.update(player, guiTemplets.build().getGuiTitle("ChangeItem", getPageNumber()), Material.CHEST, getMenu().getSize());
					updateButtons();
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
					new CustomizeItem(lootTable, itemToEdit).menuOpen(player);
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

		public List<Material> getListOfMatrial() {
			return List.of(Material.values());
		}

		@Override
		public ItemStack getFillItemsAt(Object o) {
			return itemList.getItem(o);
		}

		@Override
		public ItemStack getItemAt(int slot) {
			if (guiTemplets.menuKey("Seach_button").build().getSlot().contains(slot))
				return seachButton.getItem();
			if (guiTemplets.menuKey("Forward_button").build().getSlot().contains(slot))
				return forward.getItem();
			if (guiTemplets.menuKey("Previous_button").build().getSlot().contains(slot))
				return previous.getItem();
			if (guiTemplets.menuKey("Back_button").build().getSlot().contains(slot))
				return backButton.getItem();

			return null;
		}
	}
}
