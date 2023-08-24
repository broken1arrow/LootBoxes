package org.brokenarrow.lootboxes.listener;

import org.broken.arrow.nbt.library.RegisterNbtAPI;
import org.broken.arrow.nbt.library.utility.ServerVersion;
import org.brokenarrow.lootboxes.Lootboxes;
import org.brokenarrow.lootboxes.builder.ContainerDataBuilder;
import org.brokenarrow.lootboxes.builder.LocationData;
import org.brokenarrow.lootboxes.lootdata.ContainerDataCache;
import org.brokenarrow.lootboxes.settings.Settings;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Barrel;
import org.bukkit.block.Chest;
import org.bukkit.block.Dispenser;
import org.bukkit.block.Dropper;
import org.bukkit.block.Hopper;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import static org.brokenarrow.lootboxes.untlity.BlockChecks.getInventory;

public class CloseContainer implements Listener {

	private final ContainerDataCache containerDataCache = ContainerDataCache.getInstance();
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
			LocationData locationData = containerDataCache.getLocationData(location);
			if (locationData == null) return;
			ContainerDataBuilder containerData = containerDataCache.getCacheContainerData(locationData.getContinerData());
			if (containerData == null) return;

			if (settings.getSettingsData().isRemoveContainerWhenPlayerClose() && containerData.isSpawningContainerWithCooldown()) {
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
	}

	@Nullable
	private Location getLocation(final InventoryCloseEvent event) {
		if (ServerVersion.newerThan(ServerVersion.v1_9))
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
			if (ServerVersion.newerThan(ServerVersion.v1_13))
				if (holder instanceof Barrel)
					return ((Barrel) holder).getLocation();
		}
		return null;
	}

	@Nullable
	private Location getSourceLocation(final InventoryMoveItemEvent event) {
		if (ServerVersion.newerThan(ServerVersion.v1_9))
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
			if (ServerVersion.newerThan(ServerVersion.v1_13))
				if (holder instanceof Barrel)
					return ((Barrel) holder).getLocation();
		}
		return null;
	}

}


