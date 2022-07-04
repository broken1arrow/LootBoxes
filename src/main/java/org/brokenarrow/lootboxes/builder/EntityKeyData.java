package org.brokenarrow.lootboxes.builder;

public final class EntityKeyData {
	private final String keyName;
	private final String containerDataFileName;

	public EntityKeyData(final String keyName, final String containerDataFileName) {
		this.keyName = keyName;
		this.containerDataFileName = containerDataFileName;
	}

	public String getKeyName() {
		return keyName;
	}

	public String getContainerDataFileName() {
		return containerDataFileName;
	}

	@Override
	public String toString() {
		return "EntityKeyData{" +
				"keyName='" + keyName + '\'' +
				", containerDataFileName='" + containerDataFileName + '\'' +
				'}';
	}
}
