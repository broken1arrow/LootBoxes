package org.brokenarrow.lootboxes.menus;

import org.apache.commons.lang.WordUtils;
import org.broken.arrow.menu.button.manager.library.utility.MenuButtonData;
import org.broken.arrow.menu.button.manager.library.utility.MenuTemplate;
import org.broken.arrow.menu.library.button.MenuButton;
import org.broken.arrow.menu.library.holder.MenuHolder;
import org.brokenarrow.lootboxes.Lootboxes;
import org.brokenarrow.lootboxes.builder.SettingsData;
import org.brokenarrow.lootboxes.menus.containerdata.AlterContainerDataMenu;
import org.brokenarrow.lootboxes.settings.Settings;
import org.brokenarrow.lootboxes.untlity.CreateItemUtily;
import org.brokenarrow.lootboxes.untlity.TranslatePlaceHolders;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.jetbrains.annotations.NotNull;

import static org.brokenarrow.lootboxes.settings.ChatMessages.TURNED_ON_ADD_CONTAINERS_WHEN_PLACE_CONTAINER;
import static org.brokenarrow.lootboxes.untlity.KeyMeta.ADD_AND_REMOVE_CONTAINERS;
import static org.brokenarrow.lootboxes.untlity.KeyMeta.ADD_AND_REMOVE_CONTAINERS_ALLOW_PLACECONTAINER;
import static org.brokenarrow.lootboxes.untlity.ListOfContainers.containers;

public class ChooseContainer extends MenuHolder {

	private final MenuTemplate guiTemplate;
	private  MenuButton listOfItems;
	private  MenuButton previous;
	private  MenuButton forward;
	private  MenuButton backButton;
	private final String container;
	private final Settings settings = Lootboxes.getInstance().getSettings();

	public ChooseContainer(String container) {
		super(containers());
		this.container = container;
		//guiTemplets = new GuiTempletsYaml.Builder(getViewer(), "Choose_Container");
		this.guiTemplate = Lootboxes.getInstance().getMenu("Choose_container");
		if (guiTemplate != null) {
			setFillSpace(guiTemplate.getFillSlots());
			setMenuSize(guiTemplate.getinvSize("Choose_container"));
			setTitle(() ->TranslatePlaceHolders.translatePlaceholders(guiTemplate.getMenuTitle(),""));
			setMenuOpenSound(guiTemplate.getSound());
		} else {
			setMenuSize(36);
			setTitle(() -> "could not load menu 'Choose_container'.");
		}
	}

	@Override
	public MenuButton getFillButtonAt(@NotNull Object object) {
		MenuButtonData button = this.guiTemplate.getMenuButton(-1);
		if (button == null) return null;
		return new MenuButton() {
			private final SettingsData setting = settings.getSettingsData();

			@Override
			public void onClickInsideMenu(@NotNull final Player player, @NotNull final Inventory menu, @NotNull final ClickType click, @NotNull final ItemStack clickedItem, final Object object) {
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
				if (object instanceof Material) {
					org.broken.arrow.menu.button.manager.library.utility.MenuButton menuButton = button.getPassiveButton();

					String displayName = TranslatePlaceHolders.translatePlaceholders(player, menuButton.getDisplayName(), WordUtils.capitalizeFully(object.toString().replace("_", " ").toLowerCase()));
					return CreateItemUtily.of(object,
							displayName,
							TranslatePlaceHolders.translatePlaceholdersLore(player, menuButton.getLore())).makeItemStack();
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
			new AlterContainerDataMenu(container).menuOpen(player);
		}
		return false;
	}
}
