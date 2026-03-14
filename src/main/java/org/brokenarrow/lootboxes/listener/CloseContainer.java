package org.brokenarrow.lootboxes.listener;

import org.broken.arrow.library.menu.utility.ServerVersion;
import org.broken.arrow.library.nbt.RegisterNbtAPI;
import org.brokenarrow.lootboxes.Lootboxes;
import org.brokenarrow.lootboxes.builder.ContainerData;
import org.brokenarrow.lootboxes.builder.LootContainerData;
import org.brokenarrow.lootboxes.builder.LocationData;
import org.brokenarrow.lootboxes.lootdata.ContainerDataCache;
import org.brokenarrow.lootboxes.settings.Settings;
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

import java.util.Map;

import static org.brokenarrow.lootboxes.untlity.BlockChecks.getInventory;

public class CloseContainer implements Listener {

	private final ContainerDataCache containerDataCache = Lootboxes.getInstance().getContainerDataCache();
	private final Lootboxes lootboxes = Lootboxes.getInstance();
	private final Settings settings = Lootboxes.getInstance().getSettings();
	private final RegisterNbtAPI nbt = lootboxes.getNbtAPI();


	@EventHandler
	public void closeLootContainer(InventoryCloseEvent event) {
		Location location = this.getLocation(event);
		if (location == null) return;
		Player player = (Player) event.getPlayer();
		if (player.hasPermission("lootboxes.bypass.open.requirement")) return;

		Inventory inventory = getInventory(location);
		if (inventory != null) {
			clearRandomSawedContainers(location, inventory);
			clearFixedContainer(location, inventory);

		}
	}

	private void clearRandomSawedContainers(Location location, Inventory inventory) {
		String randomLootContainer = Lootboxes.getInstance().getLootContainerRandomCache().getCachedLootContainerLocation(location);
		if(randomLootContainer != null){
			if (settings.getSettingsData().isRemoveContainerWhenPlayerClose()) {
				location.getBlock().setType(Material.AIR);
			}
			if (inventory.getContents().length > 0) {
				for (ItemStack itemStack : inventory) {
					if (itemStack == null) continue;
					location.getWorld().dropItemNaturally(location, itemStack);
				}
			}
			inventory.clear();
			Lootboxes.getInstance().getLootContainerRandomCache().removeCachedLootContainerLocation(location);
		}
	}

	private void clearFixedContainer(Location location, Inventory inventory) {
		LocationData locationData = containerDataCache.getContainerLocationCache().getLocationData(location);
		if (locationData == null) return;
		LootContainerData lootContainerData = containerDataCache.getCacheContainerData(locationData.getContainerKey());
		if (lootContainerData == null) return;
		Map<Location, ContainerData> containerData = lootContainerData.getLinkedContainerData();
		if (containerData == null || containerData.get(location) == null) {
			return;
		}

		if (settings.getSettingsData().isRemoveContainerWhenPlayerClose() && lootContainerData.isSpawningContainerWithCooldown()) {
			location.getBlock().setType(Material.AIR);

		}
		if (inventory.getContents().length > 0) {
			for (ItemStack itemStack : inventory) {
				if (itemStack == null) continue;
				location.getWorld().dropItemNaturally(location, itemStack);
			}
		}
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


