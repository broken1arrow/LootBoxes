package org.brokenarrow.lootboxes.menus;

import org.brokenarrow.lootboxes.builder.ContainerDataBuilder;
import org.brokenarrow.lootboxes.builder.GuiTempletsYaml;
import org.brokenarrow.lootboxes.lootdata.ContainerDataCache;
import org.brokenarrow.lootboxes.untlity.CreateItemUtily;
import org.brokenarrow.lootboxes.untlity.Facing;
import org.brokenarrow.menu.library.MenuButton;
import org.brokenarrow.menu.library.MenuHolder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import static org.brokenarrow.lootboxes.untlity.BountifyStrings.bountifyCapitalized;
import static org.brokenarrow.lootboxes.untlity.ListOfContainers.containers;

public class ChoseRandomLootContainer extends MenuHolder {


	private final MenuButton listOfItems;
	private final MenuButton backButton;

	private final ContainerDataCache containerDataCache = ContainerDataCache.getInstance();
	private final GuiTempletsYaml.Builder guiTemplets;

	public ChoseRandomLootContainer(final ContainerDataBuilder containerdata,String containername) {
		super(containers());
		guiTemplets = new GuiTempletsYaml.Builder(getViewer(), "Random_loot_container_menu");

		setMenuSize(guiTemplets.build().getGuiSize());
		setTitle(guiTemplets.build().getGuiTitle());
		setFillSpace(guiTemplets.build().getFillSpace());

		listOfItems = new MenuButton() {

			@Override
			public void onClickInsideMenu(Player player, Inventory menu, ClickType click, ItemStack clickedItem, Object object) {
				if (object instanceof Material) {
					Material material = (Material) object;
					final ContainerDataBuilder containerDataBuilder = containerDataCache.getCacheContainerData(containername);
					final ContainerDataBuilder.Builder builder = containerDataBuilder.getBuilder();
					if (click == ClickType.SHIFT_LEFT) {
						builder.setRandonLootContainerItem( material);
						containerDataCache.setContainerData(containername, builder.build());
						updateButtons();
						return;
					}
					boolean isChest = material == Material.CHEST || material == Material.TRAPPED_CHEST;
					int ordinal = containerDataBuilder.getRandonLootContainerFaceing().ordinal();
					if (click.isRightClick())
						ordinal = ordinal + 1;
					if (click.isLeftClick())
						ordinal = ordinal - 1;
					if (isChest && ordinal > Facing.values().length - 3)
						ordinal =  Facing.values().length - 3;
					if (ordinal < 0)
						ordinal =  isChest ? Facing.values().length - 3 : Facing.values().length - 1;
					Facing type = Facing.getFace(ordinal);
					if (type == null)
						type = Facing.getFace(0);

					builder.setRandonLootContainerFaceing(type);
					containerDataCache.setContainerData(containername, builder.build());

					updateButtons();
				}
			}

			@Override
			public ItemStack getItem() {

				return null;
			}

			@Override
			public ItemStack getItem(Object object) {
				if (object instanceof Material) {
					final ContainerDataBuilder containerDataBuilder = containerDataCache.getCacheContainerData(containername);
					GuiTempletsYaml gui = guiTemplets.menuKey("List_Of_ContainerTypes").placeholders(bountifyCapitalized(object.toString()),bountifyCapitalized(containerDataBuilder.getRandonLootContainerFaceing())).build();
					Material material = (Material) object;

					if (containerDataBuilder.getRandonLootContainerItem() == material){
						return CreateItemUtily.of(gui.getIcon(),
								gui.getDisplayName(),
								gui.getLore()).makeItemStack();
					}
					return CreateItemUtily.of(material,
							gui.getDisplayName(),
							gui.getLore()).makeItemStack();
				}

				return null;
			}
		};


		backButton = new MenuButton() {

			@Override
			public void onClickInsideMenu(final Player player, final Inventory menu, final ClickType click, final ItemStack clickedItem, final Object object) {
				new ModifyContinerData.AlterContainerDataMenu(containername).menuOpen(player);
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
	public MenuButton getFillButtonAt(Object object) {
			return listOfItems;
	}

	@Override
	public MenuButton getButtonAt(final int slot) {

		if (guiTemplets.menuKey("Back_button").build().getSlot().contains(slot))
			return backButton;

		return null;
	}

}

