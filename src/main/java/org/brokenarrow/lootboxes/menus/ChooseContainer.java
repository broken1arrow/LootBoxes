package org.brokenarrow.lootboxes.menus;

import org.apache.commons.lang.WordUtils;
import org.brokenarrow.lootboxes.Lootboxes;
import org.brokenarrow.lootboxes.builder.GuiTempletsYaml;
import org.brokenarrow.lootboxes.builder.SettingsData;
import org.brokenarrow.lootboxes.settings.Settings;
import org.brokenarrow.lootboxes.untlity.CreateItemUtily;
import org.brokenarrow.lootboxes.untlity.TranslatePlaceHolders;
import org.brokenarrow.menu.library.MenuButton;
import org.brokenarrow.menu.library.MenuHolder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

import static org.brokenarrow.lootboxes.settings.ChatMessages.TURNED_ON_ADD_CONTAINERS_WHEN_PLACE_CONTAINER;
import static org.brokenarrow.lootboxes.untlity.KeyMeta.ADD_AND_REMOVE_CONTAINERS;
import static org.brokenarrow.lootboxes.untlity.KeyMeta.ADD_AND_REMOVE_CONTAINERS_ALLOW_PLACECONTAINER;
import static org.brokenarrow.lootboxes.untlity.ListOfContainers.containers;

public class ChooseContainer extends MenuHolder {
	private final GuiTempletsYaml.Builder guiTemplets;
	private final MenuButton listOfItems;
	private final MenuButton previous;
	private final MenuButton forward;
	private final MenuButton backButton;
	private final Settings settings = Lootboxes.getInstance().getSettings();

	public ChooseContainer(String container) {
		super(containers());
		guiTemplets = new GuiTempletsYaml.Builder(getViewer(), "Choose_Container");

		setFillSpace(guiTemplets.build().getFillSpace());
		setMenuSize(guiTemplets.build().getGuiSize());
		setTitle(guiTemplets.build().getGuiTitle());

		listOfItems = new MenuButton() {
			private final SettingsData setting = settings.getSettingsData();

			@Override
			public void onClickInsideMenu(Player player, Inventory menu, ClickType click, ItemStack clickedItem, Object object) {

				if (object instanceof Material) {
					TURNED_ON_ADD_CONTAINERS_WHEN_PLACE_CONTAINER.sendMessage(player);
					player.getInventory().addItem(CreateItemUtily.of(object,
									TranslatePlaceHolders.translatePlaceholders(setting.getPlaceContainerDisplayName(), WordUtils.capitalizeFully(object.toString().replace("_", " ").toLowerCase()), TranslatePlaceHolders.translatePlaceholdersLore(setting.getPlaceContainerLore())))
							.setItemMetaData(ADD_AND_REMOVE_CONTAINERS_ALLOW_PLACECONTAINER.name(), container).makeItemStack());
					player.setMetadata(ADD_AND_REMOVE_CONTAINERS.name(), new FixedMetadataValue(Lootboxes.getInstance(), container));
					player.closeInventory();
				}
			}

			@Override
			public ItemStack getItem() {

				return null;
			}

			@Override
			public ItemStack getItem(Object object) {
				if (object instanceof Material) {
					GuiTempletsYaml gui = guiTemplets.menuKey("List_Of_ContainerTypes").placeholders(WordUtils.capitalizeFully(object.toString().replace("_", " ").toLowerCase())).build();
					return CreateItemUtily.of(object,
							gui.getDisplayName(),
							gui.getLore()).makeItemStack();
				}

				return null;
			}
		};
		previous = new MenuButton() {
			@Override
			public void onClickInsideMenu(final Player player, final Inventory menu, final ClickType click, final ItemStack clickedItem, final Object object) {
				if (click.isLeftClick()) {
					previousPage();
				}
			}

			@Override
			public ItemStack getItem() {
				GuiTempletsYaml gui = guiTemplets.menuKey("Previous_button").build();
				if (getRequiredPages() > 1)
					return CreateItemUtily.of(gui.getIcon(), gui.getDisplayName(),
							gui.getLore()).makeItemStack();
				return null;
			}
		};
		forward = new MenuButton() {
			@Override
			public void onClickInsideMenu(final Player player, final Inventory menu, final ClickType click, final ItemStack clickedItem, final Object object) {
				if (click.isLeftClick()) {
					nextPage();
				}
			}

			@Override
			public ItemStack getItem() {
				GuiTempletsYaml gui = guiTemplets.menuKey("Forward_button").build();
				if (getRequiredPages() > 1)
					return CreateItemUtily.of(gui.getIcon(), gui.getDisplayName(),
							gui.getLore()).makeItemStack();
				return null;
			}
		};
		backButton = new MenuButton() {

			@Override
			public void onClickInsideMenu(Player player, Inventory menu, ClickType click, ItemStack clickedItem, Object object) {
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
	public MenuButton getFillButtonAt(Object object) {
		return listOfItems;
	}

	@Override
	public MenuButton getButtonAt(int slot) {

		if (guiTemplets.menuKey("Forward_button").build().getSlot().contains(slot))
			return forward;
		if (guiTemplets.menuKey("Previous_button").build().getSlot().contains(slot))
			return previous;
		if (guiTemplets.menuKey("Back_button").build().getSlot().contains(slot))
			return backButton;

		return null;
	}
}
