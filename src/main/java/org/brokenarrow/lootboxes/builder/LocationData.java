package org.brokenarrow.lootboxes.builder;

import java.util.Map;

public final class LocationData {

	private final String continerData;
	private final Map<String, KeysData> keys;

	public LocationData(final String continerData, final Map<String, KeysData> keys) {
		this.continerData = continerData;
		this.keys = keys;
	}

	public String getContinerData() {
		return continerData;
	}


	public Map<String, KeysData> getKeys() {
		return keys;
	}


	@Override
	public String toString() {
		return "LocationData{" +
				"continerData='" + continerData + '\'' +
				", keys=" + keys +
				'}';
	}
}
