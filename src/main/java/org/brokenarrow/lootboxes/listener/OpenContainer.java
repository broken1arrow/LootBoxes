package org.brokenarrow.lootboxes.listener;

import de.tr7zw.changeme.nbtapi.metodes.RegisterNbtAPI;
import org.brokenarrow.lootboxes.Lootboxes;
import org.brokenarrow.lootboxes.builder.ContainerData;
import org.brokenarrow.lootboxes.builder.ContainerDataBuilder;
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
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

import static org.brokenarrow.lootboxes.settings.ChatMessages.*;
import static org.brokenarrow.lootboxes.untlity.BlockChecks.checkBlockIsContainer;
import static org.brokenarrow.lootboxes.untlity.ConvertToTime.toTimeFromMillis;
import static org.brokenarrow.lootboxes.untlity.KeyMeta.*;
import static org.brokenarrow.lootboxes.untlity.ModifyBlock.*;
import static org.brokenarrow.lootboxes.untlity.PlaySound.playSound;

public class OpenContainer implements Listener {
	private final ContainerDataCache containerDataCache = ContainerDataCache.getInstance();
	private final Lootboxes lootboxes = Lootboxes.getInstance();
	private final RegisterNbtAPI nbt = lootboxes.getNbtAPI();


	@EventHandler
	public void openLootContainer(PlayerInteractEvent event) {
		Block block = event.getClickedBlock();
		if (checkBlockIsContainer(block)) {
			Location location = block.getLocation();
			ItemStack itemStack = event.getItem();
			Player player = event.getPlayer();
			if (player.hasMetadata(ADD_AND_REMOVE_CONTAINERS.name())) return;
			if (player.hasPermission("lootboxes.bypass.open.requirement")) return;

			String key = null;
			String containerDataName = null;
			if (itemStack != null && itemStack.getType() != Material.AIR) {
				key = nbt.getCompMetadata().getMetadata(itemStack, MOB_DROP_KEY_NAME.name());
				containerDataName = nbt.getCompMetadata().getMetadata(itemStack, MOB_DROP_CONTAINER_DATA_NAME.name());
			}
			LocationData locationData = containerDataCache.getLocationData(location);
			if (locationData == null) return;

			if (key == null || containerDataName == null) {
				List<String> list = new ArrayList<>();
				for (KeysData values : locationData.getKeys().values()) {
					if (values.getItemType() == null || values.getItemType().isAir())
						break;
					list.add(TranslatePlaceHolders.translatePlaceholders(values.getDisplayName(), values.getKeyName(), values.getLootTableLinked(), values.getAmountNeeded(), values.getItemType()));
				}
				if (!list.isEmpty()) {
					LOOKED_CONTAINER_TRY_OPEN.sendMessage(player, itemStack != null ? itemStack.getType() : "AIR", list);

					event.setCancelled(true);
					playSound(player, LOOKED_CONTAINER_SOUND.languageMessages());
					return;
				}
			}

			ContainerDataBuilder containerData = containerDataCache.getCacheContainerData(containerDataName);
			if (containerData == null) {
				containerData = containerDataCache.getCacheContainerData(locationData.getContinerData());
			}

			KeysData dataCacheCacheKey = containerData.getKeysData().get(key);
			if (!checkIfPlayerHasItem(dataCacheCacheKey, key, player, itemStack)) {
				event.setCancelled(true);
			/*if (key != null && containerData.getKeysData() != null) {
				if (!key.startsWith("Keys_"))
					key = "Keys_" + key;
				KeysData dataCacheCacheKey = containerData.getKeysData().get(key);
				if (dataCacheCacheKey == null) {

					lootboxes.getLogger().log(Level.WARNING, "Of some reson is key data null, this shold not hapend");
					event.setCancelled(true);
					return;
				}
				Material material = dataCacheCacheKey.getItemType();
				if (material != null && !material.isAir()) {
					if (dataCacheCacheKey.getAmountNeeded() <= 0) return;
					if (itemStack.getType() != material) {
						event.setCancelled(true);
						LOOKED_CONTAINER_NOT_RIGHT_ITEM.sendMessage(player, itemStack.getType(), material);
					}
					if (itemStack.getAmount() < dataCacheCacheKey.getAmountNeeded()) {
						event.setCancelled(true);
						LOOKED_CONTAINER_NOT_RIGHT_AMOUNT.sendMessage(player, itemStack.getAmount(), dataCacheCacheKey.getAmountNeeded());
					}
				}*/

			}

			if (containerData.isSpawningContainerWithCooldown() && !lootboxes.getSpawnedContainers().isRefill(location)) {
				String time = "0";
				Long cachedTime = lootboxes.getSpawnedContainers().getCachedTimeMap().get(containerDataName);
				if (cachedTime != null)
					time = toTimeFromMillis(cachedTime - System.currentTimeMillis());
				HAS_NOT_REFILL_CONTAINER.sendMessage(player, time);
				event.setCancelled(true);
				return;
			}

			if (event.useInteractedBlock() == Event.Result.DENY) {
				playSound(player, LOOKED_CONTAINER_SOUND.languageMessages());
				return;
			} else {
				playSound(player, UNLOOKED_CONTAINER_SOUND.languageMessages());
			}

			if (!containerData.isSpawningContainerWithCooldown() && !spawnLootWhenClicking(containerData, location, block)) {
				LOOKED_CONTAINER_NO_LOOTTABLE_LINKED.sendMessage(player, containerDataName);
			} else {
				OPEN_CONTAINER.sendMessage(player);
			}
			lootboxes.getSpawnedContainers().setRefill(location, false);
			if (key != null) {
				int amount = itemStack.getAmount() - dataCacheCacheKey.getAmountNeeded();
				player.getInventory().remove(itemStack);
				itemStack.setAmount(amount);
				player.getInventory().addItem(itemStack);
			}
		}
	}

	private boolean checkIfPlayerHasItem(KeysData dataCacheCacheKey, String key, Player player, ItemStack itemStack) {
		boolean checkVaidItems = true;
		if (dataCacheCacheKey == null) {
			//lootboxes.getLogger().log(Level.WARNING, "Of some reson is key data null, this shold not hapend");
			return true;
		}
		if (key != null) {
			Material material = dataCacheCacheKey.getItemType();
			if (material != null && !material.isAir() /*&& dataCacheCacheKey.getItemType() != Material.AIR*/) {
				if (dataCacheCacheKey.getAmountNeeded() <= 0) return true;
				if (itemStack.getType() != material) {
					LOOKED_CONTAINER_NOT_RIGHT_ITEM.sendMessage(player, itemStack.getType(), material);
					checkVaidItems = false;
				}
				if (itemStack.getAmount() < dataCacheCacheKey.getAmountNeeded()) {
					LOOKED_CONTAINER_NOT_RIGHT_AMOUNT.sendMessage(player, itemStack.getAmount(), dataCacheCacheKey.getAmountNeeded());
					checkVaidItems = false;
				}
			}
		}
		return checkVaidItems;
	}

	private boolean spawnLootWhenClicking(ContainerDataBuilder containerData, Location location, Block block) {
		String lootTableLinked = containerData.getLootTableLinked();
		if (lootTableLinked != null && !lootTableLinked.isEmpty()) {
			ItemStack[] item = this.lootboxes.getMakeLootTable().makeLottable(lootTableLinked);
			ContainerData containerData1 = containerData.getLinkedContainerData().get(location);
			block.setType(containerData1.getContainerType());
			setRotation(location, containerData1.getFacing());
			setCustomName(location, containerData.getDisplayname());
			Inventory inventory = getInventory(location);
			if (inventory != null) {
				inventory.setContents(item);
			}
			return true;
		}
		return false;
	}

}
