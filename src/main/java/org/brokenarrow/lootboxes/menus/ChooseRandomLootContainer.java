package org.brokenarrow.lootboxes.menus;

import org.broken.arrow.menu.button.manager.library.utility.MenuButtonData;
import org.broken.arrow.menu.button.manager.library.utility.MenuTemplate;
import org.broken.arrow.menu.library.button.MenuButton;
import org.broken.arrow.menu.library.holder.MenuHolder;
import org.brokenarrow.lootboxes.Lootboxes;
import org.brokenarrow.lootboxes.builder.ContainerDataBuilder;
import org.brokenarrow.lootboxes.lootdata.ContainerDataCache;
import org.brokenarrow.lootboxes.untlity.CreateItemUtily;
import org.brokenarrow.lootboxes.untlity.Facing;
import org.brokenarrow.lootboxes.untlity.TranslatePlaceHolders;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import static org.brokenarrow.lootboxes.untlity.BountifyStrings.bountifyCapitalized;
import static org.brokenarrow.lootboxes.untlity.ListOfContainers.containers;

public class ChooseRandomLootContainer extends MenuHolder {

	private final ContainerDataCache containerDataCache = ContainerDataCache.getInstance();
	private final MenuTemplate guiTemplate;
	private final String containerName;

	public ChooseRandomLootContainer(final ContainerDataBuilder containerData, String containerName) {
		super(containers());
		this.containerName = containerName;
		this.guiTemplate = Lootboxes.getInstance().getMenu("Random_loot_container");
		if (guiTemplate != null) {
			setFillSpace(guiTemplate.getFillSlots());
			setMenuSize(guiTemplate.getinvSize("Random_loot_container"));
			setTitle(() ->TranslatePlaceHolders.translatePlaceholders(guiTemplate.getMenuTitle(),containerName));
			setMenuOpenSound(guiTemplate.getSound());
		} else {
			setMenuSize(36);
			setTitle(() -> "could not load menu 'Random_loot_container'.");

		}
	}

	@Override
	public MenuButton getFillButtonAt(@NotNull Object object) {
		MenuButtonData button = this.guiTemplate.getMenuButton(-1);
		if (button == null) return null;
		return new MenuButton() {

			@Override
			public void onClickInsideMenu(@NotNull final Player player, @NotNull final Inventory menu, @NotNull final ClickType click, @NotNull final ItemStack clickedItem, final Object object) {
				if (object instanceof Material) {
					Material material = (Material) object;
					final ContainerDataBuilder containerDataBuilder = containerDataCache.getCacheContainerData(containerName);
					final ContainerDataBuilder.Builder builder = containerDataBuilder.getBuilder();
					if (click == ClickType.SHIFT_LEFT) {
						builder.setRandomLootContainerItem(material);
						containerDataCache.setContainerData(containerName, builder.build());
						updateButtons();
						return;
					}
					boolean isChest = material == Material.CHEST || material == Material.TRAPPED_CHEST;
					int ordinal = containerDataBuilder.getRandomLootContainerFacing().ordinal();
					if (click.isRightClick())
						ordinal = ordinal + 1;
					if (click.isLeftClick())
						ordinal = ordinal - 1;
					if (isChest && ordinal > Facing.values().length - 3)
						ordinal = Facing.values().length - 3;
					if (ordinal < 0)
						ordinal = isChest ? Facing.values().length - 3 : Facing.values().length - 1;
					Facing type = Facing.getFace(ordinal);
					if (type == null)
						type = Facing.getFace(0);

					builder.setRandomLootContainerFacing(type);
					containerDataCache.setContainerData(containerName, builder.build());

					updateButtons();
				}
			}

			@Override
			public ItemStack getItem() {
				if (object instanceof Material) {
					final ContainerDataBuilder containerDataBuilder = containerDataCache.getCacheContainerData(containerName);
					org.broken.arrow.menu.button.manager.library.utility.MenuButton menuButton = button.getPassiveButton();
					Material material = (Material) object;
					Material currentItem = containerDataBuilder.getRandomLootContainerItem();
					if (currentItem == material)
						menuButton = button.getActiveButton();

					if (menuButton == null)
						menuButton = button.getPassiveButton();

					String materialType = bountifyCapitalized(material);
					String facing = bountifyCapitalized(containerDataBuilder.getRandomLootContainerFacing());
					String displayName = TranslatePlaceHolders.translatePlaceholders(player, menuButton.getDisplayName(), materialType,facing,currentItem );

					return CreateItemUtily.of(material,
							displayName,
							TranslatePlaceHolders.translatePlaceholdersLore(player, menuButton.getLore(),materialType,facing,currentItem))
							.setGlow(menuButton.isGlow())
							.makeItemStack();
				}
				return null;
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
		if (button.isActionTypeEqual("Back_button")) {
			new SettingsContainerData(containerName).menuOpen(player);
		}
		return false;
	}
}

