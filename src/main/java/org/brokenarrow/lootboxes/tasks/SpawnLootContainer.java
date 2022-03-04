package org.brokenarrow.lootboxes.tasks;

import org.brokenarrow.lootboxes.builder.SettingsData;
import org.brokenarrow.lootboxes.settings.Settings;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.concurrent.ThreadLocalRandom;

public class SpawnLootContainer {

	private final SettingsData settings = Settings.getInstance().getSettings();
	private long time;

	public void task() {
		if (this.time == 0)
			this.time = System.currentTimeMillis() + (1000 * 5);
		else if (this.time <= System.currentTimeMillis()) {
			for (Player player : Bukkit.getOnlinePlayers()) {
				Location location = player.getLocation();
				spawnBlock(location);
			}
			this.time = System.currentTimeMillis() + (1000 * 5);
		}
	}

	public void spawnBlock(Location location) {
		Location clone = location.clone();

		int x = clone.getBlockX();
		int y = clone.getBlockY();
		int z = clone.getBlockZ();
		int blocksAwayFromPlayer = 10;
		int amountToCheck = 5;
		Location Z = checkLocation(location, amountToCheck, blocksAwayFromPlayer);

		System.out.println("loc " + Z);
		if (Z != null) {
			Z.getBlock().setType(Material.CHEST);
		}

	}

	private Location checkLocation(Location location, int amountToCheck, int blocksAwayFromPlayer) {
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
		int randomY = ThreadLocalRandom.current().nextInt(-50, 60);
		int randomX = ThreadLocalRandom.current().nextInt(-50, 60);
		int randomZ = ThreadLocalRandom.current().nextInt(-50, 60);

		int angleX = (int) (((blocksAwayFromPlayer * randomX)) * Math.PI / (45));
		int angleZ = (int) (((blocksAwayFromPlayer * randomZ)) * Math.PI / (45));

		int numberX = x + angleX;
		int numberY = (y + blocksAwayFromPlayer) + randomY;
		int numberZ = z + angleZ;

		Location locationSubtracted = new Location(location.getWorld(), numberX, numberY, numberZ);

		if (this.settings.isSpawnOnSurface()) {
			World world = location.getWorld();
			int highestBlock = world != null ? world.getHighestBlockAt(location).getLocation().getBlockY() : 0;
			return new Location(location.getWorld(), numberX, highestBlock + 1, numberZ);
		}
		if (checkIfLocationAreValid(locationSubtracted, numberY, amountToCheck))
			return locationSubtracted;

		return null;
	}

	private boolean checkIfLocationAreValid(Location location, int hight, int amountOfBlocksBetweenContainers) {
		World world = location.getWorld();
		int highestBlock = world != null ? world.getHighestBlockAt(location).getLocation().getBlockY() : 0;

		if (this.settings.getAmountOfBlocksBelowSurface() > 0)
			hight = hight + this.settings.getAmountOfBlocksBelowSurface();

		if (hight < highestBlock && !location.getBlock().isLiquid() && !checkBlock(location.getBlock()) &&
				!isNearbyChest(location, amountOfBlocksBetweenContainers))
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
}
