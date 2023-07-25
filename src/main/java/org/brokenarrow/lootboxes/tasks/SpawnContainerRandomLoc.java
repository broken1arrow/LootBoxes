package org.brokenarrow.lootboxes.tasks;

import org.brokenarrow.lootboxes.Lootboxes;
import org.brokenarrow.lootboxes.builder.ContainerData;
import org.brokenarrow.lootboxes.builder.ContainerDataBuilder;
import org.brokenarrow.lootboxes.builder.SettingsData;
import org.brokenarrow.lootboxes.lootdata.ContainerDataCache;
import org.brokenarrow.lootboxes.settings.Settings;
import org.brokenarrow.lootboxes.untlity.Facing;
import org.brokenarrow.lootboxes.untlity.RandomUntility;
import org.brokenarrow.lootboxes.untlity.RunTimedTask;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

import static org.brokenarrow.lootboxes.settings.ChatMessages.RANDOM_LOOT_MESAGE_TITEL;
import static org.brokenarrow.lootboxes.untlity.ModifyBlock.*;
import static org.brokenarrow.lootboxes.untlity.SerializeUtlity.serilazeLoc;
import static org.brokenarrow.lootboxes.untlity.blockVisualization.BlockVisualize.visulizeBlock;

public class SpawnContainerRandomLoc {

	private final Settings settingsData = Lootboxes.getInstance().getSettings();
	private long time;
	private SettingsData settings;
	private String containerdataName;
	private final ContainerDataCache containerDataCacheInstance = ContainerDataCache.getInstance();
	private final Lootboxes lootboxes = Lootboxes.getInstance();
	private final RandomUntility randomUntility = lootboxes.getRandomUntility();

	public void task() {
		this.settings = settingsData.getSettingsData();

		if (settings.isRandomContinerSpawn()) {
			if (this.time == 0) {
				setRandomSpawnedContiner();
			} else if (System.currentTimeMillis() >= this.time) {
				ContainerDataBuilder containerDataBuilder = containerDataCacheInstance.getCacheContainerData(this.containerdataName);
				if (!containerDataBuilder.isRandomSpawn()) {
					setRandomSpawnedContiner();
				}
				for (Player player : Bukkit.getOnlinePlayers()) {
					Location location = player.getLocation();
					if (lootboxes.getLandProtectingLoader().checkIfAllProvidersAllowSpawnContainer(location))
						spawnBlock(containerDataBuilder, location, player);
				}
				this.time = System.currentTimeMillis() + (1000 * containerDataBuilder.getCooldown());
			}
		}

	}

	public void setRandomSpawnedContiner() {
		for (String containerdata : containerDataCacheInstance.getCacheContainerData().keySet()) {
			ContainerDataBuilder containerDataBuilder = containerDataCacheInstance.getCacheContainerData(containerdata);
			if (containerDataBuilder.isRandomSpawn()) {
				this.time = System.currentTimeMillis() + (1000 * containerDataBuilder.getCooldown());
				this.containerdataName = containerdata;
				return;
			}
		}
	}

	public void spawnBlock(ContainerDataBuilder containerDataBuilder, Location location, Player player) {
		/*Location  locationClone = randomUntility.nextLocation(location.clone(), 80,true);
		Block block = locationClone.getBlock();
		if (locationClone.getY() - 1 != location.getY() && locationClone.getY() - 2 != location.getY())
			if (block.getType() != Material.GLASS) block.setType(Material.LIGHT_BLUE_GLAZED_TERRACOTTA);*/
		Location loc = null;
		for (int i = 0; i < containerDataBuilder.getAttempts(); i++) {
			loc = checkLocation(location.clone(), player);
			if (loc != null)
				break;
		}
		if (loc != null) {
			spawnContainer(containerDataBuilder, loc);
			String message = RANDOM_LOOT_MESAGE_TITEL.languageMessagePrefix(serilazeLoc(loc));
			if (containerDataBuilder.isShowTitel() && message != null && !message.isEmpty()) {
				 String[] mes =  message.split("\\|");
				player.sendTitle(mes.length > 0 ? mes[0] : "", mes.length > 1 ? mes[1] : "", 1, 20 * 20, 1);
			}
			if (containerDataBuilder.isContanerShallglow()) {
				final Location finalLoc = loc;
				RunTimedTask.runtaskLater(20 * 5, () -> {
					visulizeBlock(finalLoc.getBlock(), finalLoc, true);
					RunTimedTask.runtaskLater(20 * 120, () -> visulizeBlock(finalLoc.getBlock(), finalLoc, false), false);
				}, false);
			}
		}

	}

	public void spawnContainer(ContainerDataBuilder containerData, Location location) {
		Map<Location, ContainerData> containerDataMap = containerData.getLinkedContainerData();
		String lootTableLinked = containerData.getLootTableLinked();
		if (containerData.isRandomSpawn()){
			ItemStack[] stacks = this.lootboxes.getMakeLootTable().makeLottable(lootTableLinked);
			if (stacks == null) {
				return;
			}
			location.getBlock().setType(containerData.getRandonLootContainerItem());
			if (containerData.getRandonLootContainerFaceing() == Facing.RANDOM) {
				Material material = containerData.getRandonLootContainerItem();
				setRotation(location, Facing.getRandomFace(material == Material.CHEST || material == Material.TRAPPED_CHEST));
			}else
				setRotation(location, containerData.getRandonLootContainerFaceing().getFace());
			setCustomName(location, containerData.getDisplayname());

			Inventory inventory = getInventory(location);
			if (inventory != null) {
				inventory.setContents(stacks);
			}
			return;
		}
		for (Map.Entry<Location, ContainerData> entry : containerDataMap.entrySet()) {
			ContainerData container = entry.getValue();
			if (location != null && lootTableLinked != null && !lootTableLinked.isEmpty()) {
				ItemStack[] stacks = this.lootboxes.getMakeLootTable().makeLottable(lootTableLinked);
				if (stacks == null) {
					return;
				}
				location.getBlock().setType(container.getContainerType());
				setRotation(location, container.getFacing());
				setCustomName(location, containerData.getDisplayname());

				Inventory inventory = getInventory(location);
				if (inventory != null) {
					inventory.setContents(stacks);
				}
			}
		}
	}

	private Location checkLocation(Location location, Player player) {

		Location locationClone = randomUntility.nextLocation(location.clone(), 10, 80, true, true);

		/*Block block = locationClone.getBlock();
		if (locationClone.getY() - 1 != location.getY() && locationClone.getY() - 2 != location.getY())
			if (block.getType() != Material.GLASS) block.setType(Material.WHITE_GLAZED_TERRACOTTA);*/



		Location locationSubtracted = locationClone;

		if (this.settings != null && this.settings.isSpawnOnSurface()) {
			World world = location.getWorld();
			int highestBlock = world != null ? world.getHighestBlockAt(location).getLocation().getBlockY() : 0;
			return new Location(location.getWorld(), locationSubtracted.getBlockX(), highestBlock + 1, locationSubtracted.getBlockZ());
		}
		if (checkIfLocationAreValid(locationSubtracted, locationSubtracted.getBlockY(), player))
			return locationSubtracted;

		return null;
	}

	private boolean checkIfLocationAreValid(Location location, int hight, Player player) {
		World world = location.getWorld();
		int highestBlock = world != null ? world.getHighestBlockAt(location).getLocation().getBlockY() : 0;

		if (this.settings != null && this.settings.getAmountOfBlocksBelowSurface() > 0)
			hight = hight + this.settings.getAmountOfBlocksBelowSurface();

		if (hight < highestBlock && !location.getBlock().isLiquid() && !checkBlock(location.getBlock()) && !isNearbyChest(location, this.settings != null ? settings.getBlocksBetweenContainers() : 10) && !isNearbyPlayer(player, location, this.settings != null ? settings.getBlocksAwayFromPlayer() : 30))
			return true;

		return false;
	}

	private boolean checkBlock(Block block) {
		Material material = block.getType();
		switch (block.getType()) {
			case HOPPER:
			case DISPENSER:
			case DROPPER:
			case BARREL:
			case CHEST:
			case TRAPPED_CHEST:
			case BEACON:
			case IRON_BLOCK:
			case GOLD_BLOCK:
			case DIAMOND_BLOCK:
			case BEDROCK:
				return true;
			case STONE:
			case GRANITE:
			case ANDESITE:
			case CAVE_AIR:
			case DIORITE:
				return false;
			default:
				return material != Material.getMaterial("DEEPSLATE");
		}
	}
	private boolean checkNearbyContainer(Block block) {
		Material material = block.getType();
		switch (block.getType()) {
			case HOPPER:
			case DISPENSER:
			case DROPPER:
			case BARREL:
			case CHEST:
			case TRAPPED_CHEST:
			case BEACON:
				return true;
			default:
				return false;
		}
	}
	public boolean isNearbyChest(Location location, int amountOfBlocksBetweenContainers) {
		boolean hasNearbyChest = false;
		double amountOfBlocksToCheck;
		for (int X = 1; X <= amountOfBlocksBetweenContainers; X++)
			for (int Y = 1; Y <= amountOfBlocksBetweenContainers; Y++)
				for (int Z = 1; Z <= amountOfBlocksBetweenContainers; Z++) {
					if (amountOfBlocksBetweenContainers % 2 == 0)
						amountOfBlocksToCheck = amountOfBlocksBetweenContainers / 2.0;
					else amountOfBlocksToCheck = (amountOfBlocksBetweenContainers / 2.0) + 1;

					Location cloneLoc = location.clone().add(amountOfBlocksToCheck, amountOfBlocksToCheck, amountOfBlocksToCheck);
					Location loc = cloneLoc.subtract(X, Y, Z);

					if (checkNearbyContainer(loc.getBlock())) {
						hasNearbyChest = true;
					}
				}

		return hasNearbyChest;
	}

	public boolean isNearbyPlayer(Player player, Location location, int amountAwayFromPlayer) {
		boolean hasNearbyPlayer = false;
		double amountOfBlocksToCheck;
		Location playerLocation = player.getLocation();
		for (int X = 1; X <= amountAwayFromPlayer; X++)
			for (int Y = 1; Y <= amountAwayFromPlayer; Y++)
				for (int Z = 1; Z <= amountAwayFromPlayer; Z++) {
					if (amountAwayFromPlayer % 2 == 0) amountOfBlocksToCheck = amountAwayFromPlayer / 2.0;
					else amountOfBlocksToCheck = (amountAwayFromPlayer / 2.0) + 1;

					Location cloneLoc = location.clone().add(amountOfBlocksToCheck, amountOfBlocksToCheck, amountOfBlocksToCheck);
					Location loc = cloneLoc.subtract(X, Y, Z);

					if (loc.getBlockX() == playerLocation.getBlockX() && loc.getBlockZ() == playerLocation.getBlockZ() && loc.getBlockY() == playerLocation.getBlockY()) {
						hasNearbyPlayer = true;
						return true;
					}
				}

		return hasNearbyPlayer;
	}
}
