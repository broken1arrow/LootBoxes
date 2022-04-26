package org.brokenarrow.lootboxes.listener;

import org.brokenarrow.lootboxes.builder.ContainerDataBuilder;
import org.brokenarrow.lootboxes.lootdata.ContainerData;
import org.brokenarrow.lootboxes.untlity.CreateItemUtily;
import org.brokenarrow.lootboxes.untlity.RandomKey;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

public class MobDropListener implements Listener {
	ContainerData containerData = ContainerData.getInstance();

	@EventHandler
	public void playerLogIn(PlayerJoinEvent event) {
		Map<String, ContainerDataBuilder.KeysData> keysData = containerData.getCacheKeysData("Global_Container");

		for (Map.Entry<String, ContainerDataBuilder.KeysData> data : keysData.entrySet()) {
			event.getPlayer().getInventory().addItem(CreateItemUtily.of(data.getValue().getItemType(), data.getValue().getDisplayName(), data.getValue().getLore()).setItemMetaData("test", data.getKey()).makeItemStack());
		}
	}

	@EventHandler
	public void MobDrops(EntityDeathEvent event) {
		final LivingEntity entity = event.getEntity();
		if (entity.getKiller() != null && !(entity instanceof Player)) {
			ItemStack[] itemStacks = new RandomKey().makeRandomAmountOfItems(entity.getType());
			if (itemStacks != null)
				for (ItemStack itemStack : itemStacks)
					entity.getLocation().getWorld().dropItemNaturally(entity.getLocation(), itemStack);

		}
	}


}
