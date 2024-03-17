package org.brokenarrow.lootboxes.untlity;

import org.bukkit.Location;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static org.brokenarrow.lootboxes.untlity.RunTimedTask.runtaskLater;

/**
 * This class is used to add or remove locations from the location cache for the different runnable
 * tasks. So it only processes what it needs to do only.
 */
public abstract class AddOrRemoveDataFromListAPI {

	private final List<Location> locationsList = new ArrayList<>();
	private final Map<Location, Boolean> cachedLocations = new ConcurrentHashMap<>();
	private boolean goThruLocationList;
	private boolean loadingLocation;
	private BukkitTask task;

	public void checkLocationsList() {
		final Map<Location, Boolean> tempLoc = this.getTempLocations();
		System.out.println("tempLoc " + tempLoc);
		if (tempLoc.isEmpty()) return;
		if (task == null) {
			loadingLocation = true;
			this.task = runtaskLater(1, () -> {
				tempLoc.forEach((location, value) -> {
					if (value) {
						if (!this.locationsList.contains(location))
							this.locationsList.add(location);
					} else {
						this.locationsList.remove(location);
					}
				});
				loadingLocation = false;
				task = null;
				tempLoc.clear();
			}, true);
		}
	}

	public Set<Location> getTempStoredLocations() {
		final Map<Location, Boolean> dataSet = this.getTempLocations();
		if (dataSet.isEmpty()) return null;

		return dataSet.keySet();
	}

	public Set<Location> getDefultOrSecondaryList(final Set<Location> defultlist, final Set<Location> secondList) {
		if (defultlist == null || secondList == null) return null;

		return !defultlist.isEmpty() ? defultlist : !secondList.isEmpty() ? secondList : defultlist;
	}

	public void setGoThruLocationList(final boolean goThruLocationList) {
		this.goThruLocationList = goThruLocationList;
	}

	protected List<Location> getLocationsList() {
		return locationsList;
	}

	@NotNull
	protected Map<Location, Boolean> getTempLocations() {
		return cachedLocations;
	}

	/**
	 * Check if it load locations to the list.
	 *
	 * @return true if it load locations.
	 */
	public boolean isLoadingLocation() {
		return loadingLocation;
	}

	/**
	 * Add locations it shall sell or craftItems.
	 *
	 * @param location of the continer.
	 */
	public void addLocationInList(@NotNull final Location location) {
		getTempLocations().put(location, true);
	}

	/**
	 * Check if list contains location.
	 *
	 * @param location of the continer.
	 * @return true if the list contains the container
	 */
	public boolean isLocationInList(@NotNull final Location location) {
		return this.locationsList.contains(location);
	}

	/**
	 * Remove location from the list.
	 *
	 * @param location of the container you want to remove.
	 */
	public void removeLocationInList(@NotNull final Location location) {
		getTempLocations().put(location, false);
	}

}