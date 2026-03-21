package org.brokenarrow.lootboxes.untlity;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class LootContainer implements InventoryHolder {
    private final Inventory inventory;
    private final Location location;

    public LootContainer(final Location location, final int size, final String title) {
        this.inventory = Bukkit.createInventory(this, size, title);
        this.location = location;
    }

    /**
     * Get the location of the block or entity which corresponds to this inventory. May return null if this container
     * was custom created or is a virtual / subcontainer.
     *
     * @return location or null if not applicable.
     */
    @Nullable
    public Location getLocation() {
        return location;
    }

    @Override
    public @NotNull Inventory getInventory() {
        return inventory;
    }

    public void view(Player player) {
        player.openInventory(this.inventory);
    }
}
