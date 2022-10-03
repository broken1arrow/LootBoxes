package org.brokenarrow.lootboxes.untlity;

import org.brokenarrow.lootboxes.Lootboxes;
import org.bukkit.block.BlockFace;

public enum Facing {

	NORTH(),
	EAST(),
	SOUTH(),
	WEST(),
	RANDOM(),
	UP(),
	DOWN();


	public static Facing getFace(int pos){
		Facing[] blockFaces = Facing.values();
		if (pos >= blockFaces.length) return null;
		return blockFaces[pos];
	}
	public static BlockFace getFace(String face){
		BlockFace[] blockFaces = BlockFace.values();
		for (BlockFace blockFace :blockFaces)
			if (blockFace.name().equalsIgnoreCase(face))
				return blockFace;
		return null;
	}

	public BlockFace getFace(){
		BlockFace[] blockFaces = BlockFace.values();
		for (BlockFace blockFace :blockFaces)
			if (blockFace.name().equalsIgnoreCase(this.name()))
				return blockFace;
		return BlockFace.WEST;
	}

	public static BlockFace getRandomFace(boolean isChest){
		RandomUntility random = Lootboxes.getInstance().getRandomUntility();
		int number = random.nextRandomInt( Facing.values().length);
		return getFace(getFacingFromIndex( number, isChest).name());
	}

	public static Facing getFacingFromIndex(int number, boolean isChest) {
		switch (number) {
			case 0:
				return NORTH;
			case 1:
				return EAST;
			case 2:
				return SOUTH;
			case 3:
				return WEST;
			default:
				if (!isChest) {
					if (number == 4)
						return UP;
					if (number == 5)
						return DOWN;
				}
				return WEST;
		}
	}
}
