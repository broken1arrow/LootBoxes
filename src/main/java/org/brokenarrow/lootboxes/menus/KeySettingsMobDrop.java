package org.brokenarrow.lootboxes.menus;

import org.broken.arrow.menu.library.button.MenuButton;
import org.broken.arrow.menu.library.holder.MenuHolder;
import org.brokenarrow.lootboxes.Lootboxes;
import org.brokenarrow.lootboxes.builder.GuiTempletsYaml;
import org.brokenarrow.lootboxes.builder.KeyMobDropData;
import org.brokenarrow.lootboxes.builder.SettingsData;
import org.brokenarrow.lootboxes.lootdata.KeyDropData;
import org.brokenarrow.lootboxes.lootdata.LootItems;
import org.brokenarrow.lootboxes.untlity.CreateItemUtily;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import static org.brokenarrow.lootboxes.menus.MenuKeys.KEY_SETTINGS_MOBDROP;

public class KeySettingsMobDrop extends MenuHolder {
	private final MenuButton backButton;
	private final MenuButton setMobsDropThisKey;
	private final MenuButton changeChance;
	private final MenuButton changeMiniAmount;
	private final MenuButton changeMaxAmount;
	private final GuiTempletsYaml.Builder guiTemplets;
	private final LootItems lootItems = LootItems.getInstance();
	private final KeyDropData keyDropData = KeyDropData.getInstance();
	private final SettingsData settingsData = Lootboxes.getInstance().getSettings().getSettingsData();

	public KeySettingsMobDrop(final String continerData, final String keyName) {

		this.guiTemplets = new GuiTempletsYaml.Builder(getViewer(), "Key_Settings_MobDrop").placeholders(keyName, "");

		setMenuSize(guiTemplets.build().getGuiSize());
		setTitle(()-> guiTemplets.build().getGuiTitle());
		//setFillSpace(guiTemplets.build().getFillSpace());

		this.setMobsDropThisKey = new MenuButton() {
			@Override
			public void onClickInsideMenu(final @NotNull Player player, final @NotNull Inventory inventory, final @NotNull ClickType clickType, final @NotNull ItemStack itemStack, final Object o) {
				new EntityTypeListMenu(KEY_SETTINGS_MOBDROP, continerData, keyName, "").menuOpen(player);
			}

			@Override
			public ItemStack getItem() {
				final KeyMobDropData data = keyDropData.getKeyMobDropData(continerData, keyName);

				final GuiTempletsYaml gui = guiTemplets.menuKey("Mobs_some_drop_this_key").placeholders(data != null ? data.getEntityTypes() : "", data != null ? data.getKeyName() : "").build();

				return CreateItemUtily.of(gui.getIcon(),
						gui.getDisplayName(),
						gui.getLore()).makeItemStack();
			}
		};
		this.changeChance = new MenuButton() {
			@Override
			public void onClickInsideMenu(final @NotNull Player player, final @NotNull Inventory inventory, final @NotNull ClickType clickType, final @NotNull ItemStack itemStack, final Object o) {
				final KeyMobDropData data = keyDropData.getKeyMobDropData(continerData, keyName);
				final KeyMobDropData.Builder builder = data.getBuilder();

				int amount = 0;
				if (clickType == ClickType.LEFT)
					amount += 1;
				if (clickType == ClickType.RIGHT)
					amount -= 1;
				if (clickType == ClickType.SHIFT_LEFT)
					amount += settingsData.getIncrease();
				if (clickType == ClickType.SHIFT_RIGHT)
					amount -= settingsData.getDecrease();
				int chance = data.getChance() + amount;
				if (chance > 100)
					chance = 100;
				if (chance < 0)
					chance = 0;
				builder.setChance(chance);
				keyDropData.putCachedKeyData(continerData, keyName, builder.build());
				updateButton(this);
			}

			@Override
			public ItemStack getItem() {
				final KeyMobDropData data = keyDropData.getKeyMobDropData(continerData, keyName);

				final GuiTempletsYaml gui = guiTemplets.menuKey("Change_Chance").placeholders(data != null ? data.getChance() : 0, settingsData.getIncrease(), settingsData.getDecrease()).build();

				return CreateItemUtily.of(gui.getIcon(),
						gui.getDisplayName(),
						gui.getLore()).makeItemStack();
			}
		};
		this.changeMiniAmount = new MenuButton() {
			@Override
			public void onClickInsideMenu(final @NotNull Player player, final @NotNull Inventory inventory, final @NotNull ClickType clickType, final @NotNull ItemStack itemStack, final Object o) {
				final KeyMobDropData data = keyDropData.getKeyMobDropData(continerData, keyName);
				final KeyMobDropData.Builder builder = data.getBuilder();

				int amount = 0;
				if (clickType == ClickType.LEFT)
					amount += 1;
				if (clickType == ClickType.RIGHT)
					amount -= 1;
				if (clickType == ClickType.SHIFT_LEFT)
					amount += settingsData.getIncrease();
				if (clickType == ClickType.SHIFT_RIGHT)
					amount -= settingsData.getDecrease();
				int minimum = data.getMinimum() + amount;

				if (minimum < 0)
					minimum = 0;
				builder.setMinimum(minimum);
				keyDropData.putCachedKeyData(continerData, keyName, builder.build());
				updateButton(this);
			}

			@Override
			public ItemStack getItem() {
				final KeyMobDropData data = keyDropData.getKeyMobDropData(continerData, keyName);
				final GuiTempletsYaml gui = guiTemplets.menuKey("Change_Minimum").placeholders(data != null ? data.getMinimum() : 0, settingsData.getIncrease(), settingsData.getDecrease()).build();

				return CreateItemUtily.of(gui.getIcon(),
						gui.getDisplayName(),
						gui.getLore()).makeItemStack();
			}
		};
		this.changeMaxAmount = new MenuButton() {
			@Override
			public void onClickInsideMenu(final @NotNull Player player, final @NotNull Inventory inventory, final @NotNull ClickType clickType, final @NotNull ItemStack itemStack, final Object o) {
				final KeyMobDropData data = keyDropData.getKeyMobDropData(continerData, keyName);
				final KeyMobDropData.Builder builder = data.getBuilder();

				int amount = 0;
				if (clickType == ClickType.LEFT)
					amount += 1;
				if (clickType == ClickType.RIGHT)
					amount -= 1;
				if (clickType == ClickType.SHIFT_LEFT)
					amount += settingsData.getIncrease();
				if (clickType == ClickType.SHIFT_RIGHT)
					amount -= settingsData.getDecrease();
				int maximum = data.getMaximum() + amount;

				if (maximum < 0)
					maximum = 0;
				builder.setMaximum(maximum);
				keyDropData.putCachedKeyData(continerData, keyName, builder.build());
				updateButton(this);
			}

			@Override
			public ItemStack getItem() {
				final KeyMobDropData data = keyDropData.getKeyMobDropData(continerData, keyName);
				final GuiTempletsYaml gui = guiTemplets.menuKey("Change_Maximum").placeholders(data != null ? data.getMaximum() : 0, settingsData.getIncrease(), settingsData.getDecrease()).build();

				return CreateItemUtily.of(gui.getIcon(),
						gui.getDisplayName(),
						gui.getLore()).makeItemStack();
			}
		};

		backButton = new MenuButton() {

			@Override
			public void onClickInsideMenu(final @NotNull Player player, final @NotNull Inventory menu, final @NotNull ClickType click, final @NotNull ItemStack clickedItem, final Object object) {
				new EditKeysToOpen.EditKey(continerData, keyName).menuOpen(player);
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
	public MenuButton getButtonAt(final int slot) {

		if (guiTemplets.menuKey("Mobs_some_drop_this_key").build().getSlot().contains(slot))
			return setMobsDropThisKey;
		if (guiTemplets.menuKey("Change_Minimum").build().getSlot().contains(slot))
			return this.changeMiniAmount;
		if (guiTemplets.menuKey("Change_Maximum").build().getSlot().contains(slot))
			return this.changeMaxAmount;
		if (guiTemplets.menuKey("Change_Chance").build().getSlot().contains(slot))
			return changeChance;
		if (guiTemplets.menuKey("Back_button").build().getSlot().contains(slot))
			return backButton;

		return null;
	}

}
