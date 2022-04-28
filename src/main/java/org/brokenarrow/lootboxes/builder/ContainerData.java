package org.brokenarrow.lootboxes.builder;

import com.google.common.base.Enums;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;

import static org.brokenarrow.lootboxes.untlity.errors.Valid.checkNotNull;

public final class ContainerData {

	private final BlockFace facing;
	private final Material containerType;

	public ContainerData(BlockFace facing, Material containerType) {
		this.facing = facing;
		this.containerType = containerType;
	}

	public ContainerData(String facing, String containerType) {
		this.facing = addBlockFace(facing);
		this.containerType = addMatrial(containerType);
	}


	public BlockFace addBlockFace(final String facing) {
		checkNotNull(facing, "This block face are null.");
		BlockFace blockFace = Enums.getIfPresent(BlockFace.class, facing).orNull();
		checkNotNull(blockFace, "This " + facing + " are not valid");

		return blockFace;
	}

	public Material addMatrial(final String containerType) {
		checkNotNull(containerType, "This containerType are null.");
		Material material = Enums.getIfPresent(Material.class, containerType).orNull();
		checkNotNull(material, "This " + containerType + " are not valid");

		return material;
	}

	public BlockFace getFacing() {
		return facing;
	}

	public Material getContainerType() {
		return containerType;
	}

	@Override
	public String toString() {
		return "ContainerDataCache{" +
				"facing=" + facing +
				", containerType=" + containerType +
				'}';
	}
}
