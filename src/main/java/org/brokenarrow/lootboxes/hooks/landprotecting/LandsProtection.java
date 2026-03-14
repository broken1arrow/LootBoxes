package org.brokenarrow.lootboxes.hooks.landprotecting;


import me.angeschossen.lands.api.LandsIntegration;
import me.angeschossen.lands.api.exceptions.FlagConflictException;
import me.angeschossen.lands.api.flags.enums.FlagModule;
import me.angeschossen.lands.api.flags.enums.FlagTarget;
import me.angeschossen.lands.api.flags.type.NaturalFlag;
import me.angeschossen.lands.api.flags.type.parent.DefaultStateFlag;
import me.angeschossen.lands.api.land.Area;
import me.angeschossen.lands.api.land.LandWorld;
import me.angeschossen.lands.api.player.LandPlayer;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class LandsProtection implements ProtectingProvider {
    private LandsIntegration lands;
    private final Plugin plugin;
    private LandsFlag landsFlag;

    public LandsProtection(Plugin plugin) {
        lands = LandsIntegration.of(plugin);
        this.plugin = plugin;
        try {
            landsFlag = new LandsFlag(plugin);
            lands.getFlagRegistry().register(landsFlag);
        } catch (FlagConflictException exception) {
            exception.printStackTrace();
        }
    }

    @Override
    public boolean isAllowedToSpawnContainer(Location location) {
        /*Area landsArea = lands.getArea(location);
        if (landsArea != null) {
            //return false;//lands.getLandWorld(location.getWorld()).hasFlag(location, (LandFlag) flag);
            return landsArea.hasNaturalFlag(landsFlag);
        } else*/ {
            LandWorld world = lands.getWorld(location.getWorld());
            return world != null && world.hasNaturalFlag(location, landsFlag);
        }
    }

    public class LandsFlag implements NaturalFlag {
        private final Plugin plugin;

        public LandsFlag(final Plugin plugin) {
            this.plugin = plugin;
        }

        @Override
        public boolean getDefaultState() {
            return true;
        }

        @Override
        public @NotNull DefaultStateFlag<NaturalFlag> setDefaultState(final boolean state) {
            return this;
        }

        @Override
        public boolean isAutoDisable() {
            return false;
        }

        @Override
        public @NotNull NaturalFlag setAutoDisable(final boolean autoDisable) {
            return this;
        }

        @Override
        public @NotNull Plugin getPlugin() {
            return plugin;
        }

        @Override
        public boolean shouldDisplay(@Nullable final Area area, @Nullable final LandPlayer landPlayer) {
            return false;
        }

        @Override
        public @NotNull ItemStack getIcon() {
            return new ItemStack(Material.CHEST);
        }

        @Override
        public @NotNull NaturalFlag setIcon(@Nullable final ItemStack icon) {
            return this;
        }

        @Override
        public boolean isDisplayInWilderness() {
            return false;
        }

        @Override
        public boolean isAlwaysAllowInWilderness() {
            return false;
        }

        @Override
        public @NotNull NaturalFlag setAlwaysAllowInWilderness(final boolean allow) {
            return this;
        }

        @Override
        public boolean isApplyInSubareas() {
            return false;
        }

        @Override
        public @NotNull NaturalFlag setApplyInSubareas(final boolean set) {
            return this;
        }

        @Override
        public @Nullable List<String> getDescription() {
            List<String> description = new ArrayList<>();
            description.add("Allow or disallow containers spawn here.");
            return description;
        }

        @Override
        public @NotNull NaturalFlag setDescription(@Nullable final List<String> description) {
            return this;
        }

        @Override
        public @NotNull NaturalFlag setDescription(@Nullable final String description) {
            return this;
        }

        @Override
        public boolean isDisplay() {
            return false;
        }

        @Override
        public @NotNull NaturalFlag setDisplay(final boolean display) {
            return this;
        }

        @Override
        public @NotNull String getDisplayName() {
            return " ";
        }

        @Override
        public @NotNull NaturalFlag setDisplayName(@Nullable final String displayName) {
            return this;
        }

        @Override
        public @NotNull String getName() {
            return "LootBoxes";
        }

        @Override
        public boolean isActiveInWar() {
            return false;
        }

        @Override
        public @NotNull NaturalFlag setActiveInWar(final boolean activeInWar) {
            return this;
        }

        @Override
        public @NotNull String getTogglePermission() {
            return "";
        }

        @Override
        public @NotNull FlagModule getModule() {
            return FlagModule.LAND;
        }

        @Override
        public @NotNull FlagTarget getTarget() {
            return FlagTarget.ADMIN;
        }
    }

}
