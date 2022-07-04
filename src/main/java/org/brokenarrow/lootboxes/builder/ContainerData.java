package org.brokenarrow.lootboxes.builder;

import com.google.common.base.Enums;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedHashMap;
import java.util.Map;

import static org.brokenarrow.lootboxes.untlity.errors.Valid.checkNotNull;

public final class ContainerData implements ConfigurationSerializable {

	private final BlockFace facing;
	private final Material containerType;

	public ContainerData(final BlockFace facing, final Material containerType) {
		this.facing = facing;
		this.containerType = containerType;
	}

	public ContainerData(final String facing, final String containerType) {
		this.facing = addBlockFace(facing);
		this.containerType = addMatrial(containerType);
	}


	public BlockFace addBlockFace(final String facing) {
		checkNotNull(facing, "This block face are null.");
		final BlockFace blockFace = Enums.getIfPresent(BlockFace.class, facing).orNull();
		checkNotNull(blockFace, "This " + facing + " are not valid");

		return blockFace;
	}

	public Material addMatrial(final String containerType) {
		checkNotNull(containerType, "This containerType are null.");
		final Material material = Material.getMaterial(containerType);
		checkNotNull(material, "This material " + containerType + " are not valid");

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

	/**
	 * Creates a Map representation of this class.
	 * <p>
	 * This class must provide a method to restore this class, as defined in
	 * the {@link ConfigurationSerializable} interface javadocs.
	 *
	 * @return Map containing the current state of this class
	 */
	@NotNull
	@Override
	public Map<String, Object> serialize() {
		final Map<String, Object> keysData = new LinkedHashMap<>();
		keysData.put("facing", facing + "");
		keysData.put("containerType", containerType + "");
		return keysData;
	}

	public static ContainerData deserialize(final Map<String, Object> map) {
		final String facing = (String) map.get("facing");
		final String containerType = (String) map.get("containerType");

		return new ContainerData(facing,
				containerType);
	}
}
