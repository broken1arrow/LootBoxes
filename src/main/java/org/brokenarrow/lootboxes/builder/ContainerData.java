package org.brokenarrow.lootboxes.builder;

import com.google.common.base.Enums;
import de.tr7zw.changeme.nbtapi.NBT;
import org.brokenarrow.lootboxes.untlity.Facing;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedHashMap;
import java.util.Map;

import static org.brokenarrow.lootboxes.untlity.errors.Valid.checkNotNull;

public final class ContainerData implements ConfigurationSerializable {
    private Facing facing;
    private ItemStack containerType;
    private ItemStack[] containerContents;

    public ContainerData(final BlockFace facing, final Material containerType) {
        this.facing = Facing.getFace(facing);
        this.containerType = new ItemStack(containerType);
    }

    public ContainerData() {
        this.facing = Facing.RANDOM;
        this.containerType = new ItemStack(Material.CHEST);
    }

    public ContainerData(final String facing, final String containerType) {
        //this.facing = addBlockFace(facing);
        this.containerType = new ItemStack(addMaterial(containerType));
    }

    public void setBlockFace(final Facing facing) {
        this.facing = facing;
    }

    public void setContainer(final ItemStack containerType) {
        this.containerType = containerType;
    }

    public Facing getFacing() {
        return facing;
    }

    public ItemStack getContainer() {
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
        keysData.put("facing", facing.name());
        keysData.put("containerType", NBT.itemStackToNBT(containerType));
        return keysData;
    }

    public static ContainerData deserialize(final Map<String, Object> map) {
        final String facing = (String) map.get("facing");
        final String containerType = (String) map.get("containerType");

        return new ContainerData(facing,
                containerType);
    }

    private BlockFace addBlockFace(final String facing) {
        checkNotNull(facing, "This block face are null.");
        final BlockFace blockFace = Enums.getIfPresent(BlockFace.class, facing).orNull();
        checkNotNull(blockFace, "This " + facing + " are not valid");

        return blockFace;
    }

    private Material addMaterial(final String containerType) {
        checkNotNull(containerType, "This containerType are null.");
        final Material material = Material.getMaterial(containerType);
        checkNotNull(material, "This material " + containerType + " are not valid");

        return material;
    }
}
