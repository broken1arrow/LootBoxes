package org.brokenarrow.lootboxes.menus;

import org.broken.arrow.menu.library.button.MenuButton;
import org.broken.arrow.menu.library.holder.MenuHolder;
import org.brokenarrow.lootboxes.Lootboxes;
import org.brokenarrow.lootboxes.builder.ContainerDataBuilder;
import org.brokenarrow.lootboxes.builder.GuiTempletsYaml;
import org.brokenarrow.lootboxes.lootdata.ContainerDataCache;
import org.brokenarrow.lootboxes.lootdata.LootItems;
import org.brokenarrow.lootboxes.settings.Settings;
import org.brokenarrow.lootboxes.untlity.CreateItemUtily;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class ListOfLoottables extends MenuHolder {
	private final MenuButton backButton;
	private final MenuButton listOfItems;
	private final MenuButton forward;
	private final MenuButton previous;
	private final MenuButton seachButton;
	private final LootItems lootItems = LootItems.getInstance();
	private final ContainerDataCache containerDataCache = ContainerDataCache.getInstance();
	private final org.brokenarrow.lootboxes.lootdata.ItemData itemData = org.brokenarrow.lootboxes.lootdata.ItemData.getInstance();
	private final Settings settings = Lootboxes.getInstance().getSettings();
	private final GuiTempletsYaml.Builder guiTemplets;

	public ListOfLoottables(String container) {
		super(new ArrayList<>(LootItems.getInstance().getCachedLoot().keySet()));
		guiTemplets = new GuiTempletsYaml.Builder(getViewer(), "List_of_loottables").placeholders("");

		setMenuSize(guiTemplets.build().getGuiSize());
		setTitle(()-> guiTemplets.build().getGuiTitle("List_of_loottables",this.getPageNumber()));
		setFillSpace(guiTemplets.build().getFillSpace());

		seachButton = new MenuButton() {
			@Override
			public void onClickInsideMenu(@NotNull Player player, @NotNull Inventory menu, @NotNull ClickType click, @NotNull ItemStack clickedItem, Object object) {

			}

			@Override
			public ItemStack getItem() {
				GuiTempletsYaml gui = guiTemplets.menuKey("Seach_button").build();

				return CreateItemUtily.of(gui.getIcon(),
						gui.getDisplayName(),
						gui.getLore()).makeItemStack();
			}
		};

		backButton = new MenuButton() {

			@Override
			public void onClickInsideMenu(@NotNull Player player, @NotNull Inventory menu, @NotNull ClickType click, @NotNull ItemStack clickedItem, Object object) {
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
		listOfItems = new MenuButton() {
			@Override
			public void onClickInsideMenu(@NotNull Player player, @NotNull Inventory menu, @NotNull ClickType click, @NotNull ItemStack clickedItem, Object object) {
				if (object instanceof String) {
					ContainerDataBuilder data = containerDataCache.getCacheContainerData(container);
					ContainerDataBuilder.Builder builder = data.getBuilder();
					if (!data.getLootTableLinked().isEmpty())
						player.sendMessage("You change the loottable from " + data.getLootTableLinked() + " to " + object);

					if (data.getLootTableLinked().equals(object))
						player.sendMessage("Your change do not change the loottable is same as the old, old " + data.getLootTableLinked() + " new name " + object);

					builder.setContainerDataLinkedToLootTable((String) object);
					containerDataCache.setContainerData(container, builder.build());
					new ModifyContinerData.AlterContainerDataMenu(container).menuOpen(player);
				}
			}

			@Override
			public ItemStack getItem() {

				return null;
			}

			@Override
			public ItemStack getItem(@NotNull Object object) {
				if (object instanceof String) {
					if (object.equals("Global_Values")) return null;
					GuiTempletsYaml gui = guiTemplets.menuKey("Loot_Tables").placeholders("",object).build();

					return CreateItemUtily.of(gui.getIcon(),
							gui.getDisplayName(),
							gui.getLore()).makeItemStack();
				}
				return null;
			}
		};
		previous = new MenuButton() {
			@Override
			public void onClickInsideMenu(final @NotNull Player player, final @NotNull Inventory menu, final @NotNull ClickType click, final @NotNull ItemStack clickedItem, final Object object) {

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
			public void onClickInsideMenu(final @NotNull Player player, final @NotNull Inventory menu, final @NotNull ClickType click, final @NotNull ItemStack clickedItem, final Object object) {
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
	}

	@Override
	public MenuButton getFillButtonAt(@NotNull Object o) {
		return listOfItems;

	}

	@Override
	public MenuButton getButtonAt(int slot) {

		if (guiTemplets.menuKey("Forward_button").build().getSlot().contains(slot))
			return forward;
		if (guiTemplets.menuKey("Previous_button").build().getSlot().contains(slot))
			return previous;
		if (guiTemplets.menuKey("Seach_button").build().getSlot().contains(slot))
			return seachButton;
		if (guiTemplets.menuKey("Back_button").build().getSlot().contains(slot))
			return backButton;

		return null;
	}
}
