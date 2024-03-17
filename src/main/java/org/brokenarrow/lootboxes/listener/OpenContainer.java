package org.brokenarrow.lootboxes.listener;

import org.broken.arrow.nbt.library.RegisterNbtAPI;
import org.brokenarrow.lootboxes.Lootboxes;
import org.brokenarrow.lootboxes.builder.ContainerData;
import org.brokenarrow.lootboxes.builder.ContainerDataBuilder;
import org.brokenarrow.lootboxes.builder.KeysData;
import org.brokenarrow.lootboxes.builder.LocationData;
import org.brokenarrow.lootboxes.lootdata.ContainerDataCache;
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
import java.util.Map;

import static org.brokenarrow.lootboxes.settings.ChatMessages.*;
import static org.brokenarrow.lootboxes.untlity.BlockChecks.checkBlockIsContainer;
import static org.brokenarrow.lootboxes.untlity.ConvertToTime.toTimeFromMillis;
import static org.brokenarrow.lootboxes.untlity.KeyMeta.*;
import static org.brokenarrow.lootboxes.untlity.ModifyBlock.*;
import static org.brokenarrow.lootboxes.untlity.PlaySound.playSound;
import static org.brokenarrow.lootboxes.untlity.TranslatePlaceHolders.translatePlaceholders;

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
			LocationData locationData = this.containerDataCache.getLocationData(location);
			if (locationData == null) return;

			if (key == null || containerDataName == null) {
				List<String> list = new ArrayList<>();
				for (KeysData keysData : locationData.getKeys().values()) {
					if (keysData.getItemType() == null || keysData.getItemType() == Material.AIR)
						continue;
					String lootTable = keysData.getLootTableLinked();
					if (lootTable == null || lootTable.isEmpty()) {
						ContainerDataBuilder containerDataCache = this.containerDataCache.getCacheContainerData(locationData.getContainerData());
						if (containerDataCache != null) {
							lootTable = containerDataCache.getLootTableLinked();
						}
					}
					if (lootTable == null)
						lootTable = "";
					list.add(translatePlaceholders(keysData.getDisplayName(), keysData.getKeyName(), lootTable, keysData.getAmountNeeded(), keysData.getItemType()));
				}
				if (!list.isEmpty()) {
					LOOKED_CONTAINER_TRY_OPEN.sendMessage(player, itemStack != null ? itemStack.getType() : "AIR", list);

					event.setCancelled(true);
					playSound(player, LOOKED_CONTAINER_SOUND.languageMessages());
					return;
				}
			}

			ContainerDataBuilder cacheContainerData = containerDataCache.getCacheContainerData(containerDataName);
			if (cacheContainerData == null) {
				cacheContainerData = containerDataCache.getCacheContainerData(locationData.getContainerData());
			}

			KeysData dataCacheCacheKey = cacheContainerData.getKeysData().get(key);
			if (cacheContainerData.getLootTableLinked() == null || cacheContainerData.getLootTableLinked().isEmpty()) {
				LOOKED_CONTAINER_NO_LOOTTABLE_LINKED.sendMessage(player, containerDataName);
				event.setCancelled(true);
				return;
			}

			if (!checkIfPlayerHasItem(dataCacheCacheKey, key, player, itemStack)) {
				event.setCancelled(true);
			}

			if (cacheContainerData.isSpawningContainerWithCooldown() && !lootboxes.getSpawnedContainers().isRefill(location)) {
				String time = "0";
				Long cachedTime = lootboxes.getSpawnedContainers().getCachedTimeMap().get(containerDataName);
				if (cachedTime != null)
					time = toTimeFromMillis(cachedTime - System.currentTimeMillis());
				if (time.equals("0")) {
					Map<Location, ContainerData> containerData = cacheContainerData.getLinkedContainerData();
					if (containerData == null || containerData.get(location) == null)
						return;
				}
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

			if (!cacheContainerData.isSpawningContainerWithCooldown() && !spawnLootWhenClicking(cacheContainerData, location, block)) {
				LOOKED_CONTAINER_NO_LOOTTABLE_LINKED.sendMessage(player, containerDataName);
			} else {
				OPEN_CONTAINER.sendMessage(player);
			}
			System.out.println("setRefill ");
			lootboxes.getSpawnedContainers().setRefill(location, false);
			System.out.println("setRefill " + lootboxes.getSpawnedContainers().getHasRefill());
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
			if (material != null && material != Material.AIR/*&& dataCacheCacheKey.getItemType() != Material.AIR*/) {
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
			ItemStack[] stacks = this.lootboxes.getMakeLootTable().makeLootTable(lootTableLinked);
			if (stacks == null) {
				return false;
			}
			ContainerData containerDataLinked = containerData.getLinkedContainerData().get(location);
			block.setType(containerDataLinked.getContainerType());
			setRotation(location, containerDataLinked.getFacing());
			setCustomName(location, containerData.getDisplayname());
			Inventory inventory = getInventory(location);
			if (inventory != null) {
				inventory.setContents(stacks);
			}
			return true;
		}
		return false;
	}

}
