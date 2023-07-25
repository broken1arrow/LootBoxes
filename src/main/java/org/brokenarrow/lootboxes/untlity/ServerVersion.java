package org.brokenarrow.lootboxes.untlity;

import org.bukkit.plugin.Plugin;

public final class ServerVersion {

	private final float currentServerVersion;

	public boolean equals(Version version) {
		return serverVersion(version) == 0;
	}

	public boolean newerThan(Version version) {
		return serverVersion(version) > 0;
	}

	public boolean atLeast(Version version) {
		return equals(version) || newerThan(version);
	}

	public boolean olderThan(Version version) {
		return serverVersion(version) < 0;
	}

	public float serverVersion(Version version) {
		return currentServerVersion - version.getVersion();
	}

	public float getCurrentServerVersion() {
		return currentServerVersion;
	}

	public ServerVersion(Plugin plugin) {
		String[] strings = plugin.getServer().getBukkitVersion().split("\\.");
		String firstNumber;
		String secondNumber;
		String firstString = strings[1];
		if (firstString.contains("-")) {
			firstNumber = firstString.substring(0, firstString.lastIndexOf("-"));

			secondNumber = firstString.substring(firstString.lastIndexOf("-") + 1);
			int index = secondNumber.toUpperCase().indexOf("R");
			if (index >= 0)
				secondNumber = secondNumber.substring(index + 1);
		} else {
			String secondString = strings[2];
			firstNumber = firstString;
			secondNumber = secondString.substring(0, secondString.lastIndexOf("-"));
		}
		float version = Float.parseFloat(firstNumber + "." + secondNumber);
		currentServerVersion = (float) Math.floor(version);

	}

	public enum Version {
		v1_19((float) 19.0),
		v1_18((float) 18.0),
		v1_17((float) 17.0),
		v1_16((float) 16.0),
		v1_15((float) 15.0),
		v1_14((float) 14.0),
		v1_13((float) 13.0),
		v1_12((float) 12.0),
		v1_11((float) 11.0),
		v1_10((float) 10.0),
		v1_9((float) 9.0),
		v1_8((float) 8.0),
		v1_7((float) 7.0),
		v1_6((float) 6.0),
		v1_5((float) 5.0),
		v1_4((float) 4.0),
		v1_3_AND_BELOW((float) 3.0);

		private final float version;

		Version(float version) {
			this.version = version;
		}

		public float getVersion() {
			return version;
		}
	}
}