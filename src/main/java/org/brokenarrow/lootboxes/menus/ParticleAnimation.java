package org.brokenarrow.lootboxes.menus;

import org.broken.arrow.menu.button.manager.library.utility.MenuButtonData;
import org.broken.arrow.menu.button.manager.library.utility.MenuTemplate;
import org.broken.arrow.menu.library.button.MenuButton;
import org.broken.arrow.menu.library.holder.MenuHolder;
import org.brokenarrow.lootboxes.Lootboxes;
import org.brokenarrow.lootboxes.builder.ContainerDataBuilder;
import org.brokenarrow.lootboxes.builder.ParticleEffect;
import org.brokenarrow.lootboxes.commandprompt.SeachInMenu;
import org.brokenarrow.lootboxes.effects.SpawnContainerEffectsTask;
import org.brokenarrow.lootboxes.lootdata.ContainerDataCache;
import org.brokenarrow.lootboxes.menus.containerdata.AlterContainerDataMenu;
import org.brokenarrow.lootboxes.untlity.CreateItemUtily;
import org.brokenarrow.lootboxes.untlity.ParticleEffectList;
import org.brokenarrow.lootboxes.untlity.TranslatePlaceHolders;
import org.brokenarrow.lootboxes.untlity.particles.SpigotParticle;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

import static org.brokenarrow.lootboxes.menus.MenuKeys.PARTICLE_ANIMANTION;
import static org.brokenarrow.lootboxes.untlity.BountifyStrings.bountifyCapitalized;
import static org.brokenarrow.lootboxes.untlity.ConvertParticlesUnity.getEffectType;

public class ParticleAnimation extends MenuHolder {
	private final ContainerDataCache containerDataCache = ContainerDataCache.getInstance();
	private final SpawnContainerEffectsTask spawnContainerEffectsTask = Lootboxes.getInstance().getSpawnContainerEffectsTask();
	private final String container;
	private final MenuTemplate guiTemplate;
	private final ParticleEffectList particleEffectList;

	public ParticleAnimation(final String container, final String particleToSearchFor) {
		super(Lootboxes.getInstance().getParticleEffectList().getParticleList(particleToSearchFor));
		this.container = container;
		this.particleEffectList = Lootboxes.getInstance().getParticleEffectList();
		//gui = new GuiTemplesYaml.Builder(getViewer(), "Particle_Animantion").placeholders("");

		this.guiTemplate = Lootboxes.getInstance().getMenu("Particle_animation");
		if (guiTemplate != null) {
			setFillSpace(guiTemplate.getFillSlots());
			setMenuSize(guiTemplate.getinvSize("Particle_animation"));
			setTitle(() -> TranslatePlaceHolders.translatePlaceholders(guiTemplate.getMenuTitle(), ""));
			setMenuOpenSound(null);
			this.setUseColorConversion(true);
			//setMenuOpenSound(guiTemplate.getSound());
		} else {
			setMenuSize(36);
			setTitle(() -> "could not load menu 'Particle_animation'.");
		}
	}

	@Override
	public MenuButton getFillButtonAt(@NotNull Object object) {
		MenuButtonData button = this.guiTemplate.getMenuButton(-1);
		if (button == null) return null;
		//ParticleEffectList particleEffectList = null;
		//particleEffectList = Lootboxes.getInstance().getParticleEffectList();

		return new MenuButton() {
			@Override
			public void onClickInsideMenu(@NotNull final Player player, @NotNull final Inventory menu, @NotNull final ClickType click, @NotNull final ItemStack clickedItem, final Object object) {
				if (particleEffectList.checkIfParticleOrEffect(object)) {
					final ContainerDataBuilder data = containerDataCache.getCacheContainerData(container);
					final ContainerDataBuilder.Builder builder = data.getBuilder();
					if (click.isRightClick()) {
						containerDataCache.removeParticleEffect(data, object);
					} else {
						builder.setParticleEffects(setParticleData(player, data, container, object));
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
				org.broken.arrow.menu.button.manager.library.utility.MenuButton menuButton = button.getPassiveButton();
				if (particleEffectList.checkIfParticleOrEffect(object)) {

					final ContainerDataBuilder data = containerDataCache.getCacheContainerData(container);
					boolean containsEffect = containerDataCache.containsParticleEffect(data, object);
					if (containsEffect)
						menuButton = button.getActiveButton();
					if (menuButton == null)
						menuButton = button.getPassiveButton();

					String displayName = TranslatePlaceHolders.translatePlaceholders(player, menuButton.getDisplayName(), "", bountifyCapitalized(object));

					return CreateItemUtily.of(particleEffectList.checkParticleList(object),
									displayName,
									TranslatePlaceHolders.translatePlaceholdersLore(player, menuButton.getLore()))
							.setGlow(containsEffect).makeItemStack();
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
						.setGlow(menuButton.isGlow())
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
		if (button.isActionTypeEqual("Search")) {
			if (click.isLeftClick())
				new SeachInMenu(PARTICLE_ANIMANTION, PARTICLE_ANIMANTION, container, "").start(player);
			else
				new ParticleAnimation(container, "").menuOpen(player);
		}

		if (button.isActionTypeEqual("Back_button")) {
			new AlterContainerDataMenu(container).menuOpen(player);
		}
		return false;
	}

	public Map<Object, ParticleEffect> setParticleData(Player player, ContainerDataBuilder data, String container, Object object) {
		if ((particleEffectList.checkIfParticle(object) && !particleEffectList.checkParticleClass(object, Void.class)) || getEffectType(String.valueOf(object)) != getEffectType("PARTICLE"))
			new ParticleSettings(container, object).menuOpen(player);
		Map<Object, ParticleEffect> particleEffect = data.getParticleEffects();
		ParticleEffect.Builder particleBuilder = new ParticleEffect.Builder();

		if (particleEffect != null && !particleEffect.isEmpty())
			player.sendMessage("Your added effects before " + particleEffectList.getListOfParticles(particleEffect.values()) + " ,new effect added " + object);

		if (particleEffectList.checkIfParticle(object)) {
			particleBuilder.setSpigotParticle(new SpigotParticle(((Particle) object)));
			particleBuilder.setDataType(((Particle) object).getDataType());
		}
		if (object instanceof Effect) {
			particleBuilder.setEffect((Effect) object);
			particleBuilder.setDataType(((Effect) object).getData());
		}

		if (containerDataCache.containsParticleEffect(data, object)) {
			player.sendMessage("You not change this " + object + " effect from the the old one in list");
		} else {
			if (particleEffect == null)
				particleEffect = new HashMap<>();
			particleEffect.put(object, particleBuilder.build());
		}
		return particleEffect;
	}

}
