package org.brokenarrow.lootboxes.listener;

import de.tr7zw.changeme.nbtapi.metodes.RegisterNbtAPI;
import org.brokenarrow.lootboxes.Lootboxes;
import org.brokenarrow.lootboxes.builder.KeysData;
import org.brokenarrow.lootboxes.builder.LocationData;
import org.brokenarrow.lootboxes.lootdata.ContainerDataCache;
import org.brokenarrow.lootboxes.untlity.TranslatePlaceHolders;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

import static org.brokenarrow.lootboxes.settings.ChatMessages.*;
import static org.brokenarrow.lootboxes.untlity.BlockChecks.checkBlockIsContainer;
import static org.brokenarrow.lootboxes.untlity.KeyMeta.*;
import static org.brokenarrow.lootboxes.untlity.PlaySound.playSound;

public class OpenContainer implements Listener {
	private final ContainerDataCache containerDataCache = ContainerDataCache.getInstance();
	private final Lootboxes lootboxes = Lootboxes.getInstance();
	private final RegisterNbtAPI nbt = lootboxes.getNbtAPI();

	@EventHandler
	public void openLootContainer(PlayerInteractEvent event) {
		Block block = event.getClickedBlock();
		if (checkBlockIsContainer(block)) {
			Location location = event.getClickedBlock().getLocation();
			ItemStack itemStack = event.getItem();
			Player player = event.getPlayer();
			if (player.hasMetadata(ADD_AND_REMOVE_CONTAINERS.name())) return;
			String key = null;
			String containerData = null;
			if (itemStack != null && itemStack.getType() != Material.AIR) {
				key = nbt.getCompMetadata().getMetadata(itemStack, MOB_DROP_KEY_NAME.name());
				containerData = nbt.getCompMetadata().getMetadata(itemStack, MOB_DROP_CONTAINER_DATA_NAME.name());
			}
			LocationData locationData = containerDataCache.getLocationData(location);
			if (locationData != null) {
				if (key == null || containerData == null) {
					List<String> list = new ArrayList<>();
					for (KeysData values : locationData.getKeys().values())
						list.add(TranslatePlaceHolders.translatePlaceholders(values.getDisplayName(), values.getKeyName(), values.getLootTableLinked(), values.getAmountNeeded(), values.getItemType()));

					LOOKED_CONTAINER_TRY_OPEN.sendMessage(player, itemStack != null ? itemStack.getType() : "AIR", list);

					event.setCancelled(true);
					playSound(player, LOOKED_CONTAINER_SOUND.languageMessages());
					return;
				}
			} else return;

			KeysData dataCacheCacheKey = containerDataCache.getCacheKey(containerData, key);
			if (dataCacheCacheKey.getAmountNeeded() <= 0) return;

			if (itemStack.getType() != dataCacheCacheKey.getItemType()) {
				event.setCancelled(true);
				LOOKED_CONTAINER_NOT_RIGHT_ITEM.sendMessage(player, itemStack.getType(), dataCacheCacheKey.getItemType());
			}
			if (itemStack.getAmount() < dataCacheCacheKey.getAmountNeeded()) {
				event.setCancelled(true);
				LOOKED_CONTAINER_NOT_RIGHT_AMOUNT.sendMessage(player, itemStack.getAmount(), dataCacheCacheKey.getAmountNeeded());
			}
			if (event.useInteractedBlock() == Event.Result.DENY)
				playSound(player, LOOKED_CONTAINER_SOUND.languageMessages());
			else
				playSound(player, UNLOOKED_CONTAINER_SOUND.languageMessages());

		}
	}
}
