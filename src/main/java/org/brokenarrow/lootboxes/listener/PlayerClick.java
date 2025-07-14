package org.brokenarrow.lootboxes.listener;

import org.broken.arrow.library.nbt.RegisterNbtAPI;
import org.brokenarrow.lootboxes.Lootboxes;
import org.brokenarrow.lootboxes.builder.ContainerData;
import org.brokenarrow.lootboxes.builder.ContainerDataBuilder;
import org.brokenarrow.lootboxes.builder.LocationData;
import org.brokenarrow.lootboxes.builder.SettingsData;
import org.brokenarrow.lootboxes.lootdata.ContainerDataCache;
import org.brokenarrow.lootboxes.menus.containerdata.AlterContainerDataMenu;
import org.brokenarrow.lootboxes.untlity.CreateItemUtily;
import org.brokenarrow.lootboxes.untlity.RunTimedTask;
import org.brokenarrow.lootboxes.untlity.ServerVersion.Version;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.Directional;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.material.DirectionalContainer;
import org.bukkit.material.MaterialData;

import java.util.Map;

import static org.brokenarrow.lootboxes.settings.ChatMessages.*;
import static org.brokenarrow.lootboxes.untlity.BlockChecks.checkBlockIsContainer;
import static org.brokenarrow.lootboxes.untlity.KeyMeta.ADD_AND_REMOVE_CONTAINERS;
import static org.brokenarrow.lootboxes.untlity.KeyMeta.ADD_AND_REMOVE_CONTAINERS_ALLOW_PLACECONTAINER;
import static org.brokenarrow.lootboxes.untlity.ModifyBlock.getFacing;

public class PlayerClick implements Listener {


	private final ContainerDataCache containerDataCache = ContainerDataCache.getInstance();
	private final Lootboxes lootboxes = Lootboxes.getInstance();
	private final RegisterNbtAPI nbt = lootboxes.getNbtAPI();
	private final SettingsData setting = Lootboxes.getInstance().getSettings().getSettingsData();

	@EventHandler
	public void playerPlaceBlock(BlockPlaceEvent event) {
		Block blockPlaced = event.getBlockPlaced();
		if (blockPlaced.getType() != Material.AIR) {
			Player player = event.getPlayer();
			if (!player.hasMetadata(ADD_AND_REMOVE_CONTAINERS.name())) return;
			if (!player.hasPermission("lootboxes.link.containers")) {
				YOU_DONT_HAVE_PERMISSION_TO_LINK.sendMessage(player, "lootboxes.link.containers");
				event.setCancelled(true);
				return;
			}
			if (checkBlockIsContainer(blockPlaced)) {

				Location location = blockPlaced.getLocation();

				String metadata = (String) player.getMetadata(ADD_AND_REMOVE_CONTAINERS.name()).get(0).value();
				ContainerDataBuilder data = containerDataCache.getCacheContainerData(metadata);
				LocationData locationData = containerDataCache.getLocationData(location);

				if (locationData != null) {
					ADD_CONTINERS_THIS_CONTAINER_IS_USED_ALREDY.sendMessage(player, locationData.getContainerData());
					event.setCancelled(true);
					return;
				}
				if (addData(blockPlaced, data, location, metadata)) {
					ADD_CONTINERS_LEFT_CLICK_BLOCK.sendMessage(player, location.getWorld().getName(), location.getBlockX(), location.getBlockY(), location.getBlockZ());
					if (player.getGameMode() == GameMode.SURVIVAL) {
						player.getInventory().setItemInMainHand(CreateItemUtily.of(false,Material.CHEST,
										setting.getPlaceContainerDisplayName(), setting.getPlaceContainerLore())
								.setItemMetaData(ADD_AND_REMOVE_CONTAINERS_ALLOW_PLACECONTAINER.name(), metadata).makeItemStack());
					}
				} else
					event.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void playerBreakBlock(BlockBreakEvent event) {
		Block blockPlaced = event.getBlock();
		if (blockPlaced.getType() != Material.AIR) {
			Player player = event.getPlayer();
			if (!player.hasPermission("lootboxes.link.containers")) return;
			if (checkBlockIsContainer(blockPlaced)) {
				Location location = blockPlaced.getLocation();
				if (!player.hasMetadata(ADD_AND_REMOVE_CONTAINERS.name())) return;

				String metadata = (String) player.getMetadata(ADD_AND_REMOVE_CONTAINERS.name()).get(0).value();
				ContainerDataBuilder data = containerDataCache.getCacheContainerData(metadata);
				if (data == null) return;
				removeData(data, location, metadata);
				ADD_CONTINERS_RIGHT_CLICK_BLOCK.sendMessage(player, location);
			}
		}
	}

	@EventHandler
	public void playerClick(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		Block block = event.getClickedBlock();
		if (!player.hasMetadata(ADD_AND_REMOVE_CONTAINERS.name())) return;
		if (!player.hasPermission("lootboxes.link.containers")) {
			YOU_DONT_HAVE_PERMISSION_TO_LINK.sendMessage(player, "lootboxes.link.containers");
			event.setCancelled(true);
			return;
		}

		Action action = event.getAction();
		if (action == Action.LEFT_CLICK_AIR && player.isSneaking()) {
			String metadata = (String) player.getMetadata(ADD_AND_REMOVE_CONTAINERS.name()).get(0).value();
			player.removeMetadata(ADD_AND_REMOVE_CONTAINERS.name(), Lootboxes.getInstance());

			RunTimedTask.runtaskLater(5, () -> new AlterContainerDataMenu(metadata).menuOpen(player), false);
			ADD_CONTINERS_TURN_OFF_ADD_CONTAINERS.sendMessage(player);
		}
		if (block == null) return;

		Location location = block.getLocation();
		if (checkBlockIsContainer(block)) {
			String metadata = (String) player.getMetadata(ADD_AND_REMOVE_CONTAINERS.name()).get(0).value();
			ContainerDataBuilder data = containerDataCache.getCacheContainerData(metadata);
			LocationData locationData = containerDataCache.getLocationData(location);
			String itemMetadata = null;
			if (event.getItem() != null)
				itemMetadata = nbt.getCompMetadata().getMetadata(event.getItem(), ADD_AND_REMOVE_CONTAINERS_ALLOW_PLACECONTAINER.name());

			if (itemMetadata != null) {
				return;
			}

			if (locationData != null && action == Action.LEFT_CLICK_BLOCK) {
				ADD_CONTINERS_THIS_CONTAINER_IS_USED_ALREDY.sendMessage(player, locationData.getContainerData());
				event.setCancelled(true);
				return;
			}

			Map<Location, ContainerData> containerDataMap = data.getLinkedContainerData();
			if (!containerDataMap.containsKey(location) && action == Action.LEFT_CLICK_BLOCK) {
				if (addData(block, data, location, metadata)) {
					ADD_CONTINERS_LEFT_CLICK_BLOCK.sendMessage(player, location.getWorld().getName(), location.getBlockX(), location.getBlockY(), location.getBlockZ());
					event.setCancelled(true);
				}
			} else if (action == Action.RIGHT_CLICK_BLOCK) {
				event.setCancelled(true);
				removeData(data, location, metadata);
				ADD_CONTINERS_RIGHT_CLICK_BLOCK.sendMessage(player, location.getWorld().getName(), location.getBlockX(), location.getBlockY(), location.getBlockZ());
			}
		}
	}

	public void removeData(ContainerDataBuilder data, Location location, String metadata) {
		ContainerDataBuilder.Builder builder = data.getBuilder();
		Map<Location, ContainerData> containerDataMap = data.getLinkedContainerData();

		containerDataMap.remove(location);
		builder.setContainerData(containerDataMap);
		containerDataCache.setContainerData(metadata, builder.build());
	}

	public boolean addData(Block block, ContainerDataBuilder data, Location location, String metadata) {
		ContainerDataBuilder.Builder builder = data.getBuilder();
		Map<Location, ContainerData> containerDataMap = data.getLinkedContainerData();
		if (lootboxes.getServerVersion().olderThan(Version.v1_13)){
			BlockState blockState = block.getState();
			if (blockState.getData() instanceof DirectionalContainer){
				MaterialData materialData = blockState.getData();
				containerDataMap.put(location, new ContainerData(getFacing(materialData.getData()), block.getType()));
				builder.setContainerData(containerDataMap);
				if (data.getIcon() == null || data.getIcon() == Material.AIR)
					builder.setIcon(block.getType());
				containerDataCache.setContainerData(metadata, builder.build());
				return true;
			}
			return false;
		}
		if (block.getBlockData() instanceof Directional) {
			Directional container = (Directional) block.getBlockData();
			containerDataMap.put(location, new ContainerData(container.getFacing(), block.getType()));
			builder.setContainerData(containerDataMap);
			if (data.getIcon() == null || data.getIcon() == Material.AIR)
				builder.setIcon(block.getType());
			containerDataCache.setContainerData(metadata, builder.build());
			return true;
		}
		return false;
	}

	@EventHandler
	public void playerLeftSever(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		if (player.hasMetadata(ADD_AND_REMOVE_CONTAINERS.name()))
			player.removeMetadata(ADD_AND_REMOVE_CONTAINERS.name(), Lootboxes.getInstance());
		if (player.hasMetadata(ADD_AND_REMOVE_CONTAINERS_ALLOW_PLACECONTAINER.name()))
			player.removeMetadata(ADD_AND_REMOVE_CONTAINERS_ALLOW_PLACECONTAINER.name(), Lootboxes.getInstance());

	}

	@EventHandler
	public void playerJoinSever(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		if (player.hasMetadata(ADD_AND_REMOVE_CONTAINERS.name()))
			player.removeMetadata(ADD_AND_REMOVE_CONTAINERS.name(), Lootboxes.getInstance());
		if (player.hasMetadata(ADD_AND_REMOVE_CONTAINERS_ALLOW_PLACECONTAINER.name()))
			player.removeMetadata(ADD_AND_REMOVE_CONTAINERS_ALLOW_PLACECONTAINER.name(), Lootboxes.getInstance());
	}
}
