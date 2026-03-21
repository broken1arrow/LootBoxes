package org.brokenarrow.lootboxes.builder;

import org.brokenarrow.lootboxes.untlity.errors.Valid;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public final class BlockKey implements ConfigurationSerializable {
    private final UUID worldId;
    private final int x;
    private final int y;
    private final int z;
    private volatile Location cachedLocation;

    private BlockKey(final UUID worldId, final int x, final int y, final int z) {
        this.worldId = worldId;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public static BlockKey of(@Nonnull final Location loc) {
        Valid.checkNotNull(loc.getWorld(), "World cannot be null");
        return new BlockKey(
                loc.getWorld().getUID(),
                loc.getBlockX(),
                loc.getBlockY(),
                loc.getBlockZ()
        );
    }

    public static BlockKey of(@Nonnull final UUID worldId, final int x, final int y, final int z) {
        return new BlockKey(worldId, x, y, z);
    }

    @Nullable
    public Location getLocation() {
        Location loc = cachedLocation;
        if (loc != null) return loc;

        World world = Bukkit.getWorld(worldId);
        if (world == null) return null;

        loc = new Location(world, x, y, z);
        cachedLocation = loc;
        return loc;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getZ() {
        return z;
    }

    public UUID getWorldId() {
        return worldId;
    }

    public boolean isSet() {
        return worldId != null;
    }

    public @NotNull Map<String, Object> serialize(){
        Map<String, Object> primaryData = new HashMap<>();
        primaryData.put("world_id", getWorldId()  != null ? getWorldId()  + "" : null);
        primaryData.put("loc_x", x);
        primaryData.put("loc_y", y);
        primaryData.put("loc_z", z);
        return primaryData;
    }

    @Override
    public boolean equals(final Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        final BlockKey blockKey = (BlockKey) o;
        return x == blockKey.x && y == blockKey.y && z == blockKey.z && Objects.equals(worldId, blockKey.worldId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(worldId, x, y, z);
    }

    @Override
    public String toString() {
        return "worldId=" + worldId +
                " x=" + x +
                " y=" + y +
                " z=" + z;
    }
}
