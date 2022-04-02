package org.brokenarrow.lootboxes.menus;

import org.brokenarrow.lootboxes.builder.ContainerDataBuilder;
import org.brokenarrow.lootboxes.builder.GuiTempletsYaml;
import org.brokenarrow.lootboxes.lootdata.ContainerData;
import org.brokenarrow.lootboxes.lootdata.LootItems;
import org.brokenarrow.lootboxes.untlity.CreateItemUtily;
import org.brokenarrow.menu.library.MenuButton;
import org.brokenarrow.menu.library.MenuHolder;
import org.brokenarrow.menu.library.NMS.UpdateTittleContainers;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ContainersLinkedList extends MenuHolder {

	private final MenuButton backButton;
	private final MenuButton seachButton;
	private final MenuButton forward;
	private final MenuButton previous;
	private final MenuButton itemList;
	private final GuiTempletsYaml.Builder guiTemplets;
	private final LootItems lootItems = LootItems.getInstance();
	private final ContainerData containerData = ContainerData.getInstance();

	public ContainersLinkedList(String container, String itemsToSearchFor) {
		super(List.of(ContainerData.getInstance().getCacheContainerData(container).getLinkedContainerData().keySet().toArray()));
		this.guiTemplets = new GuiTempletsYaml.Builder(getViewer(), "Container_Linked_List").placeholders(getPageNumber());
		setMenuSize(guiTemplets.build().getGuiSize());
		setTitle(guiTemplets.build().getGuiTitle());
		setFillSpace(guiTemplets.build().getFillSpace());

		seachButton = new MenuButton() {
			@Override
			public void onClickInsideMenu(Player player, Inventory inventory, ClickType clickType, ItemStack itemStack, Object o) {


			/*	if (clickType.isLeftClick())
					new SeachForItem(container, "").start(player);
				else
					new ContainersLinkedList(container, "").menuOpen(player);*/
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

				if (o instanceof Location) {

					ContainerDataBuilder containerDataBuilder = containerData.getCacheContainerData(container);
					if (containerDataBuilder != null) {
						ContainerDataBuilder.Builder builder = containerDataBuilder.getBuilder();
						Map<Location, ContainerDataBuilder.ContainerData> containerDataMap = containerDataBuilder.getLinkedContainerData();
						if (containerDataMap == null)
							containerDataMap = new HashMap<>();
						if (clickType.isRightClick()) {
							containerDataMap.remove(o);

							builder.setContainerData(containerDataMap);
							containerData.setContainerData(container, builder.build());
						}
					}
				}
			}

			@Override
			public ItemStack getItem() {
				return null;
			}

			@Override
			public ItemStack getItem(Object object) {

			/*	ItemStack itemstack = null;
				if (object instanceof Material)
					itemstack = new ItemStack((Material) object);
				if (object instanceof ItemStack)
					itemstack = (ItemStack) object;
				if (itemstack == null)
					return null;*/
				GuiTempletsYaml gui = guiTemplets.menuKey("Container_list").placeholders(object, object).build();
				return CreateItemUtily.of(gui.getIcon(), gui.getDisplayName(),
						gui.getLore()).makeItemStack();
			}
		};
		previous = new MenuButton() {
			@Override
			public void onClickInsideMenu(final Player player, final Inventory menu, final ClickType click, final ItemStack clickedItem, final Object object) {

				if (click.isLeftClick()) {
					previousPage();
				}

				UpdateTittleContainers.update(player, guiTemplets.build().getGuiTitle("Container_Linked_List", getPageNumber()), Material.CHEST, getMenu().getSize());
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
				UpdateTittleContainers.update(player, guiTemplets.build().getGuiTitle("Container_Linked_List", getPageNumber()), Material.CHEST, getMenu().getSize());
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
				new ModifyContinerData.AlterContainerDataMenu(container).menuOpen(player);
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
