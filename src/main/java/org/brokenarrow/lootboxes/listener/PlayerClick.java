package org.brokenarrow.lootboxes.listener;

import org.brokenarrow.lootboxes.Lootboxes;
import org.brokenarrow.lootboxes.builder.ContainerData;
import org.brokenarrow.lootboxes.builder.ContainerDataBuilder;
import org.brokenarrow.lootboxes.builder.LocationData;
import org.brokenarrow.lootboxes.lootdata.ContainerDataCache;
import org.brokenarrow.lootboxes.menus.ModifyContinerData;
import org.brokenarrow.lootboxes.untlity.RunTimedTask;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.Directional;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.Map;

import static org.brokenarrow.lootboxes.settings.ChatMessages.*;
import static org.brokenarrow.lootboxes.untlity.BlockChecks.checkBlockIsContainer;
import static org.brokenarrow.lootboxes.untlity.KeyMeta.ADD_AND_REMOVE_CONTAINERS;

public class PlayerClick implements Listener {


	private final ContainerDataCache containerDataCache = ContainerDataCache.getInstance();

	@EventHandler
	public void playerClick(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		Block block = event.getClickedBlock();
		if (!player.hasMetadata(ADD_AND_REMOVE_CONTAINERS.name())) return;

		Action action = event.getAction();
		if (action == Action.LEFT_CLICK_AIR && player.isSneaking()) {
			String metadata = (String) player.getMetadata(ADD_AND_REMOVE_CONTAINERS.name()).get(0).value();
			player.removeMetadata(ADD_AND_REMOVE_CONTAINERS.name(), Lootboxes.getInstance());

			RunTimedTask.runtaskLater(5, () -> new ModifyContinerData.AlterContainerDataMenu(metadata).menuOpen(player), false);
			ADD_CONTINERS_TURN_OFF_ADD_CONTAINERS.sendMessage(player);
		}
		if (block == null) return;

		Location location = block.getLocation();
		if (checkBlockIsContainer(block)) {
			String metadata = (String) player.getMetadata(ADD_AND_REMOVE_CONTAINERS.name()).get(0).value();
			ContainerDataBuilder data = containerDataCache.getCacheContainerData(metadata);
			LocationData locationData = containerDataCache.getLocationData(location);

			if (locationData != null && action == Action.LEFT_CLICK_BLOCK) {
				ADD_CONTINERS_THIS_CONTAINER_IS_USED_ALREDY.sendMessage(player, locationData.getContinerData());
				event.setCancelled(true);
				return;
			}

			ContainerDataBuilder.Builder builder = data.getBuilder();
			Map<Location, ContainerData> containerDataMap = data.getLinkedContainerData();
			if (!containerDataMap.containsKey(location) && action == Action.LEFT_CLICK_BLOCK) {
				if (block.getBlockData() instanceof Directional) {
					event.setCancelled(true);
					Directional container = (Directional) block.getBlockData();
					containerDataMap.put(location, new ContainerData(container.getFacing(), block.getType()));
					builder.setContainerData(containerDataMap);
					if (data.getIcon() == null || data.getIcon() == Material.AIR)
						builder.setIcon(block.getType());
					containerDataCache.setContainerData(metadata, builder.build());
					ADD_CONTINERS_LEFT_CLICK_BLOCK.sendMessage(player, location);
				}

			} else if (action == Action.RIGHT_CLICK_BLOCK) {
				event.setCancelled(true);
				containerDataMap.remove(location);
				builder.setContainerData(containerDataMap);
				containerDataCache.setContainerData(metadata, builder.build());
				ADD_CONTINERS_RIGHT_CLICK_BLOCK.sendMessage(player, location);
			}
		}
	}

	@EventHandler
	public void playerLeftSever(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		if (player.hasMetadata(ADD_AND_REMOVE_CONTAINERS.name()))
			player.removeMetadata(ADD_AND_REMOVE_CONTAINERS.name(), Lootboxes.getInstance());
	}

	@EventHandler
	public void playerJoinSever(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		if (player.hasMetadata(ADD_AND_REMOVE_CONTAINERS.name()))
			player.removeMetadata(ADD_AND_REMOVE_CONTAINERS.name(), Lootboxes.getInstance());
	}
}
