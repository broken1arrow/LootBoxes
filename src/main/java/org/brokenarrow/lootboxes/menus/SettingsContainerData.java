package org.brokenarrow.lootboxes.menus;

import org.broken.arrow.menu.library.button.MenuButton;
import org.broken.arrow.menu.library.holder.MenuHolder;
import org.brokenarrow.lootboxes.Lootboxes;
import org.brokenarrow.lootboxes.builder.ContainerDataBuilder;
import org.brokenarrow.lootboxes.builder.GuiTempletsYaml;
import org.brokenarrow.lootboxes.lootdata.ContainerDataCache;
import org.brokenarrow.lootboxes.settings.Settings;
import org.brokenarrow.lootboxes.untlity.CreateItemUtily;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class SettingsContainerData extends MenuHolder {


	private final MenuButton randomLootContainer;
	private final MenuButton changeIfShallUsedToRandomSpawn;
	private final MenuButton showTitelSpawnChest;
	private final MenuButton contanerShallglow;
	private final MenuButton backButton;
	private final MenuButton generateLootWhenClicking;
	private final MenuButton attemptsToSpawn;
	private final GuiTempletsYaml.Builder guiTemplets;
	private final ContainerDataCache containerDataCache = ContainerDataCache.getInstance();
	private final Settings settings = Lootboxes.getInstance().getSettings();



	public SettingsContainerData(String containerDataName) {
		guiTemplets = new GuiTempletsYaml.Builder(getViewer(), "Settings_Container_Data").placeholders(containerDataName);
		setMenuSize(guiTemplets.build().getGuiSize());
		setTitle(guiTemplets.build().getGuiTitle());

		attemptsToSpawn = new MenuButton() {
			@Override
			public void onClickInsideMenu(@NotNull Player player, @NotNull Inventory menu, @NotNull ClickType click, @NotNull ItemStack clickedItem, Object object) {
				final ContainerDataBuilder containerDataBuilder = containerDataCache.getCacheContainerData(containerDataName);
				if (containerDataBuilder != null) {

					final ContainerDataBuilder.Builder builder = containerDataBuilder.getBuilder();
					int attempts = containerDataBuilder.getAttempts();
					if (click.isRightClick())
						attempts += 1;
					if (click.isLeftClick())
						attempts -= 1;

					builder.setAttempts(attempts);
					containerDataCache.setContainerData(containerDataName, builder.build());
				}
				updateButton(this);
			}

			@Override
			public ItemStack getItem() {
				final ContainerDataBuilder containerDataBuilder = containerDataCache.getCacheContainerData(containerDataName);
				GuiTempletsYaml gui = guiTemplets.menuKey("Attempts_to_spawn_random_spawn_off").placeholders("", containerDataBuilder.isSpawningContainerWithCooldown()).build();
				if (containerDataBuilder.isRandomSpawn()) {
					 gui = guiTemplets.menuKey("Attempts_to_spawn").placeholders("", containerDataBuilder.getAttempts()).build();
				}
				return CreateItemUtily.of(gui.getIcon(), gui.getDisplayName(),
						gui.getLore()).makeItemStack();
			}
		};


		generateLootWhenClicking = new MenuButton() {
			@Override
			public void onClickInsideMenu(final @NotNull Player player, final @NotNull Inventory menu, final @NotNull ClickType click, final @NotNull ItemStack clickedItem, final Object object) {
				final ContainerDataBuilder containerDataBuilder = containerDataCache.getCacheContainerData(containerDataName);
				if (containerDataBuilder != null) {
					if (containerDataBuilder.isSpawningContainerWithCooldown() == click.isLeftClick()) return;

					final ContainerDataBuilder.Builder builder = containerDataBuilder.getBuilder();
					builder.setSpawningContainerWithCooldown(click.isLeftClick());
					containerDataCache.setContainerData(containerDataName, builder.build());
				}
				updateButtons();
			}

			@Override
			public ItemStack getItem() {
				final ContainerDataBuilder containerDataBuilder = containerDataCache.getCacheContainerData(containerDataName);
				final GuiTempletsYaml gui = guiTemplets.menuKey("Generate_Loot_When_Clicking").placeholders("", containerDataBuilder.isSpawningContainerWithCooldown()).build();
				return CreateItemUtily.of(gui.getIcon(), gui.getDisplayName(),
						gui.getLore()).makeItemStack();
			}
		};

		contanerShallglow = new MenuButton() {

			@Override
			public void onClickInsideMenu(@NotNull Player player, @NotNull Inventory menu, @NotNull ClickType click, @NotNull ItemStack clickedItem, Object object) {
				final ContainerDataBuilder containerDataBuilder = containerDataCache.getCacheContainerData(containerDataName);
				if (containerDataBuilder != null) {
					if (containerDataBuilder.isContanerShallglow() == click.isLeftClick()) return;

					final ContainerDataBuilder.Builder builder = containerDataBuilder.getBuilder();
					builder.setContanerShallglow(click.isLeftClick());
					containerDataCache.setContainerData(containerDataName, builder.build());

				}
				updateButtons();
			}

			@Override
			public ItemStack getItem() {
				final ContainerDataBuilder containerDataBuilder = containerDataCache.getCacheContainerData(containerDataName);

				GuiTempletsYaml gui = guiTemplets.menuKey("Show_glow").placeholders("", containerDataBuilder.isContanerShallglow()).build();
				return CreateItemUtily.of(gui.getIcon(), gui.getDisplayName(),
						gui.getLore()).makeItemStack();
			}
		};

		showTitelSpawnChest = new MenuButton() {

			@Override
			public void onClickInsideMenu(@NotNull Player player, @NotNull Inventory menu, @NotNull ClickType click, @NotNull ItemStack clickedItem, Object object) {
				final ContainerDataBuilder containerDataBuilder = containerDataCache.getCacheContainerData(containerDataName);
				if (containerDataBuilder != null) {
					if (containerDataBuilder.isShowTitel() == click.isLeftClick()) return;

					final ContainerDataBuilder.Builder builder = containerDataBuilder.getBuilder();
					builder.setShowTitel(click.isLeftClick());
					containerDataCache.setContainerData(containerDataName, builder.build());
				}
				updateButtons();
			}

			@Override
			public ItemStack getItem() {
				final ContainerDataBuilder containerDataBuilder = containerDataCache.getCacheContainerData(containerDataName);

				GuiTempletsYaml gui = guiTemplets.menuKey("Show_titel").placeholders("", containerDataBuilder.isShowTitel()).build();
				return CreateItemUtily.of(gui.getIcon(), gui.getDisplayName(),
						gui.getLore()).makeItemStack();
			}
		};
		randomLootContainer = new MenuButton() {
			@Override
			public void onClickInsideMenu(final @NotNull Player player, final @NotNull Inventory menu, final @NotNull ClickType click, final @NotNull ItemStack clickedItem, final Object object) {
				final ContainerDataBuilder containerDataBuilder = containerDataCache.getCacheContainerData(containerDataName);
				if (containerDataBuilder != null)
					new ChoseRandomLootContainer(containerDataBuilder,containerDataName).menuOpen(player);
			}

			@Override
			public ItemStack getItem() {
				final ContainerDataBuilder containerDataBuilder = containerDataCache.getCacheContainerData(containerDataName);

				GuiTempletsYaml gui = guiTemplets.menuKey("Random_loot_container_settings").placeholders("", containerDataBuilder.getRandonLootContainerItem(),containerDataBuilder.getRandonLootContainerFaceing()).build();
				return CreateItemUtily.of(gui.getIcon(), gui.getDisplayName(),
						gui.getLore()).makeItemStack();
			}
		};

		changeIfShallUsedToRandomSpawn = new MenuButton() {

			@Override
			public void onClickInsideMenu(final @NotNull Player player, final @NotNull Inventory menu, final @NotNull ClickType click, final @NotNull ItemStack clickedItem, final Object object) {
				final ContainerDataBuilder containerDataBuilder = containerDataCache.getCacheContainerData(containerDataName);
				if (containerDataBuilder != null) {
					if (containerDataBuilder.isRandomSpawn() == click.isLeftClick()) return;

					final ContainerDataBuilder.Builder builder = containerDataBuilder.getBuilder();
					builder.setRandomSpawn(click.isLeftClick());
					containerDataCache.setContainerData(containerDataName, builder.build());


				}
				updateButtons();
			}

			@Override
			public ItemStack getItem() {
				final ContainerDataBuilder containerDataBuilder = containerDataCache.getCacheContainerData(containerDataName);
				GuiTempletsYaml gui = guiTemplets.menuKey("Set_random_spawn_disabled").placeholders(containerDataBuilder.isRandomSpawn()).build();

				if (containerDataBuilder.isRandomSpawn()) {
					gui = guiTemplets.menuKey("Set_random_spawn").placeholders(containerDataBuilder.isRandomSpawn()).build();
				}
				return CreateItemUtily.of(gui.getIcon(), gui.getDisplayName(),
						gui.getLore()).makeItemStack();
			}
		};

		backButton = new MenuButton() {

			@Override
			public void onClickInsideMenu(final @NotNull Player player, final @NotNull Inventory menu, final @NotNull ClickType click, final @NotNull ItemStack clickedItem, final Object object) {
				new ModifyContinerData.AlterContainerDataMenu(containerDataName).menuOpen(player);
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
	public MenuButton getButtonAt(int slot) {

		if (guiTemplets.menuKey("Attempts_to_spawn").build().getSlot().contains(slot))
			return attemptsToSpawn;
		if (guiTemplets.menuKey("Generate_Loot_When_Clicking").build().getSlot().contains(slot))
			return generateLootWhenClicking;
		if (guiTemplets.menuKey("Random_loot_container_settings").build().getSlot().contains(slot))
			return randomLootContainer;
		if (guiTemplets.menuKey("Set_random_spawn").build().getSlot().contains(slot))
			return changeIfShallUsedToRandomSpawn;
		if (guiTemplets.menuKey("Show_titel").build().getSlot().contains(slot))
			return showTitelSpawnChest;
		if (guiTemplets.menuKey("Show_glow").build().getSlot().contains(slot))
			return contanerShallglow;
		if (guiTemplets.menuKey("Back_button").build().getSlot().contains(slot))
			return backButton;

		return null;
	}
}
