package org.brokenarrow.lootboxes.untlity;

import lombok.Getter;
import org.bukkit.plugin.Plugin;

public final class ServerVersion {


	@Getter
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
		currentServerVersion = Integer.parseInt(plugin.getServer().getBukkitVersion().split("\\.")[1]);
	}

	public enum Version {
		v1_19(19),
		v1_18(18),
		v1_17(17),
		v1_16(16),
		v1_15(15),
		v1_14(14),
		v1_13(13),
		v1_12(12),
		v1_11(11),
		v1_10(10),
		v1_9(9),
		v1_8(8),
		v1_7(7),
		v1_6(6),
		v1_5(5),
		v1_4(4),
		v1_3_AND_BELOW(3);

		private final float version;

		Version(float version) {
			this.version = version;
		}

		public float getVersion() {
			return version;
		}
	}
}