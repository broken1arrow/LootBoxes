package org.brokenarrow.lootboxes.untlity.blockVisualization;

import org.brokenarrow.lootboxes.untlity.CreateItemUtily;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import static org.brokenarrow.lootboxes.untlity.RunTimedTask.runtask;

public class BlockVisualize {


	public static void visulizeBlock(final Block block, final Location location, final boolean shallBeVisualize) {
		visulizeBlock(null, block, location, shallBeVisualize);
	}

	public static void visulizeBlock(final Player player, final Block block, final Location location, final boolean shallBeVisualize) {

		if (!BlockVisualizerUtility.isVisualized(block) && shallBeVisualize)
			runtask(() -> BlockVisualizerUtility.visualize(player, block,
					getMaterial(), getText(block, location)), false);

		if (BlockVisualizerUtility.isVisualized(block) && shallBeVisualize) {
			BlockVisualizerUtility.stopVisualizing(block);
			runtask(() -> BlockVisualizerUtility.visualize(player, block,
					getMaterial(), getText(block, location)), false);

		} else if (BlockVisualizerUtility.isVisualized(block)) {
			BlockVisualizerUtility.stopVisualizing(block);
		}
		BlockVisualizerUtility.VisualTask.getInstance().start();
	}

	public static boolean stopVisualizing(final Block block) {
		if (BlockVisualizerUtility.isVisualized(block)) {
			BlockVisualizerUtility.stopVisualizing(block);
			return true;
		}
		return false;
	}

	public static Material getMaterial() {
		final ItemStack itemStack = CreateItemUtily.of(Material.BONE_BLOCK).makeItemStack();
		final Material material = itemStack.getType() != Material.AIR ? itemStack.getType() : null;
		return material != null ? material : Material.BONE_BLOCK;
	}

	public static String getText(final Block block, final Location location) {
		final String message = "";
		if (message != null && !message.equals(""))
			return message;
		return "";
	}
}
