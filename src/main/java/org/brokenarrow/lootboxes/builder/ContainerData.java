package org.brokenarrow.lootboxes.builder;

import com.google.common.base.Enums;
import de.tr7zw.changeme.nbtapi.NBT;
import org.broken.arrow.library.nbt.RegisterNbtAPI;
import org.brokenarrow.lootboxes.untlity.Facing;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.brokenarrow.lootboxes.untlity.errors.Valid.checkNotNull;

public final class ContainerData implements ConfigurationSerializable {
    private Facing facing;
    private ItemStack containerType;
    private ItemStack[] containerContents;

    public ContainerData() {
        this(Facing.RANDOM, new ItemStack(Material.CHEST));
    }

    public ContainerData(final Facing facing, final ItemStack containerType) {
        this.facing = facing;
        this.containerType = containerType;
    }

    public ContainerData(final BlockFace facing, final ItemStack containerType) {
        this(Facing.getFace(facing), new ItemStack(containerType));
    }

    public void setBlockFace(final Facing facing) {
        this.facing = facing;
    }

    public void setContainer(final ItemStack containerType) {
        this.containerType = containerType;
    }

    public void setContents(@Nullable final ItemStack[] contents) {
        this.containerContents = contents;
    }

    public Facing getFacing() {
        return facing;
    }

    public ItemStack getContainer() {
        return containerType;
    }

    public Material getContainerType() {
        if(containerType == null)
            return Material.AIR;
        return containerType.getType();
    }

    @Nullable
    public ItemStack[] getContainerContents() {
        return containerContents;
    }


    @Override
    public String toString() {
        return "ContainerData{" +
                "facing=" + facing +
                ", containerType=" + containerType +
                ", containerContents=" + Arrays.toString(containerContents) +
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
        if (containerType != null) {
            try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
                NBT.itemStackToNBT(containerType).writeCompound(outputStream);
                keysData.put("containerType", outputStream.toByteArray());
            } catch (IOException e) {
            }
        }
        if (containerContents != null)
            keysData.put("container_contents", RegisterNbtAPI.serializeItemStack(containerContents));
        return keysData;
    }

    public static ContainerData deserialize(final Map<String, Object> map) {
        final String facing = (String) map.get("facing");
        final Object lootContainerType = map.get("containerType");
        final Object containerContents = map.get("container_contents");

        ItemStack containerType = null;
        if (lootContainerType instanceof byte[]) {
            byte[] primitiveArray = (byte[]) lootContainerType;
            try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(primitiveArray)) {
                containerType = NBT.itemStackFromNBT(NBT.readNBT(byteArrayInputStream));
            } catch (IOException e) {
            }
        } else {
            Material material = Material.getMaterial(lootContainerType + "");
            if (material != null)
                containerType = new ItemStack(material);
        }
        
        ItemStack[] contents = null;
        if (containerContents instanceof byte[]) {
            byte[] primitiveArray = (byte[]) containerContents;
            contents = RegisterNbtAPI.deserializeItemStack(primitiveArray);
        }

        ContainerData containerData = new ContainerData(Facing.getFace(facing),
                containerType);
        containerData.setContents(contents);
        return containerData;
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
