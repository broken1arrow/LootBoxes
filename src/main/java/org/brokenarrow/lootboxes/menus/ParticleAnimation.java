package org.brokenarrow.lootboxes.menus;

import org.broken.arrow.library.menu.button.MenuButton;
import org.broken.arrow.library.menu.button.logic.ButtonUpdateAction;
import org.broken.arrow.library.menu.button.logic.FillMenuButton;
import org.broken.arrow.library.menu.button.manager.utility.MenuButtonData;
import org.broken.arrow.library.menu.button.manager.utility.MenuTemplate;
import org.broken.arrow.library.menu.holder.MenuHolderPage;
import org.brokenarrow.lootboxes.Lootboxes;
import org.brokenarrow.lootboxes.builder.LootContainerData;
import org.brokenarrow.lootboxes.builder.ParticleEffect;
import org.brokenarrow.lootboxes.commandprompt.SearchInMenu;
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

public class ParticleAnimation extends MenuHolderPage<Object> {
    private final ContainerDataCache containerDataCache = Lootboxes.getInstance().getContainerDataCache();
    private final SpawnContainerEffectsTask spawnContainerEffectsTask = Lootboxes.getInstance().getSpawnContainerEffectsTask();
    private final String container;
    private final MenuTemplate guiTemplate;
    private final ParticleEffectList particleEffectList;

    public ParticleAnimation(final String container, final String particleToSearchFor) {
        super(Lootboxes.getInstance().getParticleEffectList().getParticleList(particleToSearchFor));
        this.container = container;
        this.particleEffectList = Lootboxes.getInstance().getParticleEffectList();
        this.guiTemplate = Lootboxes.getInstance().getMenu("Particle_animation");

        setUseColorConversion(true);
        setIgnoreItemCheck(true);

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
                org.broken.arrow.library.menu.button.manager.utility.MenuButton menuButton = button.getPassiveButton();

                return CreateItemUtily.of(menuButton.isGlow(), menuButton.getMaterial(),
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
        if (button.isActionTypeEqual("Search")) {
            if (click.isLeftClick())
                new SearchInMenu(PARTICLE_ANIMANTION, PARTICLE_ANIMANTION, container, "").start(player);
            else
                new ParticleAnimation(container, "").menuOpen(player);
        }

        if (button.isActionTypeEqual("Back_button")) {
            new AlterContainerDataMenu(container).menuOpen(player);
        }
        return false;
    }

    public Map<String, ParticleEffect> setParticleData(Player player, LootContainerData data, String container, Object object) {
        if ((particleEffectList.checkIfParticle(object) && !particleEffectList.checkParticleClass(object, Void.class)) || getEffectType(String.valueOf(object)) != getEffectType("PARTICLE"))
            new ParticleSettings(container, object).menuOpen(player);
        Map<String, ParticleEffect> particleEffect = data.getParticleEffects();
        ParticleEffect.Builder particleBuilder = new ParticleEffect.Builder();

        if (particleEffect != null && !particleEffect.isEmpty())
            player.sendMessage("Your added effects before " + particleEffectList.getListOfParticles(particleEffect.values()) + " ,new effect added " + object);
        String particleName = null;
        if (particleEffectList.checkIfParticle(object)) {
            particleBuilder.setSpigotParticle(new SpigotParticle(((Particle) object)));
            particleBuilder.setDataType(((Particle) object).getDataType());
            particleName = ((Particle) object).name();
        }
        if (object instanceof Effect) {
            particleBuilder.setEffect((Effect) object);
            particleBuilder.setDataType(((Effect) object).getData());
            particleName = ((Effect) object).name();
        }

        if (containerDataCache.containsParticleEffect(data, particleName)) {
            player.sendMessage("You not change this " + object + " effect from the the old one in list");
        } else {
            if (particleEffect == null)
                particleEffect = new HashMap<>();
            if (particleName != null)
                particleEffect.put(particleName, particleBuilder.build());
        }
        return particleEffect;
    }

    @Override
    public FillMenuButton<Object> createFillMenuButton() {
        MenuButtonData button = this.guiTemplate.getMenuButton(-1);
        if (button == null) return null;

        return new FillMenuButton<>((player, menu, click, clickedItem, particle) -> {
            if (particleEffectList.checkIfParticleOrEffect(particle)) {
                containerDataCache.write(container, containerBuilder -> {

                    if (click.isRightClick()) {
                        containerDataCache.removeParticleEffect(containerBuilder, particle);
                    } else {
                        containerBuilder.setParticleEffects(setParticleData(player, containerBuilder, container, particle));
                    }
                    if (click.isLeftClick()) {
                        for (final Location location : containerBuilder.getLinkedContainerData().keySet())
                            spawnContainerEffectsTask.addLocationInList(location);
                    }
                });
                return ButtonUpdateAction.ALL;
            }
            return ButtonUpdateAction.NONE;
        }, (slot, particle) -> {
            org.broken.arrow.library.menu.button.manager.utility.MenuButton menuButton = button.getPassiveButton();
            if (particleEffectList.checkIfParticleOrEffect(particle)) {
                boolean containsEffect = containerDataCache.containsParticleEffect(container, particle);
                if (containsEffect)
                    menuButton = button.getActiveButton();
                if (menuButton == null)
                    menuButton = button.getPassiveButton();

                String displayName = TranslatePlaceHolders.translatePlaceholders(player, menuButton.getDisplayName(), "", bountifyCapitalized(particle));

                return CreateItemUtily.of(containsEffect, particleEffectList.checkParticleList(particle),
                                displayName,
                                TranslatePlaceHolders.translatePlaceholdersLore(player, menuButton.getLore()))
                        .makeItemStack();
            }
            return null;
        });
    }
}
