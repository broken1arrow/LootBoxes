package org.brokenarrow.lootboxes.builder;

import java.util.Set;

public class LocationData {

	private final String continerData;
	private final String displayname;
	private final Set<String> keys;

	public LocationData(String continerData, String displayname, Set<String> keys) {
		this.continerData = continerData;
		this.displayname = displayname;
		this.keys = keys;
	}

	public String getContinerData() {
		return continerData;
	}

	public String getDisplayname() {
		return displayname;
	}

	public Set<String> getKeys() {
		return keys;
	}

	@Override
	public String toString() {
		return "LocationData{" +
				"continerData='" + continerData + '\'' +
				", displayname='" + displayname + '\'' +
				", keys=" + keys +
				'}';
	}
}
