package org.brokenarrow.lootboxes.listener;

import org.brokenarrow.lootboxes.lootdata.ContainerDataCache;
import org.brokenarrow.lootboxes.untlity.RandomKey;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

public class MobDropListener implements Listener {
	ContainerDataCache containerDataCache = ContainerDataCache.getInstance();

	@EventHandler
	public void MobDrops(EntityDeathEvent event) {
		final LivingEntity entity = event.getEntity();
		if (entity.getKiller() != null && !(entity instanceof Player)) {
			ItemStack[] itemStacks = new RandomKey().makeRandomAmountOfItems(entity.getType());
			if (itemStacks != null)
				for (ItemStack itemStack : itemStacks) {
					if (itemStack == null) continue;

					entity.getLocation().getWorld().dropItemNaturally(entity.getLocation(), itemStack);
				}
		}
	}


}
