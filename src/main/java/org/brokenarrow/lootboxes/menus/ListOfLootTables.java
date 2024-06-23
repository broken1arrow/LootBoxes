package org.brokenarrow.lootboxes.menus;

import org.broken.arrow.menu.button.manager.library.utility.MenuButtonData;
import org.broken.arrow.menu.button.manager.library.utility.MenuTemplate;
import org.broken.arrow.menu.library.button.MenuButton;
import org.broken.arrow.menu.library.button.logic.ButtonUpdateAction;
import org.broken.arrow.menu.library.button.logic.FillMenuButton;
import org.broken.arrow.menu.library.holder.MenuHolderPage;
import org.brokenarrow.lootboxes.Lootboxes;
import org.brokenarrow.lootboxes.builder.ContainerDataBuilder;
import org.brokenarrow.lootboxes.builder.KeysData;
import org.brokenarrow.lootboxes.lootdata.ContainerDataCache;
import org.brokenarrow.lootboxes.lootdata.LootItems;
import org.brokenarrow.lootboxes.menus.containerdata.AlterContainerDataMenu;
import org.brokenarrow.lootboxes.untlity.CreateItemUtily;
import org.brokenarrow.lootboxes.untlity.TranslatePlaceHolders;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;

public class ListOfLootTables extends MenuHolderPage<String> {
	private final ContainerDataCache containerDataCache = ContainerDataCache.getInstance();
	private final String container;
	private final MenuTemplate guiTemplate;

	public ListOfLootTables(String container) {
		super(new ArrayList<>(LootItems.getInstance().getCachedLoot().keySet()));
		this.container = container;
		this.guiTemplate = Lootboxes.getInstance().getMenu("List_of_loot_tables");

		setUseColorConversion(true);
		setIgnoreItemCheck(true);

		if (guiTemplate != null) {
			setFillSpace(guiTemplate.getFillSlots());
			setMenuSize(guiTemplate.getinvSize("List_of_loot_tables"));
			setTitle(() -> TranslatePlaceHolders.translatePlaceholders(guiTemplate.getMenuTitle(), ""));
			this.setUseColorConversion(true);
		} else {
			setMenuSize(36);
			setTitle(() -> "could not load menu 'List_of_loot_tables'.");

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
				org.broken.arrow.menu.button.manager.library.utility.MenuButton menuButton = button.getPassiveButton();

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
		}
		if (button.isActionTypeEqual("Back_button")) {
			new AlterContainerDataMenu(container).menuOpen(player);
		}
		return false;
	}

	@Override
	public FillMenuButton<String> createFillMenuButton() {
		MenuButtonData button = this.guiTemplate.getMenuButton(-1);
		if (button == null) return null;

		return new FillMenuButton<>((player1, menu, click, clickedItem, lootTable) -> {
			if (lootTable != null) {
			ContainerDataBuilder cacheContainerData = containerDataCache.getCacheContainerData(container);
			ContainerDataBuilder.Builder builder = cacheContainerData.getBuilder();
			boolean isRightClick = click.isRightClick();
			if (!isRightClick && cacheContainerData.getLootTableLinked() != null) {
				if (!cacheContainerData.getLootTableLinked().isEmpty())
					player.sendMessage("You change the loottable from " + cacheContainerData.getLootTableLinked() + " to " + lootTable );

				if (cacheContainerData.getLootTableLinked().equals(lootTable ))
					player.sendMessage("You don't the loottable as it is same as the old, old " + cacheContainerData.getLootTableLinked() + " new name " + lootTable );
			}
			builder.setContainerDataLinkedToLootTable(isRightClick ? "" : lootTable);
			Map<String, KeysData> keysDataMap = cacheContainerData.getKeysData();
			if (keysDataMap != null && !keysDataMap.isEmpty()) {
				for (Entry<String, KeysData> map : keysDataMap.entrySet()) {
					map.getValue().setLootTableLinked(isRightClick ? "" : lootTable);
				}
			}
			builder.setKeysData(keysDataMap);
			containerDataCache.setContainerData(container, builder.build());

			new AlterContainerDataMenu(container).menuOpen(player);
		}
            return ButtonUpdateAction.NONE;
        },(slot, lootTable) -> {
			if (lootTable != null && lootTable.equals("Global_Values"))
				return null;
			if(lootTable == null)
				return null;

			org.broken.arrow.menu.button.manager.library.utility.MenuButton menuButton = button.getPassiveButton();
			String displayName = TranslatePlaceHolders.translatePlaceholders(player, menuButton.getDisplayName(), lootTable);
			return CreateItemUtily.of(menuButton.isGlow(),menuButton.getMaterial(),
							displayName,
							TranslatePlaceHolders.translatePlaceholdersLore(player, menuButton.getLore(), lootTable))
					.setGlow(menuButton.isGlow())
					.makeItemStack();
        });
	}
}
