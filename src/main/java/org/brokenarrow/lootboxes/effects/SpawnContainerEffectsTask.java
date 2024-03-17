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
    int counter;

    public void runTask() {

        if (counter == 5) {
            if (!loadingLocation && !this.getLocationsList().isEmpty()) {
                final SpawnContainerEffects task = new SpawnContainerEffects(null, null, this.getLocationsList().toArray(new Location[0]));
                runtask(() -> plugin.getHeavyTasks().addTask("effectsOnChest", 2, 1, task), true);
            }
            if (!isLoadingLocation()) {
                loadingLocation = true;
                runtaskLater(1, () -> {
                    checkLocationsList();
                    loadingLocation = false;
                }, true);
            }
            counter = 0;
        }
        counter++;
    }

}
