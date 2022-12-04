package org.brokenarrow.lootboxes.effects;

import org.brokenarrow.lootboxes.Lootboxes;
import org.brokenarrow.lootboxes.untlity.AddOrRemoveDataFromListAPI;
import org.bukkit.Location;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static org.brokenarrow.lootboxes.untlity.RunTimedTask.runtask;
import static org.brokenarrow.lootboxes.untlity.RunTimedTask.runtaskLater;

public class SpawnContainerEffectsTask extends AddOrRemoveDataFromListAPI {

	public SpawnContainerEffectsTask(final Lootboxes plugin) {
		this.plugin = plugin;
	}


	private final Lootboxes plugin;

	private final Set<Location> cachedLocationsForRemoval = ConcurrentHashMap.newKeySet();
	private final Set<Location> cachedLocationsToAdd = ConcurrentHashMap.newKeySet();
	boolean loadingLocation;

	public void runTask() {

		if (!loadingLocation) {
			final SpawnContainerEffects task = new SpawnContainerEffects(null, null, this.getLocationsList().toArray(new Location[this.getLocationsList().size() + 1]));
			runtask(() -> plugin.getHeavyTasks().addTask("effectsOnChest", 3, 3, task), true);
		}


		final Set<Location> locations = getDefultOrSecondaryList(this.cachedLocationsToAdd, this.cachedLocationsForRemoval);
		if (locations != null && !locations.isEmpty()) {
			loadingLocation = true;
			runtaskLater(1, () -> {
				checkLocationsList();
				loadingLocation = false;
			}, true);
		}
	}

}
