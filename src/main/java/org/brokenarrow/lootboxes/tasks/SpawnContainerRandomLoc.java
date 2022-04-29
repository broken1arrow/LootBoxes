package org.brokenarrow.lootboxes.tasks;

import org.brokenarrow.lootboxes.Lootboxes;
import org.brokenarrow.lootboxes.builder.ContainerData;
import org.brokenarrow.lootboxes.builder.ContainerDataBuilder;
import org.brokenarrow.lootboxes.builder.SettingsData;
import org.brokenarrow.lootboxes.lootdata.ContainerDataCache;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

import static org.brokenarrow.lootboxes.untlity.ModifyBlock.*;
import static org.brokenarrow.lootboxes.untlity.RandomUntility.randomIntNumber;

public class SpawnContainerRandomLoc {

	private final SettingsData settings = Lootboxes.getInstance().getSettings().getSettings();
	private long time;
	private final ContainerDataCache containerDataCacheInstance = ContainerDataCache.getInstance();
	private final Lootboxes lootboxes = Lootboxes.getInstance();

	public void task() {
		if (settings.isRandomContinerSpawn())
			if (this.time == 0)
				this.time = System.currentTimeMillis() + (1000 * 5);
			else if (System.currentTimeMillis() >= this.time) {
				for (Player player : Bukkit.getOnlinePlayers()) {
					Location location = player.getLocation();
					spawnBlock(location, player);
				}

				this.time = System.currentTimeMillis() + (1000 * 5);
			}
	}

	public void spawnBlock(Location location, Player player) {

		int blocksAwayFromPlayer = 10;
		int amountToCheck = 5;
		Location loc = checkLocation(location, amountToCheck, blocksAwayFromPlayer, player);

		System.out.println("loc " + loc);
		if (loc != null) {
			ContainerDataBuilder containerDataBuilder = containerDataCacheInstance.getCacheContainerData("Global_Container");
			spawnContainer(containerDataBuilder, loc);
		}

	}

	public void spawnContainer(ContainerDataBuilder containerData, Location location) {
		Map<Location, org.brokenarrow.lootboxes.builder.ContainerData> containerDataMap = containerData.getLinkedContainerData();
		String lootTableLinked = containerData.getLootTableLinked();
		for (Map.Entry<Location, ContainerData> entry : containerDataMap.entrySet()) {
			ContainerData container = entry.getValue();
			if (location != null && lootTableLinked != null && !lootTableLinked.isEmpty()) {

				location.getBlock().setType(container.getContainerType());
				setRotation(location, container.getFacing());
				setCustomName(location, containerData.getDisplayname());

				ItemStack[] item = this.lootboxes.getMakeLootTable().makeLottable(lootTableLinked);

				Inventory inventory = getInventory(location);
				if (inventory != null) {
					inventory.setContents(item);
				}
			}
		}
	}

	private Location checkLocation(Location location, int amountToCheck, int blocksAwayFromPlayer, Player player) {
		int x = location.getBlockX();
		int y = location.getBlockY();
		int z = location.getBlockZ();


		int randomY = randomIntNumber(-20, 20);
		int randomX = randomIntNumber(-20, 20);
		int randomZ = randomIntNumber(-20, 20);


		int numberX = x + randomX;
		int numberY = y + randomY;
		int numberZ = z + randomZ;

		Location locationSubtracted = new Location(location.getWorld(), numberX, numberY, numberZ);

		if (this.settings != null && this.settings.isSpawnOnSurface()) {
			World world = location.getWorld();
			int highestBlock = world != null ? world.getHighestBlockAt(location).getLocation().getBlockY() : 0;
			return new Location(location.getWorld(), numberX, highestBlock + 1, numberZ);
		}
		if (checkIfLocationAreValid(locationSubtracted, numberY, amountToCheck, player))
			return locationSubtracted;

		return null;
	}

	private boolean checkIfLocationAreValid(Location location, int hight, int amountOfBlocksBetweenContainers, Player player) {
		World world = location.getWorld();
		int highestBlock = world != null ? world.getHighestBlockAt(location).getLocation().getBlockY() : 0;

		if (this.settings != null && this.settings.getAmountOfBlocksBelowSurface() > 0)
			hight = hight + this.settings.getAmountOfBlocksBelowSurface();

		if (hight < highestBlock && !location.getBlock().isLiquid() && !checkBlock(location.getBlock()) &&
				!isNearbyChest(location, amountOfBlocksBetweenContainers) &&
				!isNearbyPlayer(player, location, 20))
			return true;

		return false;
	}

	private boolean checkBlock(Block block) {
		switch (block.getType()) {
			case HOPPER:
			case DISPENSER:
			case DROPPER:
			case BARREL:
			case CHEST:
			case TRAPPED_CHEST:
			case BEACON:
			case IRON_BLOCK:
			case GOLD_BLOCK:
			case DIAMOND_BLOCK:
				return true;
			default:
				return false;

		}
	}

	public boolean isNearbyChest(Location location, int amountOfBlocksBetweenContainers) {
		boolean hasNearbyChest = false;
		double amountOfBlocksToCheck;
		for (int X = 1; X <= amountOfBlocksBetweenContainers; X++)
			for (int Y = 1; Y <= amountOfBlocksBetweenContainers; Y++)
				for (int Z = 1; Z <= amountOfBlocksBetweenContainers; Z++) {
					if (amountOfBlocksBetweenContainers % 2 == 0)
						amountOfBlocksToCheck = amountOfBlocksBetweenContainers / 2.0;
					else
						amountOfBlocksToCheck = (amountOfBlocksBetweenContainers / 2.0) + 1;

					Location cloneLoc = location.clone().add(amountOfBlocksToCheck, amountOfBlocksToCheck, amountOfBlocksToCheck);
					Location loc = cloneLoc.subtract(X, Y, Z);

					if (checkBlock(loc.getBlock())) {
						hasNearbyChest = true;
					}
				}

		return hasNearbyChest;
	}

	public boolean isNearbyPlayer(Player player, Location location, int amountAwayFromPlayer) {
		boolean hasNearbyPlayer = false;
		double amountOfBlocksToCheck;
		Location playerLocation = player.getLocation();
		for (int X = 1; X <= amountAwayFromPlayer; X++)
			for (int Y = 1; Y <= amountAwayFromPlayer; Y++)
				for (int Z = 1; Z <= amountAwayFromPlayer; Z++) {
					if (amountAwayFromPlayer % 2 == 0)
						amountOfBlocksToCheck = amountAwayFromPlayer / 2.0;
					else
						amountOfBlocksToCheck = (amountAwayFromPlayer / 2.0) + 1;

					Location cloneLoc = location.clone().add(amountOfBlocksToCheck, amountOfBlocksToCheck, amountOfBlocksToCheck);
					Location loc = cloneLoc.subtract(X, Y, Z);

					if (loc.getBlockX() == playerLocation.getBlockX() && loc.getBlockZ() == playerLocation.getBlockZ() && loc.getBlockY() == playerLocation.getBlockY()) {
						hasNearbyPlayer = true;
						return true;
					}
				}

		return hasNearbyPlayer;
	}
}
