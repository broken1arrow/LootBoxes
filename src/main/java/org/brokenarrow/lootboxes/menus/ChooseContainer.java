package org.brokenarrow.lootboxes.menus;

import org.apache.commons.lang.WordUtils;
import org.broken.arrow.menu.button.manager.library.utility.MenuButtonData;
import org.broken.arrow.menu.button.manager.library.utility.MenuTemplate;
import org.broken.arrow.menu.library.button.MenuButton;
import org.broken.arrow.menu.library.button.logic.ButtonUpdateAction;
import org.broken.arrow.menu.library.button.logic.FillMenuButton;
import org.broken.arrow.menu.library.holder.MenuHolderPage;
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

public class ChooseContainer extends MenuHolderPage<Material> {

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
		this.guiTemplate = Lootboxes.getInstance().getMenu("Choose_container");

		setUseColorConversion(true);
		setIgnoreItemCheck(true);

		if (guiTemplate != null) {
			setFillSpace(guiTemplate.getFillSlots());
			setMenuSize(guiTemplate.getinvSize("Choose_container"));
			setTitle(() ->TranslatePlaceHolders.translatePlaceholders(guiTemplate.getMenuTitle(),""));
			this.setUseColorConversion(true);
			setMenuOpenSound(guiTemplate.getSound());
		} else {
			setMenuSize(36);
			setTitle(() -> "could not load menu 'Choose_container'.");
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
		if (button.isActionTypeEqual("Back_button")) {
			new AlterContainerDataMenu(container).menuOpen(player);
		}
		return false;
	}

	@Override
	public FillMenuButton<Material> createFillMenuButton() {
		MenuButtonData button = this.guiTemplate.getMenuButton(-1);
		if (button == null) return null;

		return new FillMenuButton<>((player, menu, click, clickedItem, material) -> {
			if (material != null) {
				final SettingsData setting = settings.getSettingsData();

				TURNED_ON_ADD_CONTAINERS_WHEN_PLACE_CONTAINER.sendMessage(player);
				player.getInventory().addItem(CreateItemUtily.of(material,
								TranslatePlaceHolders.translatePlaceholders(setting.getPlaceContainerDisplayName(), WordUtils.capitalizeFully(material.toString().replace("_", " ").toLowerCase()), TranslatePlaceHolders.translatePlaceholdersLore(setting.getPlaceContainerLore())))
						.setItemMetaData(ADD_AND_REMOVE_CONTAINERS_ALLOW_PLACECONTAINER.name(), container).makeItemStack());
				player.setMetadata(ADD_AND_REMOVE_CONTAINERS.name(), new FixedMetadataValue(Lootboxes.getInstance(), container));
				player.closeInventory();
			}
			return ButtonUpdateAction.NONE;
		}, (slot, material) -> {
			if (material != null) {
				org.broken.arrow.menu.button.manager.library.utility.MenuButton menuButton = button.getPassiveButton();

				String displayName = TranslatePlaceHolders.translatePlaceholders(player, menuButton.getDisplayName(), WordUtils.capitalizeFully(material.toString().replace("_", " ").toLowerCase()));
				return CreateItemUtily.of(menuButton.isGlow(), material,
						displayName,
						TranslatePlaceHolders.translatePlaceholdersLore(player, menuButton.getLore())).makeItemStack();
			}
			return null;
		});
	}
}
