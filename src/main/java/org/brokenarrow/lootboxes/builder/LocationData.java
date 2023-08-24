package org.brokenarrow.lootboxes.builder;

import java.util.Map;

public final class LocationData {

	private final String containerData;
	private final Map<String, KeysData> keys;

	public LocationData(final String containerData, final Map<String, KeysData> keys) {
		this.containerData = containerData;
		this.keys = keys;
	}

	public String getContainerData() {
		return containerData;
	}


	public Map<String, KeysData> getKeys() {
		return keys;
	}


	@Override
	public String toString() {
		return "LocationData{" +
				"containerData='" + containerData + '\'' +
				", keys=" + keys +
				'}';
	}
}
