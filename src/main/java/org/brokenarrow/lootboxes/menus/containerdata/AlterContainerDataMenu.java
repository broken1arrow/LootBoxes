package org.brokenarrow.lootboxes.menus.containerdata;

import org.broken.arrow.library.menu.button.manager.utility.MenuButtonData;
import org.broken.arrow.library.menu.button.manager.utility.MenuTemplate;
import org.broken.arrow.library.menu.button.MenuButton;
import org.broken.arrow.library.menu.holder.MenuHolder;
import org.brokenarrow.lootboxes.Lootboxes;
import org.brokenarrow.lootboxes.builder.ContainerDataBuilder;
import org.brokenarrow.lootboxes.builder.SettingsData;
import org.brokenarrow.lootboxes.commandprompt.ChangeDisplayNameLore;
import org.brokenarrow.lootboxes.commandprompt.ContainerDataLinkedLootTable;
import org.brokenarrow.lootboxes.commandprompt.SpecifyTime;
import org.brokenarrow.lootboxes.lootdata.ContainerDataCache;
import org.brokenarrow.lootboxes.menus.ChooseContainer;
import org.brokenarrow.lootboxes.menus.ContainersLinkedList;
import org.brokenarrow.lootboxes.menus.ListOfLootTables;
import org.brokenarrow.lootboxes.menus.MaterialList;
import org.brokenarrow.lootboxes.menus.ParticleAnimation;
import org.brokenarrow.lootboxes.menus.SettingsContainerData;
import org.brokenarrow.lootboxes.menus.keys.EditKeysToOpen;
import org.brokenarrow.lootboxes.settings.Settings;
import org.brokenarrow.lootboxes.untlity.CreateItemUtily;
import org.brokenarrow.lootboxes.untlity.TranslatePlaceHolders;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.jetbrains.annotations.NotNull;

import static org.brokenarrow.lootboxes.menus.MenuKeys.ALTER_CONTAINER_DATA_MENU;
import static org.brokenarrow.lootboxes.settings.ChatMessages.ADD_CONTINERS_TURNED_ON_ADD_CONTAINERS_WITH_TOOL;
import static org.brokenarrow.lootboxes.settings.ChatMessages.ADD_CONTINERS_TURN_ON_ADD_CONTAINERS;
import static org.brokenarrow.lootboxes.untlity.KeyMeta.ADD_AND_REMOVE_CONTAINERS;

public final class AlterContainerDataMenu extends MenuHolder {
	private final ContainerDataCache containerDataCache = ContainerDataCache.getInstance();
	private final Settings settings = Lootboxes.getInstance().getSettings();
	private ContainerDataBuilder containerDataBuilder;
	private final String containerDataName;
	private final MenuTemplate guiTemplate;

	public AlterContainerDataMenu(final String containerDataName) {
		this.containerDataName = containerDataName;
		this.guiTemplate = Lootboxes.getInstance().getMenu("Alter_container_data");

		setUseColorConversion(true);
		setIgnoreItemCheck(true);

		if (guiTemplate != null) {
			//setFillSpace(guiTemplate.getFillSlots());
			setMenuSize(guiTemplate.getinvSize("Alter_container_data"));
			setTitle(() -> TranslatePlaceHolders.translatePlaceholders(player, guiTemplate.getMenuTitle(), containerDataName));
			setMenuOpenSound(guiTemplate.getSound());
			this.setUseColorConversion(true);
		} else {
			setMenuSize(36);
			setTitle(() -> "could not load menu 'Alter_container_data'.");
		}
		this.containerDataBuilder = containerDataCache.getCacheContainerData(containerDataName);
	}

	@Override
	public MenuButton getButtonAt(int slot) {
		MenuButtonData button = this.guiTemplate.getMenuButton(slot);
		if (button == null) return null;
		return new MenuButton() {
			@Override
			public void onClickInsideMenu(@NotNull final Player player, @NotNull final Inventory menu, @NotNull final ClickType click, @NotNull final ItemStack clickedItem) {
				if (run(button, click))
					updateButtons();
			}

			@Override
			public ItemStack getItem() {
				org.broken.arrow.library.menu.button.manager.utility.MenuButton menuButton = button.getPassiveButton();
				Object[] placeholders = new Object[0];
				menuButton = getActiveButton(button, menuButton);

				if (menuButton == null)
					menuButton = button.getPassiveButton();
				if (button.isActionTypeEqual("Change_displayName"))
					placeholders = getPlaceholders(containerDataBuilder.getDisplayName(),
							containerDataBuilder.getDisplayName() == null || containerDataBuilder.getDisplayName().isEmpty() ?
									containerDataName : containerDataBuilder.getDisplayName());
				if (button.isActionTypeEqual("Container_linked_to_loot_table"))
					placeholders = new Object[]{containerDataBuilder.getLootTableLinked()};
				if (button.isActionTypeEqual("Particle_animation")) {
					placeholders = getPlaceholders("", containerDataCache.getParticlesList(containerDataName));
				}
				if (button.isActionTypeEqual("Keys_to_open_container"))
					placeholders = getPlaceholders("", containerDataCache.getListOfKeys(containerDataName));
				if (button.isActionTypeEqual("Cooldown_container"))
					placeholders = getPlaceholders("", containerDataBuilder.getCooldown());
				if (button.isActionTypeEqual("Containers"))
					placeholders = getPlaceholders("", containerDataName);


				return CreateItemUtily.of(menuButton.isGlow(),menuButton.getMaterial(),
								TranslatePlaceHolders.translatePlaceholders(player, menuButton.getDisplayName(), placeholders),
								TranslatePlaceHolders.translatePlaceholdersLore(player, menuButton.getLore(), placeholders))
						.makeItemStack();
			}
		};
	}
	public boolean run(MenuButtonData button, ClickType click) {
		final SettingsData setting = settings.getSettingsData();
		this.containerDataBuilder = containerDataCache.getCacheContainerData(containerDataName);
		switch (button.getActionType()) {
			case "Container_linked_to_loot_table":
				if (containerDataName != null && this.containerDataBuilder != null) {
					if (click.isRightClick()) {
						new ContainerDataLinkedLootTable(this.containerDataBuilder, containerDataName);
					} else if (click.isLeftClick()) {
						new ListOfLootTables(containerDataName).menuOpen(player);
					}
				}
				break;
			case "Particle_animation":
				try{
				new ParticleAnimation(containerDataName, "")
						.menuOpen(player);
				}catch (Exception exception){
					exception.printStackTrace();
				}
				break;
			case "Change_displayName":
				new ChangeDisplayNameLore(ALTER_CONTAINER_DATA_MENU, containerDataName, "", false).start(player);
				break;
			case "Keys_to_open_container":
				new EditKeysToOpen(containerDataName).menuOpen(player);
				break;
			case "Change_icon":
				new MaterialList(ALTER_CONTAINER_DATA_MENU, "", containerDataName, "").menuOpen(player);
				break;
			case "Cooldown_container":

				if (!this.containerDataBuilder.isSpawningContainerWithCooldown()) return false;
				new SpecifyTime(containerDataName).start(player);
				break;
			case "Containers":
				if (this.containerDataBuilder != null)
					new ContainersLinkedList(this.containerDataBuilder, containerDataName, "").menuOpen(player);
				break;
			case "Add_remove_containers":
				if (this.containerDataBuilder != null && this.containerDataBuilder.isRandomSpawn()) return false;

				if (click.isShiftClick() && click.isRightClick()) {
					new ChooseContainer(containerDataName).menuOpen(player);
					return false;
				} else if (!click.isShiftClick() && click.isLeftClick()) {
					ADD_CONTINERS_TURN_ON_ADD_CONTAINERS.sendMessage(player);
					player.closeInventory();
				} else if (!click.isShiftClick() && click.isRightClick()) {
					ADD_CONTINERS_TURNED_ON_ADD_CONTAINERS_WITH_TOOL.sendMessage(player);
					player.getInventory().addItem(CreateItemUtily.of(false,setting.getLinkToolItem(),
									setting.getLinkToolDisplayName(), setting.getLinkToolLore())
							.setItemMetaData(ADD_AND_REMOVE_CONTAINERS.name(), containerDataName).makeItemStack());
					player.closeInventory();
				}
				player.setMetadata(ADD_AND_REMOVE_CONTAINERS.name(), new FixedMetadataValue(Lootboxes.getInstance(), containerDataName));
				break;
			case "Settings_container_data":
				new SettingsContainerData(containerDataName).menuOpen(player);
				break;
			case "Back_button":
				new ModifyContainerData().menuOpen(player);
				break;
			default:
			if (button.isActionTypeEqual("Forward_button")) {
			}
			if (button.isActionTypeEqual("Previous_button")) {
			}
			if (button.isActionTypeEqual("Search")) {
			}
		}
		return false;
	}
/*	public boolean run(MenuButtonData button, ClickType click) {
		final SettingsData setting = settings.getSettingsData();
		if (button.isActionTypeEqual("Container_linked_to_loot_table")) {
			final ContainerDataBuilder containerDataBuilder = this.containerDataBuilder;
			if (containerDataName != null && containerDataBuilder != null) {
				if (click.isRightClick()) {
					new ContainerDataLinkedLootTable(containerDataBuilder, containerDataName);
				} else if (click.isLeftClick()) {
					new ListOfLootTables(containerDataName).menuOpen(player);
				}
			}
		}
		if (button.isActionTypeEqual("Particle_animation")) {
			new ParticleAnimation(containerDataName, "").menuOpen(player);
		}
		if (button.isActionTypeEqual("Change_displayName")) {
			new ChangeDisplayNameLore(ALTER_CONTAINER_DATA_MENU, containerDataName, "", false).start(player);
		}
		if (button.isActionTypeEqual("Keys_to_open_container")) {
			new EditKeysToOpen(containerDataName).menuOpen(player);
		}
		if (button.isActionTypeEqual("Change_icon")) {
			new MaterialList(ALTER_CONTAINER_DATA_MENU, "", containerDataName, "").menuOpen(player);
		}
		if (button.isActionTypeEqual("Cooldown_container")) {
			final ContainerDataBuilder containerDataBuilder = containerDataCache.getCacheContainerData(containerDataName);
			if (!containerDataBuilder.isSpawningContainerWithCooldown()) return false;
			new SpecifyTime(containerDataName).start(player);
		}
		if (button.isActionTypeEqual("Containers")) {
			final ContainerDataBuilder containerDataBuilder = containerDataCache.getCacheContainerData(containerDataName);
			if (containerDataBuilder != null)
				new ContainersLinkedList(containerDataBuilder, containerDataName, "").menuOpen(player);
		}
		if (button.isActionTypeEqual("Add_remove_containers")) {
			final ContainerDataBuilder containerDataBuilder = containerDataCache.getCacheContainerData(containerDataName);
			if (containerDataBuilder != null && containerDataBuilder.isRandomSpawn()) return false;

			if (click.isShiftClick() && click.isRightClick()) {
				new ChooseContainer(containerDataName).menuOpen(player);
				return false;
			} else if (!click.isShiftClick() && click.isLeftClick()) {
				ADD_CONTINERS_TURN_ON_ADD_CONTAINERS.sendMessage(player);
				player.closeInventory();
			} else if (!click.isShiftClick() && click.isRightClick()) {
				ADD_CONTINERS_TURNED_ON_ADD_CONTAINERS_WITH_TOOL.sendMessage(player);
				player.getInventory().addItem(CreateItemUtily.of(setting.getLinkToolItem(),
								setting.getLinkToolDisplayName(), setting.getLinkToolLore())
						.setItemMetaData(ADD_AND_REMOVE_CONTAINERS.name(), containerDataName).makeItemStack());
				player.closeInventory();
			}
			player.setMetadata(ADD_AND_REMOVE_CONTAINERS.name(), new FixedMetadataValue(Lootboxes.getInstance(), containerDataName));
		}
		if (button.isActionTypeEqual("Settings_container_data")) {
			new SettingsContainerData(containerDataName).menuOpen(player);
		}

		if (button.isActionTypeEqual("Forward_button")) {
		}
		if (button.isActionTypeEqual("Previous_button")) {
		}
		if (button.isActionTypeEqual("Search")) {
		}

		if (button.isActionTypeEqual("Back_button")) {
			new ModifyContainerData().menuOpen(player);
		}
		return false;
	}*/

	public org.broken.arrow.library.menu.button.manager.utility.MenuButton getActiveButton(MenuButtonData button, org.broken.arrow.library.menu.button.manager.utility.MenuButton passiveButton) {
		if (!containerDataBuilder.isSpawningContainerWithCooldown() && passiveButton.isActionTypeEqual("Generate_loot_is_off"))
			return button.getActiveButton();
		if (containerDataBuilder.isRandomSpawn()) {
			if (passiveButton.isActionTypeEqual("Linked_to_loot_table")) {
				return button.getActiveButton();
			}
			if (passiveButton.isActionTypeEqual("Random_spawn_on")) {
				return button.getActiveButton();
			}
			if (passiveButton.isActionTypeEqual("Add_remove_on")) {
				return button.getActiveButton();
			}
		}
		return null;
	}

	public Object[] getPlaceholders(Object... placeholder) {
		return placeholder;
	}
}