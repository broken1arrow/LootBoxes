package org.brokenarrow.lootboxes.untlity;

import org.broken.arrow.color.library.TextTranslator;
import org.bukkit.Location;
import org.bukkit.block.Barrel;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Chest;
import org.bukkit.block.Dispenser;
import org.bukkit.block.Dropper;
import org.bukkit.block.Hopper;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Directional;
import org.bukkit.inventory.Inventory;

public class ModifyBlock {


	/**
	 * Set custom name on container.
	 *
	 * @param location were coniner is placed.
	 * @param text     the tittle you want to set.
	 */
	public static void setCustomName(Location location, String text) {
		Block block = location.getBlock();
		if (!checkBlocktype(block)) return;
		if (text == null) return;
		text = TextTranslator.toSpigotFormat(text);

		switch (block.getType()) {
			case HOPPER:
				Hopper hopper = ((Hopper) block.getState());
				hopper.setCustomName(text);
				hopper.update();
				break;
			case DISPENSER:
				Dispenser dispenser = ((Dispenser) block.getState());
				dispenser.setCustomName(text);
				dispenser.update();
				break;
			case DROPPER:
				Dropper dropper = ((Dropper) block.getState());
				dropper.setCustomName(text);
				dropper.update();
				break;
			case BARREL:
				Barrel barrel = ((Barrel) block.getState());
				barrel.setCustomName(text);
				barrel.update();
				break;
			case CHEST:
			case TRAPPED_CHEST:
				Chest chest = ((Chest) block.getState());
				chest.setCustomName(text);
				chest.update();
				break;
			default:
				break;
		}
	}

	public static void setRotation(Location location, BlockFace latitude) {
		Block block = location.getBlock();
		BlockData blockData = block.getBlockData();
		if (blockData instanceof Directional) {
			((Directional) blockData).setFacing(latitude);
			block.setBlockData(blockData);
		}
	}


	/**
	 * Get the inventory on the location.
	 *
	 * @param location you want to get the inventory.
	 * @return inventory or null if it not find inventory.
	 */
	public static Inventory getInventory(Location location) {
		Block block = location.getBlock();
		if (!checkBlocktype(block)) return null;

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
	public static boolean checkBlocktype(Block block) {
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
