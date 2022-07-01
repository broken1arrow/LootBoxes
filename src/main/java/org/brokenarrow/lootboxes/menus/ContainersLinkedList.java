package org.brokenarrow.lootboxes.menus;

import org.brokenarrow.lootboxes.builder.ContainerDataBuilder;
import org.brokenarrow.lootboxes.builder.GuiTempletsYaml;
import org.brokenarrow.lootboxes.lootdata.ContainerDataCache;
import org.brokenarrow.lootboxes.lootdata.LootItems;
import org.brokenarrow.lootboxes.untlity.CreateItemUtily;
import org.brokenarrow.lootboxes.untlity.TeleportPlayer;
import org.brokenarrow.menu.library.MenuButton;
import org.brokenarrow.menu.library.MenuHolder;
import org.brokenarrow.menu.library.NMS.UpdateTittleContainers;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.brokenarrow.lootboxes.settings.ChatMessages.*;

public class ContainersLinkedList extends MenuHolder {

	private final MenuButton backButton;
	private final MenuButton seachButton;
	private final MenuButton forward;
	private final MenuButton previous;
	private final MenuButton itemList;
	private final GuiTempletsYaml.Builder guiTemplets;
	private final LootItems lootItems = LootItems.getInstance();
	private final ContainerDataCache containerDataCache = ContainerDataCache.getInstance();

	public ContainersLinkedList(final String container, final String itemsToSearchFor) {
		super(Arrays.asList(ContainerDataCache.getInstance().getLinkedContainerData(container).keySet().toArray()));
		this.guiTemplets = new GuiTempletsYaml.Builder(getViewer(), "Container_Linked_List").placeholders("");
		setMenuSize(guiTemplets.build().getGuiSize());
		setTitle(guiTemplets.build().getGuiTitle());
		setFillSpace(guiTemplets.build().getFillSpace());

		seachButton = new MenuButton() {
			@Override
			public void onClickInsideMenu(final Player player, final Inventory inventory, final ClickType clickType, final ItemStack itemStack, final Object o) {


			/*	if (clickType.isLeftClick())
					new SeachInMenu(container, "").start(player);
				else
					new ContainersLinkedList(container, "").menuOpen(player);*/
			}

			@Override
			public ItemStack getItem() {
				final GuiTempletsYaml gui = guiTemplets.menuKey("Seach_button").build();

				return CreateItemUtily.of(gui.getIcon(), gui.getDisplayName(),
						gui.getLore()).makeItemStack();
			}
		};

		itemList = new MenuButton() {
			@Override
			public void onClickInsideMenu(final Player player, final Inventory inventory, final ClickType clickType, final ItemStack itemStack, final Object o) {

				if (o instanceof Location) {

					final ContainerDataBuilder containerDataBuilder = containerDataCache.getCacheContainerData(container);
					if (containerDataBuilder != null) {
						final ContainerDataBuilder.Builder builder = containerDataBuilder.getBuilder();
						Map<Location, org.brokenarrow.lootboxes.builder.ContainerData> containerDataMap = containerDataBuilder.getLinkedContainerData();
						if (containerDataMap == null)
							containerDataMap = new HashMap<>();
						if (clickType.isRightClick()) {
							containerDataMap.remove(o);

							builder.setContainerData(containerDataMap);
							containerDataCache.setContainerData(container, builder.build());
						}
						if (clickType.isLeftClick()) {
							final TeleportPlayer teleport = new TeleportPlayer(player, (Location) o);
							if (teleport.teleportPlayer()) {
								final Location teleportLoc = teleport.getFinalTeleportLoc();

								if (teleportLoc.getZ() - 0.5 == ((Location) o).getZ() && teleportLoc.getX() - 0.5 == ((Location) o).getX())
									CONTINER_IS_NOT_OBSTACLE.sendMessage(player, teleportLoc.getWorld().getName(), teleportLoc.getX(), teleportLoc.getY(), teleportLoc.getZ());
								else if (teleportLoc.equals(o))
									CONTINER_IS_OBSTACLE_ON_ALL_SIDES.sendMessage(player, teleportLoc.getWorld().getName(), teleportLoc.getX() - 0.5, teleportLoc.getY(), teleportLoc.getZ() - 0.5);
								else
									CONTINER_IS_OBSTACLE_ON_SOME_SIDES.sendMessage(player, teleportLoc.getWorld().getName(), teleportLoc.getX(), teleportLoc.getY(), teleportLoc.getZ());
							}
						}
					}
				}
			}

			@Override
			public ItemStack getItem() {
				return null;
			}

			@Override
			public ItemStack getItem(final Object object) {

				Location location = null;
				if (object instanceof Location)
					location = (Location) object;
				final GuiTempletsYaml gui;
				if (location != null)
					gui = guiTemplets.menuKey("Container_list").placeholders(location.getWorld() != null ? location.getWorld().getName() : location.getWorld(), location.getBlockX(), location.getBlockY(), location.getBlockZ()).build();
				else
					gui = guiTemplets.menuKey("Container_list").placeholders(object).build();
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

				UpdateTittleContainers.update(player, guiTemplets.build().getGuiTitle("Container_Linked_List"));
				updateButtons();
			}

			@Override
			public ItemStack getItem() {
				final GuiTempletsYaml gui = guiTemplets.menuKey("Previous_button").build();

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
				UpdateTittleContainers.update(player, guiTemplets.build().getGuiTitle("Container_Linked_List", getPageNumber()));
				updateButtons();
			}

			@Override
			public ItemStack getItem() {
				final GuiTempletsYaml gui = guiTemplets.menuKey("Forward_button").build();
				return CreateItemUtily.of(gui.getIcon(), gui.getDisplayName(),
						gui.getLore()).makeItemStack();
			}
		};
		backButton = new MenuButton() {
			@Override
			public void onClickInsideMenu(final Player player, final Inventory inventory, final ClickType clickType, final ItemStack itemStack, final Object o) {
				new ModifyContinerData.AlterContainerDataMenu(container).menuOpen(player);
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
	public MenuButton getFillButtonAt(final Object o) {
		return itemList;
	}

	@Override
	public MenuButton getButtonAt(final int slot) {

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
