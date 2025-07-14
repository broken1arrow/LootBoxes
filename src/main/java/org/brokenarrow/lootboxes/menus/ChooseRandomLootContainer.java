package org.brokenarrow.lootboxes.menus;

import org.broken.arrow.library.menu.button.manager.utility.MenuButtonData;
import org.broken.arrow.library.menu.button.manager.utility.MenuTemplate;
import org.broken.arrow.library.menu.button.MenuButton;
import org.broken.arrow.library.menu.button.logic.ButtonUpdateAction;
import org.broken.arrow.library.menu.button.logic.FillMenuButton;
import org.broken.arrow.library.menu.holder.MenuHolderPage;
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
import org.jetbrains.annotations.Nullable;

import static org.brokenarrow.lootboxes.untlity.BountifyStrings.bountifyCapitalized;
import static org.brokenarrow.lootboxes.untlity.ListOfContainers.containers;

public class ChooseRandomLootContainer extends MenuHolderPage<Material> {

	private final ContainerDataCache containerDataCache = ContainerDataCache.getInstance();
	private final MenuTemplate guiTemplate;
	private final String containerName;

	public ChooseRandomLootContainer(final ContainerDataBuilder containerData, String containerName) {
		super(containers());
		this.containerName = containerName;
		this.guiTemplate = Lootboxes.getInstance().getMenu("Random_loot_container");

		setUseColorConversion(true);
		setIgnoreItemCheck(true);

		if (guiTemplate != null) {
			setFillSpace(guiTemplate.getFillSlots());
			setMenuSize(guiTemplate.getinvSize("Random_loot_container"));
			setTitle(() ->TranslatePlaceHolders.translatePlaceholders(guiTemplate.getMenuTitle(),containerName));
			setMenuOpenSound(guiTemplate.getSound());
			this.setUseColorConversion(true);
		} else {
			setMenuSize(36);
			setTitle(() -> "could not load menu 'Random_loot_container'.");

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
		if (button.isActionTypeEqual("Back_button")) {
			new SettingsContainerData(containerName).menuOpen(player);
		}
		return false;
	}

	@Override
	public FillMenuButton<Material> createFillMenuButton() {
		MenuButtonData button = this.guiTemplate.getMenuButton(-1);
		if (button == null) return null;

		return new FillMenuButton<>((player, menu, click, clickedItem, material) -> {
			if (material != null) {

				final ContainerDataBuilder containerDataBuilder = containerDataCache.getCacheContainerData(containerName);
				final ContainerDataBuilder.Builder builder = containerDataBuilder.getBuilder();
				if (click == ClickType.SHIFT_LEFT) {
					builder.setRandomLootContainerItem(material);
					containerDataCache.setContainerData(containerName, builder.build());
					return ButtonUpdateAction.ALL;
				}
				Facing type = getContainerFacing(click, material, containerDataBuilder);

				builder.setRandomLootContainerFacing(type);
				containerDataCache.setContainerData(containerName, builder.build());
				return ButtonUpdateAction.ALL;
			}
			return ButtonUpdateAction.NONE;
		}, (slot, material) -> {
			org.broken.arrow.library.menu.button.manager.utility.MenuButton menuButton = button.getPassiveButton();

			ItemStack itemstack = null;
			if (material != null)
				itemstack = new ItemStack(material);
			if (itemstack == null)
				return null;
			String displayName = TranslatePlaceHolders.translatePlaceholders(player, menuButton.getDisplayName(), "", bountifyCapitalized(itemstack.getType()));

			return CreateItemUtily.of(menuButton.isGlow(),itemstack,
							displayName,
							TranslatePlaceHolders.translatePlaceholdersLore(player, menuButton.getLore()))
					.makeItemStack();
		});
	}

	@Nullable
	private static Facing getContainerFacing(ClickType click, Material material, ContainerDataBuilder containerDataBuilder) {
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
		return type;
	}
}

