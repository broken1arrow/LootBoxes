package org.brokenarrow.lootboxes.builder;

import org.brokenarrow.lootboxes.Lootboxes;
import org.brokenarrow.lootboxes.untlity.ServerVersion;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class CustomContainer {
    private final ItemStack container;
    private final boolean vanillaInventory;

    public CustomContainer(@NotNull final ItemStack container) {
        this.container = container;
        this.vanillaInventory = checkItemIsContainer(container);
    }

    public ItemStack getContainer() {
        return container;
    }

    public boolean isVanillaInventory() {
        return vanillaInventory;
    }

    public boolean checkItemIsContainer(final ItemStack itemStack) {
        if (itemStack == null) return false;

        Material type = itemStack.getType();
        switch (type) {
            case HOPPER:
            case DISPENSER:
            case DROPPER:
            case CHEST:
            case TRAPPED_CHEST:
            case SHULKER_BOX:
                return true;
            default:
                if (Lootboxes.getInstance().getServerVersion().atLeast(ServerVersion.Version.v1_14))
                    return type == Material.BARREL;
                return false;
        }

    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        CustomContainer that = (CustomContainer) o;
        return vanillaInventory == that.vanillaInventory && container.isSimilar(container);
    }

    @Override
    public int hashCode() {
        int hash = 1;
        hash = hash * 31 + container.getType().hashCode();
        hash = hash * 31 + (container.hasItemMeta() ? container.getItemMeta().hashCode() : 0);
        hash = hash * 31 + (vanillaInventory ? 0 : 1);
        return hash;
    }

    @Override
    public String toString() {
        return "CustomChest{" +
                "container=" + container +
                ", vanillaInventory=" + vanillaInventory +
                '}';
    }
}