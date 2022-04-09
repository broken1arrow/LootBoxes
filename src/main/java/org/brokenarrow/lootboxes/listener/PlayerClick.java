package org.brokenarrow.lootboxes.listener;

import org.brokenarrow.lootboxes.Lootboxes;
import org.brokenarrow.lootboxes.builder.ContainerDataBuilder;
import org.brokenarrow.lootboxes.lootdata.ContainerData;
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

public class PlayerClick implements Listener {


	private final ContainerData containerData = ContainerData.getInstance();

	@EventHandler
	public void playerClick(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		Block block = event.getClickedBlock();
		if (!player.hasMetadata("addRemovecontainers")) return;

		Action action = event.getAction();
		if (action == Action.LEFT_CLICK_AIR && player.isSneaking()) {
			player.removeMetadata("addRemovecontainers", Lootboxes.getInstance());
			player.sendMessage("You have now turn now off add ar remove mode");
		}
		if (block == null) return;

		Location location = block.getLocation();
		if (block.getType() == Material.CHEST || block.getType() == Material.BARREL || block.getType() == Material.HOPPER) {
			String metadata = (String) player.getMetadata("addRemovecontainers").get(0).value();
			System.out.println("metadata  " + metadata);
			ContainerDataBuilder data = containerData.getCacheContainerData(metadata);

			ContainerDataBuilder.Builder builder = data.getBuilder();
			Map<Location, ContainerDataBuilder.ContainerData> containerDataMap = data.getLinkedContainerData();
			if (!containerDataMap.containsKey(location) && action == Action.LEFT_CLICK_BLOCK) {
				if (block.getBlockData() instanceof Directional) {
					event.setCancelled(true);
					Directional container = (Directional) block.getBlockData();
					containerDataMap.put(location, new ContainerDataBuilder.ContainerData(container.getFacing(), block.getType()));
					builder.setContainerData(containerDataMap);
					if (data.getIcon() == null || data.getIcon() == Material.AIR)
						builder.setIcon(block.getType());
					containerData.setContainerData(metadata, builder.build());
				}

			} else if (action == Action.RIGHT_CLICK_BLOCK) {
				containerDataMap.remove(location);
				builder.setContainerData(containerDataMap);
				containerData.setContainerData(metadata, builder.build());
			}
		}
	}

	@EventHandler
	public void playerLeftSever(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		if (player.hasMetadata("addRemovecontainers"))
			player.removeMetadata("addRemovecontainers", Lootboxes.getInstance());
	}

	@EventHandler
	public void playerLeftSever(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		if (player.hasMetadata("addRemovecontainers"))
			player.removeMetadata("addRemovecontainers", Lootboxes.getInstance());
	}
}
