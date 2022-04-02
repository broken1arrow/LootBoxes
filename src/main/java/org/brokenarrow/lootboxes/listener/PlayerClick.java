package org.brokenarrow.lootboxes.listener;

import org.brokenarrow.lootboxes.builder.ContainerDataBuilder;
import org.brokenarrow.lootboxes.lootdata.ContainerData;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.Directional;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.Map;

public class PlayerClick implements Listener {


	private final ContainerData containerData = ContainerData.getInstance();

	@EventHandler
	public void playerClick(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		Block block = event.getClickedBlock();
		if (block == null) return;
		
		Location location = event.getClickedBlock().getLocation();
		if (!player.hasMetadata("addRemovecontainers")) return;

		if (block.getType() == Material.CHEST || block.getType() == Material.BARREL || block.getType() == Material.HOPPER) {
			String metadata = (String) player.getMetadata("addRemovecontainers").get(0).value();
			System.out.println("metadata  " + metadata);
			ContainerDataBuilder data = containerData.getCacheContainerData(metadata);

			ContainerDataBuilder.Builder builder = data.getBuilder();
			Map<Location, ContainerDataBuilder.ContainerData> containerDataMap = data.getLinkedContainerData();
			if (!containerDataMap.containsKey(location)) {
				if (block.getBlockData() instanceof Directional) {
					event.setCancelled(true);
					Directional container = (Directional) block.getBlockData();
					containerDataMap.put(location, new ContainerDataBuilder.ContainerData(container.getFacing(), block.getType()));
					System.out.println("tesjhkvfjy playerClick facing: " + container.getFacing());
					builder.setContainerData(containerDataMap);
					if (data.getIcon() == null || data.getIcon() == Material.AIR)
						builder.setIcon(block.getType());
					containerData.setContainerData(metadata, builder.build());
					System.out.println("tesjhkvfjy playerClick" + containerData.getCacheContainerData());
				}

			}

		}


	}
}
