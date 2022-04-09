package org.brokenarrow.lootboxes.tasks;

import org.brokenarrow.lootboxes.Lootboxes;
import org.brokenarrow.lootboxes.builder.ContainerDataBuilder;
import org.brokenarrow.lootboxes.builder.SettingsData;
import org.brokenarrow.lootboxes.lootdata.ContainerData;
import org.brokenarrow.lootboxes.settings.Settings;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.Map;

import static org.brokenarrow.lootboxes.untlity.ModifyBlock.*;
import static org.brokenarrow.lootboxes.untlity.RandomUntility.randomIntNumber;

public class SpawnContainerRandomLoc {

	private final SettingsData settings = Settings.getInstance().getSettings();
	private long time;
	private final ContainerData containerDataInstance = ContainerData.getInstance();
	private final Lootboxes lootboxes = Lootboxes.getInstance();

	public void task() {
		if (Lootboxes.getInstance().getSettings().getSettings().isRandomContinerSpawn())
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
		Location clone = location.clone();

		int x = clone.getBlockX();
		int y = clone.getBlockY();
		int z = clone.getBlockZ();
		int blocksAwayFromPlayer = 10;
		int amountToCheck = 5;
		Location loc = checkLocation(location, amountToCheck, blocksAwayFromPlayer, player);

		System.out.println("loc " + loc);
		if (loc != null) {
			ContainerDataBuilder containerDataBuilder = containerDataInstance.getCacheContainerData("Global_Container");
			spawnContainer(containerDataBuilder, loc);
		}

	}

	public void spawnContainer(ContainerDataBuilder containerData, Location location) {
		Map<Location, ContainerDataBuilder.ContainerData> containerDataMap = containerData.getLinkedContainerData();
		String lootTableLinked = containerData.getLootTableLinked();
		for (Map.Entry<Location, ContainerDataBuilder.ContainerData> entry : containerDataMap.entrySet()) {
			ContainerDataBuilder.ContainerData containerData1 = entry.getValue();
			if (location != null && lootTableLinked != null && !lootTableLinked.isEmpty()) {

				location.getBlock().setType(containerData1.getContainerType());
				setRotation(location, containerData1.getFacing());
				setCustomName(location);

				ItemStack[] item = this.lootboxes.getMakeLootTable().makeLottable(lootTableLinked);
				System.out.println("loootTable ===" + Arrays.toString(item));

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

/*
		final double randomRadius = random.nextDouble() * 30;
		final double theta = Math.toRadians(random.nextDouble() * 360);
		final double phi = Math.toRadians(random.nextDouble() * 180 - 90);

		final double xx = randomRadius * Math.cos(theta) * Math.sin(phi);
		final double zz = randomRadius * Math.cos(phi);

		System.out.println("xx " + xx);
		System.out.println("zz " + zz);
		double y1 = randomRadius * Math.sin(theta) * Math.cos(phi);
*/

		int randomY = randomIntNumber(-20, 20);
		int randomX = randomIntNumber(-20, 20);
		int randomZ = randomIntNumber(-20, 20);

		int angleX = (int) (((blocksAwayFromPlayer * randomX)) * Math.PI / (45));
		int angleZ = (int) (((blocksAwayFromPlayer * randomZ)) * Math.PI / (45));

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
					loc.getWorld().spawnParticle(Particle.BLOCK_MARKER, loc, 1, Material.BARRIER.createBlockData());

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
					loc.getWorld().spawnParticle(Particle.BLOCK_MARKER, loc, 1, Material.BARRIER.createBlockData());

					if (loc.getBlockX() == playerLocation.getBlockX() && loc.getBlockZ() == playerLocation.getBlockZ() && loc.getBlockY() == playerLocation.getBlockY()) {
						hasNearbyPlayer = true;
						return true;
					}
				}

		return hasNearbyPlayer;
	}
}