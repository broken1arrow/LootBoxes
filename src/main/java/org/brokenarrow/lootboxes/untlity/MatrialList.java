package org.brokenarrow.lootboxes.untlity;

import org.brokenarrow.lootboxes.Lootboxes;
import org.bukkit.Material;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MatrialList {
	private List<Material> matrials;
	private final ServerVersion version;
	private boolean firstRun;

	public MatrialList() {
		this.version = Lootboxes.getInstance().getServerVersion();
		if (this.matrials == null) {
			this.matrials = Stream.of(Material.values()).filter((material) -> material != Material.AIR && checkIfValidItem(material)
			).sorted(Comparator.comparing(Enum::name)).collect(Collectors.toList());
			firstRun = true;
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
			case SPRUCE_DOOR:
			case JUNGLE_DOOR:
			case ACACIA_DOOR:
			case DARK_OAK_DOOR:
			case BIRCH_DOOR:
			case BREWING_STAND:
			case CAULDRON:
			case CARROT:
			case FLOWER_POT:
			case POTATO:
				if (version.olderThan(ServerVersion.Version.v1_13))
					return false;
			default:
				String stringItem = material.toString();
				if (stringItem.startsWith("LEGACY"))
					return false;
				if (version.olderThan(ServerVersion.Version.v1_13)) {
					if (stringItem.startsWith("STATIONARY")
							|| stringItem.startsWith("BURNING")
							|| stringItem.endsWith("BLOCK_OFF")
							|| stringItem.endsWith("BLOCK_ON")
							|| checkLegazy(stringItem))
						return false;
				}

				if (!stringItem.startsWith("POTTED") && !stringItem.contains("CANDLE_CAKE")
						&& !stringItem.startsWith("ATTACHED_") && !stringItem.contains("WALL_")
						&& !stringItem.contains("COMMAND"))
					return true;

				return false;

		}

	}

	public boolean checkLegazy(String stringItem) {
		switch (stringItem) {
			case "CROPS":
			case "DAYLIGHT_DETECTOR_INVERTED":
			case "DOUBLE_STEP":
			case "DOUBLE_STONE_SLAB2":
			case "ENDER_PORTAL":
			case "BED_BLOCK":
			case "CAKE_BLOCK":
			case "GLOWING_REDSTONE_ORE":
			case "IRON_DOOR_BLOCK":
			case "BEETROOT_BLOCK":
			case "PISTON_EXTENSION":
			case "PISTON_MOVING_PIECE":
			case "PORTAL":
			case "PURPUR_DOUBLE_SLAB":
			case "REDSTONE_COMPARATOR_OFF":
			case "REDSTONE_COMPARATOR_ON":
			case "REDSTONE_LAMP_ON":
			case "REDSTONE_TORCH_OFF":
			case "SIGN_POST":
			case "SKULL":
			case "STANDING_BANNER":
			case "SUGAR_CANE_BLOCK":
			case "WOODEN_DOOR":
			case "WOOD_DOUBLE_STEP":
			case "NETHER_WARTS":
				return true;
			default:
				return false;
		}
	}

	public List<Material> getMatrials(String itemsToSearchFor) {
		if (itemsToSearchFor != null && !itemsToSearchFor.isEmpty())
			return matrials.stream().filter((matrial) -> matrial.toString().contains(itemsToSearchFor.toUpperCase())).sorted(Comparator.comparing(Enum::name)).collect(Collectors.toList());
		return matrials;
	}
}
