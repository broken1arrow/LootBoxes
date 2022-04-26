package org.brokenarrow.lootboxes.untlity;

import com.google.common.base.Enums;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MobList {
	private List<EntityType> entityType;
	private boolean firstRun;

	public MobList() {
		if (this.entityType == null) {
			this.entityType = Stream.of(EntityType.values()).filter((entity) -> entity.isSpawnable() && entity.isAlive()
			).sorted(Comparator.comparing(Enum::name)).collect(Collectors.toList());
			firstRun = true;
		}
	}

	public Material makeSpawnEggs(final EntityType type) {
		try {

			String name = type.toString() + "_SPAWN_EGG";

			// Special cases
			if (type.name().equals("ZOMBIFIED_PIGLIN"))
				if (ServerVersion.newerThan(ServerVersion.v1_15))
					name = "ZOMBIFIED_PIGLIN_SPAWN_EGG";
				else
					name = "ZOMBIE_PIGMAN_SPAWN_EGG";

			else if (type == EntityType.MUSHROOM_COW)
				name = "MOOSHROOM_SPAWN_EGG";

			return Enums.getIfPresent(Material.class, name).orNull();

		} catch (final Throwable throwable) {
			throwable.printStackTrace();
			//Debugger.saveError(throwable, "Something went wrong while creating spawn egg!", "Type: " + type);
		}
		return Material.SHEEP_SPAWN_EGG;
	}


	public List<EntityType> getEntityTypeList(String itemsToSearchFor) {
		if (itemsToSearchFor != null && !itemsToSearchFor.isEmpty())
			return this.entityType.stream().filter((entityType) -> entityType.toString().contains(itemsToSearchFor.toUpperCase())).sorted(Comparator.comparing(Enum::name)).collect(Collectors.toList());
		return this.entityType;
	}
}
