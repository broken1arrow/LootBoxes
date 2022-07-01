package org.brokenarrow.lootboxes.untlity;

import org.bukkit.Location;

import java.util.List;
import java.util.Set;

/**
 * This class is used to add or remove locations from the location cache for the different runnable
 * tasks. So it only processes what it needs to do only.
 */
public interface AddOrRemoveDataFromListAPI {

	default void checkLocationsList(final List<Location> listToModify, final Set<Location> locationsToAddOrRemove, final boolean addToList) {

		if (!locationsToAddOrRemove.isEmpty()) {
			if (addToList) {
				addLocationToList(listToModify, locationsToAddOrRemove);
			} else {
				removeLocationFromList(listToModify, locationsToAddOrRemove);
			}
			locationsToAddOrRemove.clear();
		}
	}

	default void addLocationToList(final List<Location> listToModify, final Set<Location> locationsToAdd) {

		if (!locationsToAdd.isEmpty()) {
			locationsToAdd.stream().filter(loc -> !listToModify.contains(loc)).forEach(listToModify::add);
		}
		locationsToAdd.clear();
	}

	default void removeLocationFromList(final List<Location> listToModify, final Set<Location> locationsToRemove) {
		if (!locationsToRemove.isEmpty()) {
			locationsToRemove.forEach(listToModify::remove);
		}
		locationsToRemove.clear();
	}

	default Set<Location> getDefultOrSecondaryList(final Set<Location> defultlist, final Set<Location> secondList) {
		if (defultlist == null || secondList == null) return null;

		return !defultlist.isEmpty() ? defultlist : !secondList.isEmpty() ? secondList : defultlist;
	}

	/**
	 * Add locations it shall sell or craftItems.
	 *
	 * @param location of the continer.
	 */
	void addLocationInList(final Location location);

	/**
	 * Check if list contains location.
	 *
	 * @param location of the continer.
	 * @return true if the list contains the container
	 */
	default boolean isLocationInList(final Location location) {
		return false;
	}

	/**
	 * Remove location from the list.
	 *
	 * @param location of the container you want to remove.
	 */
	void removeLocationInList(final Location location);

}