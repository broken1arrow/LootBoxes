package org.brokenarrow.lootboxes.menus;

import org.brokenarrow.lootboxes.Lootboxes;
import org.brokenarrow.lootboxes.builder.ContainerDataBuilder;
import org.brokenarrow.lootboxes.builder.GuiTempletsYaml;
import org.brokenarrow.lootboxes.commandprompt.SeachInMenu;
import org.brokenarrow.lootboxes.lootdata.ContainerDataCache;
import org.brokenarrow.lootboxes.lootdata.LootItems;
import org.brokenarrow.lootboxes.settings.Settings;
import org.brokenarrow.lootboxes.untlity.CreateItemUtily;
import org.brokenarrow.menu.library.MenuButton;
import org.brokenarrow.menu.library.MenuHolder;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;
import java.util.List;

import static org.brokenarrow.lootboxes.menus.MenuKeys.PARTICLE_ANIMANTION;

public class ParticleAnimantion extends MenuHolder {

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

	public ParticleAnimantion(final String container, final String particleToSearchFor) {
		super(Lootboxes.getInstance().getParticleEffectList().getParticleList(particleToSearchFor));

		guiTemplets = new GuiTempletsYaml.Builder(getViewer(), "Particle_Animantion").placeholders("");

		setMenuSize(guiTemplets.build().getGuiSize());
		setTitle(guiTemplets.build().getGuiTitle());
		setFillSpace(guiTemplets.build().getFillSpace());

		seachButton = new MenuButton() {
			@Override
			public void onClickInsideMenu(final Player player, final Inventory menu, final ClickType click, final ItemStack clickedItem, final Object object) {
				if (click.isLeftClick())
					new SeachInMenu(PARTICLE_ANIMANTION, PARTICLE_ANIMANTION, container, "").start(player);
				else
					new ParticleAnimantion(container, "");
			}

			@Override
			public ItemStack getItem() {
				final GuiTempletsYaml gui = guiTemplets.menuKey("Seach_button").build();

				return CreateItemUtily.of(gui.getIcon(),
						gui.getDisplayName(),
						gui.getLore()).makeItemStack();
			}
		};

		backButton = new MenuButton() {

			@Override
			public void onClickInsideMenu(final Player player, final Inventory menu, final ClickType click, final ItemStack clickedItem, final Object object) {
				new ModifyContinerData.AlterContainerDataMenu(container).menuOpen(player);
			}

			@Override
			public ItemStack getItem() {
				final GuiTempletsYaml gui = guiTemplets.menuKey("Back_button").build();

				return CreateItemUtily.of(gui.getIcon(),
						gui.getDisplayName(),
						gui.getLore()).makeItemStack();
			}
		};
		listOfItems = new MenuButton() {
			@Override
			public void onClickInsideMenu(final Player player, final Inventory menu, final ClickType click, final ItemStack clickedItem, final Object object) {

				if (object instanceof Particle) {
					final ContainerDataBuilder data = containerDataCache.getCacheContainerData(container);
					final ContainerDataBuilder.Builder builder = data.getBuilder();
					final List<Particle> particleEffect = data.getParticleEffects();
					final Particle particle = (Particle) object;
					if (particleEffect != null) {
						if (!particleEffect.isEmpty())
							player.sendMessage("You change the loottable from " + data.getLootTableLinked() + " to " + object);

						if (particleEffect.contains(particle))
							player.sendMessage("Your change do not change the loottable is same as the old, old " + data.getLootTableLinked() + " new name " + object);
						if (click.isLeftClick())
							particleEffect.add(particle);
						if (click.isRightClick())
							particleEffect.remove(particle);
					}

					builder.setParticleEffect(particleEffect != null ? particleEffect : Collections.singletonList(particle));
					containerDataCache.setContainerData(container, builder.build());
					for (final Location location : containerDataCache.getLinkedContainerData(container).keySet())
						Lootboxes.getInstance().getSpawnContainerEffectsTask().addLocationInList(location);

					new ModifyContinerData.AlterContainerDataMenu(container).menuOpen(player);
				}
			}

			@Override
			public ItemStack getItem() {

				return null;
			}

			@Override
			public ItemStack getItem(final Object object) {

				if (object instanceof Particle) {
					final GuiTempletsYaml gui = guiTemplets.menuKey("Particle_list").placeholders(((Particle) object).name().equals("BLOCK_MARKER") ? "BARRIER" : object).build();
					final ContainerDataBuilder data = containerDataCache.getCacheContainerData(container);
					final List<Particle> particleEffect = data.getParticleEffects();

					return CreateItemUtily.of(Lootboxes.getInstance().getParticleEffectList().checkParticleList((Particle) object),
							gui.getDisplayName(),
							gui.getLore()).setGlow(particleEffect.contains((Particle) object)).makeItemStack();
				}
			/*	if (object instanceof ItemStack) {
					ItemStack item = ((ItemStack) object);
					if (cacheItemData.get(item) == null) return null;

					return item;
				}*/
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
				final GuiTempletsYaml gui = guiTemplets.menuKey("Previous_button").build();

				return CreateItemUtily.of(gui.getIcon(), gui.getDisplayName(),
						gui.getLore()).makeItemStack();
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
				final GuiTempletsYaml gui = guiTemplets.menuKey("Forward_button").build();
				return CreateItemUtily.of(gui.getIcon(), gui.getDisplayName(),
						gui.getLore()).makeItemStack();
			}
		};
	}

	@Override
	public MenuButton getFillButtonAt(final Object o) {
		return listOfItems;

	}

	@Override
	public MenuButton getButtonAt(final int slot) {

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
