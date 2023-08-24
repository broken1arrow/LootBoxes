package org.brokenarrow.lootboxes.untlity;

import org.broken.arrow.color.library.TextTranslator;
import org.brokenarrow.lootboxes.Lootboxes;
import org.brokenarrow.lootboxes.untlity.ServerVersion.Version;
import org.bukkit.Location;
import org.bukkit.block.Barrel;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.block.Dispenser;
import org.bukkit.block.Dropper;
import org.bukkit.block.Hopper;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Directional;
import org.bukkit.inventory.Inventory;
import org.bukkit.material.DirectionalContainer;
import org.bukkit.material.MaterialData;

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

		if (Lootboxes.getInstance().getServerVersion().olderThan(Version.v1_13)){
			BlockState blockState = block.getState();
			if (blockState.getData() instanceof DirectionalContainer){
				MaterialData materialData = blockState.getData();
				materialData.setData(setFacingDirection(latitude));
				blockState.setData(materialData);
				blockState.update(true);
			}
			return;
		}
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

	public static byte setFacingDirection(BlockFace face) {
		byte data;

		switch (face) {
			case NORTH:
				data = 0x2;
				break;

			case SOUTH:
				data = 0x3;
				break;

			case WEST:
				data = 0x4;
				break;

			case EAST:
			default:
				data = 0x5;
		}
		return data;
	}


	public static BlockFace getFacing(byte data) {
		switch (data) {
			case 0x2:
				return BlockFace.NORTH;

			case 0x3:
				return BlockFace.SOUTH;

			case 0x4:
				return BlockFace.WEST;

			case 0x5:
			default:
				return BlockFace.EAST;
		}
	}

}
