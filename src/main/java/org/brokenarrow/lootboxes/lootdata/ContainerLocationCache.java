package org.brokenarrow.lootboxes.lootdata;

import org.brokenarrow.lootboxes.builder.LocationData;
import org.bukkit.Location;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

public class ContainerLocationCache {

    private final Map<Location, LocationData> cachedLocations = new HashMap<>();

    public Map<Location, LocationData> getCachedLocations() {
        return this.cachedLocations;
    }

    @Nullable
    public LocationData getLocationData(final Location location) {
        return this.cachedLocations.get(location);
    }

    public void put(final Location location, final LocationData locationData) {
        this.cachedLocations.put(location, locationData);
    }


}
