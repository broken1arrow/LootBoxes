package org.brokenarrow.lootboxes.builder;

import java.util.Map;

public final class LocationData {

	private final String containerKey;
	private final Map<String, KeysData> keys;

	public LocationData(final String containerKey, final Map<String, KeysData> keys) {
		this.containerKey = containerKey;
		this.keys = keys;
	}

	public String getContainerKey() {
		return containerKey;
	}


	public Map<String, KeysData> getKeys() {
		return keys;
	}


	@Override
	public String toString() {
		return "LocationData{" +
				"containerData='" + containerKey + '\'' +
				", keys=" + keys +
				'}';
	}
}
