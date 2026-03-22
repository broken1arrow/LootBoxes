package org.brokenarrow.lootboxes.listener;

import org.broken.arrow.library.menu.utility.ServerVersion;
import org.broken.arrow.library.nbt.RegisterNbtAPI;
import org.brokenarrow.lootboxes.Lootboxes;
import org.brokenarrow.lootboxes.builder.ContainerData;
import org.brokenarrow.lootboxes.builder.LocationData;
import org.brokenarrow.lootboxes.builder.LootContainerData;
import org.brokenarrow.lootboxes.lootdata.ContainerDataCache;
import org.brokenarrow.lootboxes.lootdata.LootContainerRandomCache;
import org.brokenarrow.lootboxes.settings.Settings;
import org.brokenarrow.lootboxes.untlity.LootContainer;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

public class CloseContainer implements Listener {

    private final ContainerDataCache containerDataCache = Lootboxes.getInstance().getContainerDataCache();
    private final Lootboxes lootboxes = Lootboxes.getInstance();
    private final Settings settings = Lootboxes.getInstance().getSettings();
    private final RegisterNbtAPI nbt = lootboxes.getNbtAPI();


    @EventHandler
    public void closeLootContainer(InventoryCloseEvent event) {
        Player player = (Player) event.getPlayer();
        if (player.hasPermission("lootboxes.bypass.open.requirement")) return;

        Location location;
        Inventory inventory = event.getInventory();
        if (inventory.getHolder() instanceof LootContainer) {
            location = ((LootContainer) inventory.getHolder()).getLocation();
        } else
            location = this.getLocation(event);
        if (location == null) return;
        clearRandomSawedContainers(location, inventory);
        clearFixedContainer(location, inventory);

    }

    private void clearRandomSawedContainers(Location location, Inventory inventory) {
        LootContainerRandomCache.RandomLootData randomLootContainer = Lootboxes.getInstance().getLootContainerRandomCache().getCachedLootContainerLocation(location);
        if (randomLootContainer != null) {
            if (settings.getSettingsData().isRemoveContainerWhenPlayerClose()) {
                location.getBlock().setType(Material.AIR);
            }
            if (inventory.getContents().length > 0) {
                for (ItemStack itemStack : inventory) {
                    if (itemStack == null) continue;
                    location.getWorld().dropItemNaturally(location, itemStack);
                }
            }
            containerDataCache.write(randomLootContainer.getContainerKey(), lootContainer -> {
                ContainerData lootData = lootContainer.getRandomLootData();
                if (lootData != null) {
                    lootData.setContents(null);
                }
            });
            inventory.clear();
            Lootboxes.getInstance().getLootContainerRandomCache().removeCachedLootContainerLocation(location);
        }
    }

    private void clearFixedContainer(Location location, Inventory inventory) {
        LocationData locationData = containerDataCache.getContainerLocationCache().getLocationData(location);
        if (locationData == null) return;
        LootContainerData lootContainerData = containerDataCache.getCacheContainerData(locationData.getContainerKey());
        if (lootContainerData == null) return;
        ContainerData containerData = lootContainerData.getLinkedContainerData(location);
        if (containerData == null) {
            return;
        }

        if (settings.getSettingsData().isRemoveContainerWhenPlayerClose() && lootContainerData.isSpawningContainerWithCooldown()) {
            location.getBlock().setType(Material.AIR);
        }

        ItemStack[] contents = inventory.getContents();
        if (containerData.getContainerContents() != null) {
            contents = containerData.getContainerContents();
        }

        if (contents != null) {
            for (ItemStack itemStack : contents) {
                if (itemStack == null) continue;
                location.getWorld().dropItemNaturally(location, itemStack);
            }
        }
        containerData.setContents(null);
        inventory.clear();
    }

    @Nullable
    private Location getLocation(final InventoryCloseEvent event) {
        if (ServerVersion.newerThan(9.0))
            return event.getInventory().getLocation();
        else {
            final org.bukkit.inventory.InventoryHolder holder = event.getInventory().getHolder();
            if (holder == null) return null;

            if (holder instanceof Chest)
                return ((Chest) holder).getLocation();
            if (holder instanceof Hopper)
                return ((Hopper) holder).getLocation();
            if (holder instanceof Dropper)
                return ((Dropper) holder).getLocation();
            if (holder instanceof Dispenser)
                return ((Dispenser) holder).getLocation();
            if (ServerVersion.newerThan(13.2))
                if (holder instanceof Barrel)
                    return ((Barrel) holder).getLocation();
        }
        return null;
    }

    @Nullable
    private Location getSourceLocation(final InventoryMoveItemEvent event) {
        if (ServerVersion.newerThan(9.0))
            return event.getSource().getLocation();
        else {
            final org.bukkit.inventory.InventoryHolder holder = event.getSource().getHolder();
            if (holder instanceof Chest)
                return ((Chest) holder).getLocation();
            if (holder instanceof Hopper)
                return ((Hopper) holder).getLocation();
            if (holder instanceof Dropper)
                return ((Dropper) holder).getLocation();
            if (holder instanceof Dispenser)
                return ((Dispenser) holder).getLocation();
            if (ServerVersion.newerThan(13.2))
                if (holder instanceof Barrel)
                    return ((Barrel) holder).getLocation();
        }
        return null;
    }

}


