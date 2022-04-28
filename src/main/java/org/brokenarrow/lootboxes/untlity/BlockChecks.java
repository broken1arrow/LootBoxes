package org.brokenarrow.lootboxes.untlity;

import org.bukkit.Location;
import org.bukkit.block.*;
import org.bukkit.inventory.Inventory;

public class BlockChecks {

	/**
	 * Get the inventory on the location.
	 *
	 * @param location you want to get the inventory.
	 * @return inventory or null if it not find inventory.
	 */
	public static Inventory getInventory(Location location) {
		Block block = location.getBlock();
		if (!checkBlockIsContainer(block)) return null;

		Inventory inventory = null;
		switch (block.getType()) {
			case HOPPER:
				inventory = ((Hopper) block.getState()).getInventory();
				break;
			case DISPENSER:
				inventory = ((Dispenser) block.getState()).getInventory();
				break;
			case DROPPER:
				inventory = ((Dropper) block.getState()).getInventory();
				break;
			case BARREL:
				inventory = ((Barrel) block.getState()).getInventory();
				break;
			case CHEST:
			case TRAPPED_CHEST:
				inventory = ((Chest) block.getState()).getInventory();
				break;
			default:
				break;
		}
		return inventory;
	}

	/**
	 * Get the inventory of the block.
	 *
	 * @param block you want to get the inventory.
	 * @return inventory or null if it not find inventory.
	 */
	public static Inventory getInventory(Block block) {

		if (!checkBlockIsContainer(block)) return null;

		Inventory inventory = null;
		switch (block.getType()) {
			case HOPPER:
				inventory = ((Hopper) block.getState()).getInventory();
				break;
			case DISPENSER:
				inventory = ((Dispenser) block.getState()).getInventory();
				break;
			case DROPPER:
				inventory = ((Dropper) block.getState()).getInventory();
				break;
			case BARREL:
				inventory = ((Barrel) block.getState()).getInventory();
				break;
			case CHEST:
			case TRAPPED_CHEST:
				inventory = ((Chest) block.getState()).getInventory();
				break;
			default:
				break;
		}
		return inventory;
	}

	/**
	 * Check if it valid container some can store items.
	 *
	 * @param block block you want to check
	 * @return true if it container.
	 */
	public static boolean checkBlockIsContainer(Block block) {
		if (block == null) return false;
		
		switch (block.getType()) {
			case HOPPER:
			case DISPENSER:
			case DROPPER:
			case BARREL:
			case CHEST:
			case TRAPPED_CHEST:
				return true;
			default:
				return false;
		}

	}
}
