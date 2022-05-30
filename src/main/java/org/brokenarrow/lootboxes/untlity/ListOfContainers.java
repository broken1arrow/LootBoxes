package org.brokenarrow.lootboxes.untlity;

import org.bukkit.Material;

import java.util.ArrayList;
import java.util.List;

public class ListOfContainers {

	public static List<Material> containers() {
		List<Material> materials = new ArrayList<>();
		materials.add(Material.HOPPER);
		materials.add(Material.DISPENSER);
		materials.add(Material.DROPPER);
		materials.add(Material.CHEST);
		materials.add(Material.TRAPPED_CHEST);
		if (ServerVersion.newerThan(ServerVersion.v1_13))
			materials.add(Material.BARREL);

		return materials;
	}
}
