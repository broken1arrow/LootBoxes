package org.brokenarrow.lootboxes.untlity;

import org.bukkit.Material;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MatrialList {
	private List<Material> matrials;
	private boolean firstRun = true;

	public MatrialList() {
		if (firstRun) {
			this.matrials = Stream.of(Material.values()).filter((material) -> material != Material.AIR && checkIfValidItem(material)
			).sorted(Comparator.comparing(Enum::name)).collect(Collectors.toList());
			firstRun = false;
			System.out.println("teskjhgbkböolinh if this get called");
		}
	}

	public boolean checkIfValidItem(Material material) {
		if (material == null || material == Material.AIR)
			return false;

		switch (material) {
			case PISTON_HEAD:
			case MOVING_PISTON:
			case LAVA:
			case WATER:
			case NETHER_PORTAL:
			case BUBBLE_COLUMN:
			case SOUL_FIRE:
			case FIRE:
			case CAVE_VINES:
			case BIG_DRIPLEAF_STEM:
			case LAVA_CAULDRON:
			case WATER_CAULDRON:
			case POWDER_SNOW_CAULDRON:
			case KELP_PLANT:
			case BAMBOO_SAPLING:
			case PUMPKIN_STEM:
			case MELON_STEM:
			case FROSTED_ICE:
			case SWEET_BERRY_BUSH:
			case REDSTONE_WIRE:
			case VOID_AIR:
			case CAVE_AIR:
			case CAVE_VINES_PLANT:
			case WEEPING_VINES_PLANT:
			case TWISTING_VINES_PLANT:
			case COCOA:
			case TALL_SEAGRASS:
			case POWDER_SNOW:
			case TRIPWIRE:
			case BEETROOTS:
			case CARROTS:
			case END_GATEWAY:
			case END_PORTAL:
			case POTATOES:
				return false;
			default:
				String stringItem = material.toString();
				if (stringItem.startsWith("LEGACY"))
					return false;

				if (!stringItem.startsWith("POTTED") && !stringItem.contains("CANDLE_CAKE")
						&& !stringItem.startsWith("ATTACHED_") && !stringItem.contains("WALL_")
						&& !stringItem.contains("COMMAND_"))
					return true;

		}
		return false;
	}

	public List<Material> getMatrials(String itemsToSearchFor) {
		if (itemsToSearchFor != null && !itemsToSearchFor.isEmpty())
			return matrials.stream().filter((matrial) -> matrial.toString().contains(itemsToSearchFor.toUpperCase())).sorted(Comparator.comparing(Enum::name)).collect(Collectors.toList());
		return matrials;
	}
}
