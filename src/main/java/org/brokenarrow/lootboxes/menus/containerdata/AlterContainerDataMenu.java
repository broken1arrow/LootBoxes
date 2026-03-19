package org.brokenarrow.lootboxes.menus.containerdata;

import org.broken.arrow.library.menu.button.MenuButton;
import org.broken.arrow.library.menu.button.manager.utility.MenuButtonData;
import org.broken.arrow.library.menu.button.manager.utility.MenuTemplate;
import org.broken.arrow.library.menu.holder.MenuHolder;
import org.brokenarrow.lootboxes.Lootboxes;
import org.brokenarrow.lootboxes.builder.KeysData;
import org.brokenarrow.lootboxes.builder.LootContainerData;
import org.brokenarrow.lootboxes.builder.ParticleEffect;
import org.brokenarrow.lootboxes.builder.SettingsData;
import org.brokenarrow.lootboxes.commandprompt.ChangeDisplayNameLore;
import org.brokenarrow.lootboxes.commandprompt.ContainerDataLinkedLootTable;
import org.brokenarrow.lootboxes.commandprompt.SpecifyTime;
import org.brokenarrow.lootboxes.lootdata.ContainerDataCache;
import org.brokenarrow.lootboxes.menus.*;
import org.brokenarrow.lootboxes.menus.keys.EditKeysToOpenMenu;
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

import java.util.Map;

import static org.brokenarrow.lootboxes.menus.MenuKeys.ALTER_CONTAINER_DATA_MENU;
import static org.brokenarrow.lootboxes.settings.ChatMessages.ADD_CONTINERS_TURNED_ON_ADD_CONTAINERS_WITH_TOOL;
import static org.brokenarrow.lootboxes.settings.ChatMessages.ADD_CONTINERS_TURN_ON_ADD_CONTAINERS;
import static org.brokenarrow.lootboxes.untlity.KeyMeta.ADD_AND_REMOVE_CONTAINERS;

public final class AlterContainerDataMenu extends MenuHolder {
	private final ContainerDataCache containerDataCache = Lootboxes.getInstance().getContainerDataCache();
	private final Settings settings = Lootboxes.getInstance().getSettings();
	private final String containerKey;
	private final MenuTemplate guiTemplate;

	public AlterContainerDataMenu(final String containerKey) {
		this.containerKey = containerKey;
		this.guiTemplate = Lootboxes.getInstance().getMenu("Alter_container_data");

		setUseColorConversion(true);
		setIgnoreItemCheck(true);

		if (guiTemplate != null) {
			//setFillSpace(guiTemplate.getFillSlots());
			setMenuSize(guiTemplate.getinvSize("Alter_container_data"));
			setTitle(() -> TranslatePlaceHolders.translatePlaceholders(player, guiTemplate.getMenuTitle(), containerKey));
			setMenuOpenSound(guiTemplate.getSound());
			this.setUseColorConversion(true);
		} else {
			setMenuSize(36);
			setTitle(() -> "could not load menu 'Alter_container_data'.");
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
					updateButtons();
			}

			@Override
			public ItemStack getItem() {
				return containerDataCache.read(containerKey, containerData -> {
					org.broken.arrow.library.menu.button.manager.utility.MenuButton menuButton = button.getPassiveButton();
					Object[] placeholders = new Object[0];
					menuButton = getActiveButton(containerData ,button, menuButton);
					if (menuButton == null)
						menuButton = button.getPassiveButton();
					if (button.isActionTypeEqual("Change_displayName"))
						placeholders = getPlaceholders(containerData.getDisplayName(),
								containerData.getDisplayName() == null || containerData.getDisplayName().isEmpty() ?
										containerKey : containerData.getDisplayName());
					if (button.isActionTypeEqual("Container_linked_to_loot_table"))
						placeholders = new Object[]{containerData.getLootTableLinked()};
					if (button.isActionTypeEqual("Particle_animation")) {
						final Map<String, ParticleEffect> particleEffect = containerData.getParticleEffects();
						placeholders = getPlaceholders("",particleEffect != null && !particleEffect.isEmpty()? particleEffect.keySet() : "Not set");
					}
					if (button.isActionTypeEqual("Keys_to_open_container")) {
						final Map<String, KeysData> keysData = containerData.getKeysData();
						placeholders = getPlaceholders("", keysData != null ? keysData.keySet()  : "");
					}
					if (button.isActionTypeEqual("Cooldown_container"))
						placeholders = getPlaceholders("", containerData.getCooldown());
					if (button.isActionTypeEqual("Containers"))
						placeholders = getPlaceholders("", containerKey);
					String material = menuButton.getMaterial().equals("AIR") || menuButton.getMaterial().isEmpty() ? Material.PAPER.name() : menuButton.getMaterial();

					return CreateItemUtily.of(menuButton.isGlow(), material,
									TranslatePlaceHolders.translatePlaceholders(player, menuButton.getDisplayName(), placeholders),
									TranslatePlaceHolders.translatePlaceholdersLore(player, menuButton.getLore(), placeholders))
							.makeItemStack();
				});
			}
		};
	}

	public boolean run(MenuButtonData button, ClickType click) {
		final SettingsData setting = settings.getSettingsData();
		return this.containerDataCache.write(containerKey, lootContainerData -> {
			switch (button.getActionType()) {
				case "Container_linked_to_loot_table":
					if (click.isRightClick()) {
						new ContainerDataLinkedLootTable(lootContainerData, containerKey);
					} else if (click.isLeftClick()) {
						new ListOfLootTables(containerKey).menuOpen(player);
					}
					break;
				case "Particle_animation":
					try {
						new ParticleAnimation(containerKey, "")
								.menuOpen(player);
					} catch (Exception exception) {
						exception.printStackTrace();
					}
					break;
				case "Change_displayName":
					new ChangeDisplayNameLore(ALTER_CONTAINER_DATA_MENU, containerKey, "", false).start(player);
					break;
				case "Keys_to_open_container":
					new EditKeysToOpenMenu(containerKey).menuOpen(player);
					break;
				case "Change_icon":
					new MaterialList(ALTER_CONTAINER_DATA_MENU, "", containerKey, "").menuOpen(player);
					break;
				case "Cooldown_container":

					if (!lootContainerData.isSpawningContainerWithCooldown()) return false;
					new SpecifyTime(containerKey).start(player);
					break;
				case "Containers":
						new ContainersLinkedList(lootContainerData, containerKey, "").menuOpen(player);
					break;
				case "Add_remove_containers":
					if (lootContainerData.isRandomSpawn()) return false;

					if (click.isShiftClick() && click.isRightClick()) {
						new ChooseContainer(containerKey).menuOpen(player);
						return false;
					} else if (!click.isShiftClick() && click.isLeftClick()) {
						ADD_CONTINERS_TURN_ON_ADD_CONTAINERS.sendMessage(player);
						player.closeInventory();
					} else if (!click.isShiftClick() && click.isRightClick()) {
						ADD_CONTINERS_TURNED_ON_ADD_CONTAINERS_WITH_TOOL.sendMessage(player);
						player.getInventory().addItem(CreateItemUtily.of(false, setting.getLinkToolItem(),
										setting.getLinkToolDisplayName(), setting.getLinkToolLore())
								.setItemMetaData(ADD_AND_REMOVE_CONTAINERS.name(), containerKey).makeItemStack());
						player.closeInventory();
					}
					player.setMetadata(ADD_AND_REMOVE_CONTAINERS.name(), new FixedMetadataValue(Lootboxes.getInstance(), containerKey));
					break;
				case "Settings_container_data":
					new SettingsContainerData(containerKey).menuOpen(player);
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
		});
	}

	public org.broken.arrow.library.menu.button.manager.utility.MenuButton getActiveButton(LootContainerData containerData, MenuButtonData button, org.broken.arrow.library.menu.button.manager.utility.MenuButton passiveButton) {
		if (!containerData.isSpawningContainerWithCooldown() && passiveButton.isActionTypeEqual("Generate_loot_is_off"))
			return button.getActiveButton();
		if (containerData.isRandomSpawn()) {
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