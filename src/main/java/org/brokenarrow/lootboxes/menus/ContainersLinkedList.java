package org.brokenarrow.lootboxes.menus;

import org.broken.arrow.menu.button.manager.library.utility.MenuButtonData;
import org.broken.arrow.menu.button.manager.library.utility.MenuTemplate;
import org.broken.arrow.menu.library.button.MenuButton;
import org.broken.arrow.menu.library.holder.MenuHolder;
import org.brokenarrow.lootboxes.Lootboxes;
import org.brokenarrow.lootboxes.builder.ContainerData;
import org.brokenarrow.lootboxes.builder.ContainerDataBuilder;
import org.brokenarrow.lootboxes.builder.ContainerDataBuilder.Builder;
import org.brokenarrow.lootboxes.builder.GuiTempletsYaml;
import org.brokenarrow.lootboxes.lootdata.ContainerDataCache;
import org.brokenarrow.lootboxes.lootdata.LootItems;
import org.brokenarrow.lootboxes.menus.containerdata.AlterContainerDataMenu;
import org.brokenarrow.lootboxes.untlity.CreateItemUtily;
import org.brokenarrow.lootboxes.untlity.TeleportPlayer;
import org.brokenarrow.lootboxes.untlity.TranslatePlaceHolders;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.brokenarrow.lootboxes.settings.ChatMessages.*;

public class ContainersLinkedList extends MenuHolder {

	private  MenuButton backButton;
	private  MenuButton seachButton;
	private  MenuButton forward;
	private  MenuButton previous;
	private MenuButton itemList;
	private GuiTempletsYaml.Builder guiTemplets;
	private final LootItems lootItems = LootItems.getInstance();
	private final ContainerDataCache containerDataCache = ContainerDataCache.getInstance();
	private final MenuTemplate guiTemplate;
	private final String containerName;

	public ContainersLinkedList(final ContainerDataBuilder containerdata, final String containerName, final String itemsToSearchFor) {
		super(Arrays.asList(containerdata.getLinkedContainerData().keySet().toArray()));
		this.containerName = containerName;
		//this.guiTemplets = new GuiTempletsYaml.Builder(getViewer(), "Container_Linked_List").placeholders(containerName);
		this.guiTemplate = Lootboxes.getInstance().getMenu("Container_linked_list");

		if (guiTemplate != null) {
			setFillSpace(guiTemplate.getFillSlots());
			setMenuSize(guiTemplate.getinvSize("Container_linked_list"));
			setTitle(guiTemplate::getMenuTitle);
			setMenuOpenSound(guiTemplate.getSound());
		} else {
			setMenuSize(36);
			setTitle(() -> "could not load menu 'Container_linked_list'.");

		}
	}


	@Override
	public MenuButton getFillButtonAt(@NotNull Object object) {
		MenuButtonData button = this.guiTemplate.getMenuButton(-1);
		if (button == null) return null;
		return new MenuButton() {

			@Override
			public void onClickInsideMenu(@NotNull final Player player, @NotNull final Inventory menu, @NotNull final ClickType click, @NotNull final ItemStack clickedItem, final Object object) {
				if (object instanceof Location) {

					final ContainerDataBuilder containerDataBuilder = containerDataCache.getCacheContainerData(containerName);
					if (containerDataBuilder != null) {
						final Builder builder = containerDataBuilder.getBuilder();
						Map<Location, ContainerData> containerDataMap = containerDataBuilder.getLinkedContainerData();
						if (containerDataMap == null)
							containerDataMap = new HashMap<>();
						if (click.isRightClick()) {
							containerDataMap.remove(object);

							builder.setContainerData(containerDataMap);
							containerDataCache.setContainerData(containerName, builder.build());
						}
						if (click.isLeftClick()) {
							final TeleportPlayer teleport = new TeleportPlayer(player, (Location) object);
							if (teleport.teleportPlayer()) {
								final Location teleportLoc = teleport.getFinalTeleportLoc();

								if (teleportLoc.getZ() - 0.5 == ((Location) object).getZ() && teleportLoc.getX() - 0.5 == ((Location) object).getX())
									CONTINER_IS_NOT_OBSTACLE.sendMessage(player, teleportLoc.getWorld().getName(), teleportLoc.getX(), teleportLoc.getY(), teleportLoc.getZ());
								else if (teleportLoc.equals(object))
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

				Location location = null;
				if (object instanceof Location)
					location = (Location) object;
				if (location == null) return null;

				org.broken.arrow.menu.button.manager.library.utility.MenuButton menuButton = button.getPassiveButton();

				String displayName = TranslatePlaceHolders.translatePlaceholders(player, menuButton.getDisplayName(), location.getWorld() != null ? location.getWorld().getName() : location.getWorld(), location.getBlockX(), location.getBlockY(), location.getBlockZ());

				return CreateItemUtily.of(menuButton.getMaterial(),
								displayName,
								TranslatePlaceHolders.translatePlaceholdersLore(player, menuButton.getLore()))
						.makeItemStack();
			}
		};
		//return listOfItems;
	}

	@Override
	public MenuButton getButtonAt(int slot) {
		MenuButtonData button = this.guiTemplate.getMenuButton(slot);
		if (button == null) return null;
		return new MenuButton() {
			@Override
			public void onClickInsideMenu(@NotNull final Player player, @NotNull final Inventory menu, @NotNull final ClickType click, @NotNull final ItemStack clickedItem, final Object object) {
				if (run(button, click))
					updateButton(this);
			}

			@Override
			public ItemStack getItem() {
				org.broken.arrow.menu.button.manager.library.utility.MenuButton menuButton = button.getPassiveButton();

				return CreateItemUtily.of(menuButton.getMaterial(),
								TranslatePlaceHolders.translatePlaceholders(player, menuButton.getDisplayName()),
								TranslatePlaceHolders.translatePlaceholdersLore(player, menuButton.getLore()))
						.makeItemStack();
			}
		};
	}

	public boolean run(MenuButtonData button, ClickType click) {

		if (button.isActionTypeEqual("Forward_button")) {
			if (click.isLeftClick()) {
				nextPage();
			}
		}
		if (button.isActionTypeEqual("Previous_button")) {
			if (click.isLeftClick()) {
				previousPage();
			}
		}
		if (button.isActionTypeEqual("Search")) {
			/*	if (clickType.isLeftClick())
					new SeachInMenu(container, "").start(player);
				else
					new ContainersLinkedList(container, "").menuOpen(player);*/
		}
		if (button.isActionTypeEqual("Back_button")) {
			new AlterContainerDataMenu(containerName).menuOpen(player);
		}
		return false;
	}
}
