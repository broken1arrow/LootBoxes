package org.brokenarrow.lootboxes.menus;

import org.broken.arrow.library.menu.button.manager.utility.MenuButtonData;
import org.broken.arrow.library.menu.button.manager.utility.MenuTemplate;
import org.broken.arrow.library.menu.button.MenuButton;
import org.broken.arrow.library.menu.button.logic.ButtonUpdateAction;
import org.broken.arrow.library.menu.button.logic.FillMenuButton;
import org.broken.arrow.library.menu.holder.MenuHolderPage;
import org.brokenarrow.lootboxes.Lootboxes;
import org.brokenarrow.lootboxes.builder.ContainerData;
import org.brokenarrow.lootboxes.builder.ContainerDataBuilder;
import org.brokenarrow.lootboxes.builder.ContainerDataBuilder.Builder;
import org.brokenarrow.lootboxes.lootdata.ContainerDataCache;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static org.brokenarrow.lootboxes.settings.ChatMessages.CONTINER_IS_NOT_OBSTACLE;
import static org.brokenarrow.lootboxes.settings.ChatMessages.CONTINER_IS_OBSTACLE_ON_ALL_SIDES;
import static org.brokenarrow.lootboxes.settings.ChatMessages.CONTINER_IS_OBSTACLE_ON_SOME_SIDES;

public class ContainersLinkedList extends MenuHolderPage<Location> {
	private final ContainerDataCache containerDataCache = ContainerDataCache.getInstance();
	private final MenuTemplate guiTemplate;
	private final String containerName;

	public ContainersLinkedList(final ContainerDataBuilder containerdata, final String containerName, final String itemsToSearchFor) {
		super(new ArrayList<>(containerdata.getLinkedContainerData().keySet()));
		this.containerName = containerName;
		//this.guiTemplets = new GuiTempletsYaml.Builder(getViewer(), "Container_Linked_List").placeholders(containerName);
		this.guiTemplate = Lootboxes.getInstance().getMenu("Container_linked_list");

		setUseColorConversion(true);
		setIgnoreItemCheck(true);

		if (guiTemplate != null) {
			setFillSpace(guiTemplate.getFillSlots());
			setMenuSize(guiTemplate.getinvSize("Container_linked_list"));
			setTitle(() ->TranslatePlaceHolders.translatePlaceholders(guiTemplate.getMenuTitle(),""));
			setMenuOpenSound(guiTemplate.getSound());
			this.setUseColorConversion(true);
		} else {
			setMenuSize(36);
			setTitle(() -> "could not load menu 'Container_linked_list'.");

		}
	}

	@Override
	public MenuButton getButtonAt(int slot) {
		MenuButtonData button = this.guiTemplate.getMenuButton(slot);
		if (button == null) return null;
		return new MenuButton() {
			@Override
			public void onClickInsideMenu(@NotNull final Player player, @NotNull final Inventory menu, @NotNull final ClickType click, @NotNull final ItemStack clickedItem) {
				if (run(button, click))
					updateButton(this);
			}

			@Override
			public ItemStack getItem() {
				org.broken.arrow.library.menu.button.manager.utility.MenuButton menuButton = button.getPassiveButton();

				return CreateItemUtily.of(menuButton.isGlow(),menuButton.getMaterial(),
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

	@Override
	public FillMenuButton<Location> createFillMenuButton() {
		MenuButtonData button = this.guiTemplate.getMenuButton(-1);
		if (button == null) return null;

		return new FillMenuButton<>((player, menu, click, clickedItem, location ) -> {
			if (location != null) {
				final ContainerDataBuilder containerDataBuilder = containerDataCache.getCacheContainerData(containerName);
				if (containerDataBuilder != null) {
					final Builder builder = containerDataBuilder.getBuilder();
					Map<Location, ContainerData> containerDataMap = containerDataBuilder.getLinkedContainerData();
					if (containerDataMap == null)
						containerDataMap = new HashMap<>();
					if (click.isRightClick()) {
						containerDataMap.remove(location);

						builder.setContainerData(containerDataMap);
						containerDataCache.setContainerData(containerName, builder.build());
					}
					if (click.isLeftClick()) {
						final TeleportPlayer teleport = new TeleportPlayer(player, (Location) location);
						if (teleport.teleportPlayer()) {
							final Location teleportLoc = teleport.getFinalTeleportLoc();

							if (teleportLoc.getZ() - 0.5 == ((Location) location).getZ() && teleportLoc.getX() - 0.5 == ((Location) location).getX())
								CONTINER_IS_NOT_OBSTACLE.sendMessage(player, teleportLoc.getWorld().getName(), teleportLoc.getX(), teleportLoc.getY(), teleportLoc.getZ());
							else if (teleportLoc.equals(location))
								CONTINER_IS_OBSTACLE_ON_ALL_SIDES.sendMessage(player, teleportLoc.getWorld().getName(), teleportLoc.getX() - 0.5, teleportLoc.getY(), teleportLoc.getZ() - 0.5);
							else
								CONTINER_IS_OBSTACLE_ON_SOME_SIDES.sendMessage(player, teleportLoc.getWorld().getName(), teleportLoc.getX(), teleportLoc.getY(), teleportLoc.getZ());
						}
					}
				}
			}
			return ButtonUpdateAction.NONE;
		}, (slot, location) -> {
            if (location == null) return null;

			org.broken.arrow.library.menu.button.manager.utility.MenuButton menuButton = button.getPassiveButton();
			String displayName = TranslatePlaceHolders.translatePlaceholders(player, menuButton.getDisplayName(), location.getWorld() != null ? location.getWorld().getName() : location.getWorld(), location.getBlockX(), location.getBlockY(), location.getBlockZ());

			return CreateItemUtily.of(menuButton.isGlow(),menuButton.getMaterial(),
							displayName,
							TranslatePlaceHolders.translatePlaceholdersLore(player, menuButton.getLore()))
					.makeItemStack();
		});
	}
}
