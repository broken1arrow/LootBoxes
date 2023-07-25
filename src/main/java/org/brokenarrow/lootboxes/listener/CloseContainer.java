package org.brokenarrow.lootboxes.listener;

import org.broken.arrow.nbt.library.RegisterNbtAPI;
import org.brokenarrow.lootboxes.Lootboxes;
import org.brokenarrow.lootboxes.builder.ContainerDataBuilder;
import org.brokenarrow.lootboxes.builder.LocationData;
import org.brokenarrow.lootboxes.lootdata.ContainerDataCache;
import org.brokenarrow.lootboxes.settings.Settings;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import static org.brokenarrow.lootboxes.untlity.BlockChecks.getInventory;

public class CloseContainer implements Listener {

	private final ContainerDataCache containerDataCache = ContainerDataCache.getInstance();
	private final Lootboxes lootboxes = Lootboxes.getInstance();
	private final Settings settings = Lootboxes.getInstance().getSettings();
	private final RegisterNbtAPI nbt = lootboxes.getNbtAPI();


	@EventHandler
	public void closeLootContainer(InventoryCloseEvent event) {
		Location location = event.getInventory().getLocation();
		if (location == null) return;
		Player player = (Player) event.getPlayer();
		if (player.hasPermission("lootboxes.bypass.open.requirement")) return;

		Inventory inventory = getInventory(location);
		if (inventory != null) {
			LocationData locationData = containerDataCache.getLocationData(location);
			if (locationData == null) return;
			ContainerDataBuilder containerData = containerDataCache.getCacheContainerData(locationData.getContinerData());
			if (containerData == null) return;

			if (settings.getSettingsData().isRemoveContainerWhenPlayerClose() && containerData.isSpawningContainerWithCooldown()) {
				location.getBlock().setType(Material.AIR);

			}
			if (!inventory.isEmpty()) {
				for (ItemStack itemStack : inventory) {
					if (itemStack == null) continue;
					location.getWorld().dropItemNaturally(location, itemStack);
				}
			}
			inventory.clear();

		}
	}
}


