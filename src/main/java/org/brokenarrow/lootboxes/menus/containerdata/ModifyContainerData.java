package org.brokenarrow.lootboxes.menus.containerdata;

import org.broken.arrow.menu.button.manager.library.utility.MenuButtonData;
import org.broken.arrow.menu.button.manager.library.utility.MenuTemplate;
import org.broken.arrow.menu.library.button.MenuButton;
import org.broken.arrow.menu.library.holder.MenuHolder;
import org.brokenarrow.lootboxes.Lootboxes;
import org.brokenarrow.lootboxes.builder.ContainerDataBuilder;
import org.brokenarrow.lootboxes.commandprompt.CreateContainerDataName;
import org.brokenarrow.lootboxes.lootdata.ContainerDataCache;
import org.brokenarrow.lootboxes.lootdata.KeyDropData;
import org.brokenarrow.lootboxes.menus.MainMenu;
import org.brokenarrow.lootboxes.untlity.CreateItemUtily;
import org.brokenarrow.lootboxes.untlity.TranslatePlaceHolders;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class ModifyContainerData extends MenuHolder {

	private final KeyDropData keyDropData = KeyDropData.getInstance();
	private final ContainerDataCache containerDataCache = ContainerDataCache.getInstance();

	private final MenuTemplate guiTemplate;

	public ModifyContainerData() {
		super(ContainerDataCache.getInstance().getContainerData());
		this.guiTemplate = Lootboxes.getInstance().getMenu("Containers_list");

		setUseColorConversion(true);
		if (guiTemplate != null) {
			setFillSpace(guiTemplate.getFillSlots());
			setMenuSize(guiTemplate.getinvSize("Containers_list"));
			setTitle(() -> TranslatePlaceHolders.translatePlaceholders(player, guiTemplate.getMenuTitle(), ""));
			setMenuOpenSound(guiTemplate.getSound());
			this.setUseColorConversion(true);
		} else {
			setMenuSize(36);
			setTitle(() -> "could not load menu 'Containers_list'.");
		}
	}

	@Override
	public MenuButton getFillButtonAt(@NotNull Object object) {
		MenuButtonData button = this.guiTemplate.getMenuButton(-1);
		if (button == null) return null;
		return new MenuButton() {

			@Override
			public void onClickInsideMenu(@NotNull final Player player, @NotNull final Inventory menu, @NotNull final ClickType click, @NotNull final ItemStack clickedItem, final Object object) {

				if (object instanceof String) {
					if (click.isLeftClick())
						new AlterContainerDataMenu((String) object).menuOpen(player);
					if (click.isRightClick()) {
						containerDataCache.removeCacheContainerData((String) object);
						keyDropData.removeKey((String) object);
					}
				}
			}

			@Override
			public ItemStack getItem() {
				org.broken.arrow.menu.button.manager.library.utility.MenuButton menuButton = button.getPassiveButton();
				if (object instanceof String) {
					final ContainerDataBuilder data = containerDataCache.getCacheContainerData(String.valueOf(object));
					if (data != null) {
						String tableLinked = data.getLootTableLinked();
						final String tableLink = tableLinked == null || tableLinked.isEmpty() ? "non" : tableLinked;
						String displayName = TranslatePlaceHolders.translatePlaceholders(player, menuButton.getDisplayName(), object, tableLink, data.getCooldown(), data.getIcon());

						ItemStack itemStack = null;
						if (data.getIcon() == null || data.getIcon() == Material.AIR)
							itemStack = CreateItemUtily.of(Material.CHEST).makeItemStack();
						return CreateItemUtily.of(itemStack != null ? itemStack : data.getIcon(),
								displayName,
								TranslatePlaceHolders.translatePlaceholdersLore(player, menuButton.getLore(),object, tableLink, data.getCooldown(), data.getIcon()))
								.setShowEnchantments(true).makeItemStack();
					}
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
						.setGlow(menuButton.isGlow())
						.makeItemStack();
			}
		};
	}

	public boolean run(MenuButtonData button, ClickType click) {
		if (button.isActionTypeEqual("Create_container_data")) {
			new CreateContainerDataName(Material.AIR).start(player);
		}
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
		if (button.isActionTypeEqual("Search")) {}

		if (button.isActionTypeEqual("Back_button")) {
			new MainMenu().menuOpen(player);
		}
		return false;
	}

}
