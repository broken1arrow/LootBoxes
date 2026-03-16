package org.brokenarrow.lootboxes.hooks.landprotecting;

import me.angeschossen.lands.api.LandsIntegration;
import me.angeschossen.lands.api.exceptions.FlagConflictException;
import me.angeschossen.lands.api.flags.enums.FlagTarget;
import me.angeschossen.lands.api.flags.type.NaturalFlag;
import me.angeschossen.lands.api.land.LandWorld;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;

public class LandsProtection implements ProtectingProvider {
    private LandsIntegration lands;
    private final Plugin plugin;
    private NaturalFlag flag;

    public LandsProtection(Plugin plugin) {
        lands = LandsIntegration.of(plugin);
        this.plugin = plugin;

        NaturalFlag flag = lands.getFlagRegistry().getNatural("LootBoxes");
        if (flag == null) {
            flag = NaturalFlag.of(lands, FlagTarget.ADMIN, "LootBoxes");
            try {
                lands.getFlagRegistry().register(flag);
            } catch (FlagConflictException exception) {
                //exception.printStackTrace();
            }
        }
        List<String> description = new ArrayList<>();
        description.add("Allow or disallow containers spawn here.");
        flag.setDisplayName("Loot boxes")
                .setIcon(new ItemStack(Material.CHEST))
                .setDescription(description)
                .setDefaultState(true);
        this.flag = flag;

    }

    @Override
    public boolean isAllowedToSpawnContainer(Location location) {
        /*Area landsArea = lands.getArea(location);
        if (landsArea != null) {
            //return false;//lands.getLandWorld(location.getWorld()).hasFlag(location, (LandFlag) flag);
            return landsArea.hasNaturalFlag(landsFlag);
        } else*/
        {
            LandWorld world = lands.getWorld(location.getWorld());
            return world != null && world.hasNaturalFlag(location, flag);
        }
    }
}
