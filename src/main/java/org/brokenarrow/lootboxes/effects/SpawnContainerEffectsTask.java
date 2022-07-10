package org.brokenarrow.lootboxes.effects;

import org.brokenarrow.lootboxes.Lootboxes;
import org.brokenarrow.lootboxes.untlity.AddOrRemoveDataFromListAPI;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static org.brokenarrow.lootboxes.untlity.RunTimedTask.runtask;
import static org.brokenarrow.lootboxes.untlity.RunTimedTask.runtaskLater;

public class SpawnContainerEffectsTask implements AddOrRemoveDataFromListAPI {

	public SpawnContainerEffectsTask(final Lootboxes plugin) {
		this.plugin = plugin;
	}


	private final Lootboxes plugin;

	private final List<Location> locationsList = new ArrayList<>();
	private final Set<Location> cachedLocationsForRemoval = ConcurrentHashMap.newKeySet();
	private final Set<Location> cachedLocationsToAdd = ConcurrentHashMap.newKeySet();
	boolean loadingLocation;

	public void runTask() {

		if (!loadingLocation) {
			final SpawnContainerEffects task = new SpawnContainerEffects(null, null, this.locationsList.toArray(new Location[this.locationsList.size() + 1]));
			runtask(() -> plugin.getHeavyTasks().addTask("effectsOnChest", 3, 3, task), true);
		}


		final Set<Location> locations = getDefultOrSecondaryList(this.cachedLocationsToAdd, this.cachedLocationsForRemoval);
		if (locations != null && !locations.isEmpty()) {
			loadingLocation = true;
			runtaskLater(1, () -> {
				checkLocationsList(this.locationsList, locations, !this.cachedLocationsToAdd.isEmpty());
				loadingLocation = false;
			}, true);
		}
	}

	/**
	 * Add locations it shall sell or craftItems.
	 *
	 * @param location of the continer.
	 */
	@Override
	public void addLocationInList(final Location location) {
		if (!this.locationsList.contains(location)) {
			this.cachedLocationsToAdd.add(location);
			this.cachedLocationsForRemoval.remove(location);
		}
	}

	/**
	 * Check if it contains location.
	 *
	 * @param location of the continer.
	 */
	@Override
	public void removeLocationInList(final Location location) {
		cachedLocationsForRemoval.add(location);
	}
}
