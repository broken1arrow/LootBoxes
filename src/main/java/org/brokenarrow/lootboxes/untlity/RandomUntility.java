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

	public RandomUntility newRandomsSeed() {
		random = new Random(seedUniquifier() ^ System.nanoTime());
		return this;
	}

	public int randomIntNumber(int origin, int bound) {
		return randomIntNumber(origin, bound, false);
	}

	public int randomIntNumber(int origin, int bound, boolean newRandomsSeed) {
		if (newRandomsSeed)
			newRandomsSeed();
		return origin + nextRandomInt(bound - origin + 1);
	}

	public boolean chance(final int percent) {
		return chance(percent, false);
	}

	public boolean chance(final int percent, boolean newRandomsSeed) {
		if (newRandomsSeed)
			newRandomsSeed();
		if (percent == 100)
			return true;
		return random.nextDouble() * 100D < percent;
	}

	public int nextRandomInt(int bound) {
		if (bound < 0)
			bound = 1;
		return random.nextInt(bound);
	}
}
