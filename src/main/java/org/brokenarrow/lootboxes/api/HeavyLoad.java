package org.brokenarrow.lootboxes.api;

public interface HeavyLoad {

	boolean compute();

	default void computeTask() {
		this.compute();
	/*	final long stoptime = (long) (System.nanoTime() + (1000_000 * this.getMilliPerTick()));
		while (System.nanoTime() <= stoptime) {
			if (!this.compute())
				break;
		}*/
	}

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
