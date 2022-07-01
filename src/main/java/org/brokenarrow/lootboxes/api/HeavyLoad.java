package org.brokenarrow.lootboxes.api;

public interface HeavyLoad {

	void compute();

	default boolean reschedule() {
		return false;
	}

	default boolean computeWithDelay(final int conter) {
		return false;
	}

	default double getMilliPerTick() {
		return 4.5;
	}

	default long rescheduleMaxRunTime() {
		return 0;
	}

}
