package org.brokenarrow.lootboxes.untlity;

import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;


public final class RandomUntility extends Random {

	private static Random random;
	private static final AtomicLong seedUniquifier
			= new AtomicLong(8682522807148012L);

	public RandomUntility() {
		random = new Random(seedUniquifier() ^ System.nanoTime());
	}

	private static long seedUniquifier() {
		// L'Ecuyer, "Tables of Linear Congruential Generators of
		// Different Sizes and Good Lattice Structure", 1999
		for (; ; ) {
			long current = seedUniquifier.get();
			long next = current * 1181783497276652981L;
			if (seedUniquifier.compareAndSet(current, next))
				return next;
		}
	}

	public static int randomIntNumber(int origin, int bound) {
		return randomIntNumber(origin, bound, false);
	}

	public static int randomIntNumber(int origin, int bound, boolean newRandomsSeed) {
		if (newRandomsSeed)
			new RandomUntility();
		return origin + nextRandomInt(bound - origin + 1);
	}

	public static boolean chance(final int percent) {
		return chance(percent, false);
	}

	public static boolean chance(final int percent, boolean newRandomsSeed) {
		if (newRandomsSeed)
			new RandomUntility();
		return random.nextDouble() * 100D < percent;
	}

	public static int nextRandomInt(final int bound) {
		return random.nextInt(bound);
	}
}
