package org.brokenarrow.lootboxes.menus;

import org.brokenarrow.lootboxes.Lootboxes;
import org.brokenarrow.lootboxes.builder.ContainerDataBuilder;
import org.brokenarrow.lootboxes.builder.GuiTempletsYaml;
import org.brokenarrow.lootboxes.builder.ParticleEffect;
import org.brokenarrow.lootboxes.commandprompt.SeachInMenu;
import org.brokenarrow.lootboxes.effects.SpawnContainerEffectsTask;
import org.brokenarrow.lootboxes.lootdata.ContainerDataCache;
import org.brokenarrow.lootboxes.lootdata.ItemData;
import org.brokenarrow.lootboxes.lootdata.LootItems;
import org.brokenarrow.lootboxes.settings.Settings;
import org.brokenarrow.lootboxes.untlity.CreateItemUtily;
import org.brokenarrow.lootboxes.untlity.ParticleEffectList;
import org.brokenarrow.menu.library.MenuButton;
import org.brokenarrow.menu.library.MenuHolder;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Map;
import java.util.stream.Collectors;

import static org.brokenarrow.lootboxes.menus.MenuKeys.PARTICLE_ANIMANTION;
import static org.brokenarrow.lootboxes.untlity.BountifyStrings.bountifyCapitalized;
import static org.brokenarrow.lootboxes.untlity.ConvetParticlesUntlity.getEffectType;

public class ParticleAnimantion extends MenuHolder {

	private final MenuButton backButton;
	private final MenuButton listOfItems;
	private final MenuButton forward;
	private final MenuButton previous;
	private final MenuButton seachButton;
	private final LootItems lootItems = LootItems.getInstance();
	private final ContainerDataCache containerDataCache = ContainerDataCache.getInstance();
	private final ItemData itemData = ItemData.getInstance();
	private final Settings settings = Lootboxes.getInstance().getSettings();
	private final ParticleEffectList particleEffectList = Lootboxes.getInstance().getParticleEffectList();
	private final SpawnContainerEffectsTask spawnContainerEffectsTask = Lootboxes.getInstance().getSpawnContainerEffectsTask();
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
					new ParticleAnimantion(container, "").menuOpen(player);
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

				if (object instanceof Particle || object instanceof Effect) {
					final ContainerDataBuilder data = containerDataCache.getCacheContainerData(container);
					final ContainerDataBuilder.Builder builder = data.getBuilder();
					if (click.isRightClick()) {
						containerDataCache.removeParticleEffect(data, object);
					} else {
						builder.setParticleEffects(setParticelData(player, data, container, object));
					}
					containerDataCache.setContainerData(container, builder.build());
					if (click.isLeftClick()) {
						for (final Location location : containerDataCache.getLinkedContainerData(container).keySet())
							spawnContainerEffectsTask.addLocationInList(location);

					}
					updateButtons();
				}
			}

			@Override
			public ItemStack getItem() {

				return null;
			}

			@Override
			public ItemStack getItem(final Object object) {

				if (object instanceof Particle || object instanceof Effect) {
					GuiTempletsYaml gui = guiTemplets.menuKey("Particle_list").placeholders(bountifyCapitalized(object)).build();

					final ContainerDataBuilder data = containerDataCache.getCacheContainerData(container);
					boolean containsEffect = containerDataCache.containsParticleEffect(data, object);
					if (containsEffect)
						gui = guiTemplets.menuKey("Particle_list_selected").placeholders(bountifyCapitalized(object)).build();


					return CreateItemUtily.of(particleEffectList.checkParticleList(object),
							gui.getDisplayName(),
							gui.getLore()).setGlow(containsEffect).makeItemStack();
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

	public Map<Object, ParticleEffect> setParticelData(Player player, ContainerDataBuilder data, String container, Object object) {
		if ((object instanceof Particle && ((Particle) object).getDataType() != Void.class) || getEffectType(String.valueOf(object)) != getEffectType("PARTICLE"))
			new ParticleSettings(container, object).menuOpen(player);
		Map<Object, ParticleEffect> particleEffect = data.getParticleEffects();
		ParticleEffect.Builder particleBuilder = new ParticleEffect.Builder();

		if (!particleEffect.isEmpty())
			player.sendMessage("Your added effects before " + particleEffect.values().stream().map(effect -> effect.getParticle() != null ? effect.getParticle() : effect.getEffect()).collect(Collectors.toList()) + " ,new effect added " + object);

		if (object instanceof Particle) {
			particleBuilder.setParticle((Particle) object);
			particleBuilder.setDataType(((Particle) object).getDataType());
		}
		if (object instanceof Effect) {
			particleBuilder.setEffect((Effect) object);
			particleBuilder.setDataType(((Effect) object).getData());
		}

		if (containerDataCache.containsParticleEffect(data, object)) {
			player.sendMessage("You not change this " + object + " effect from the the old one in list");
		} else
			particleEffect.put(object, particleBuilder.build());

		return particleEffect;
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
