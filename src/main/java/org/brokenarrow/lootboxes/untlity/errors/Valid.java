package org.brokenarrow.lootboxes.untlity.errors;

public class Valid {

	public static void checkNotNull(Object checkNull, String s) {
		if (checkNull == null)
			throw new CatchExceptions(s);
	}

	public static void checkBoolean(boolean bolen, String s) {
		if (!bolen)
			throw new CatchExceptions(s);
	}

	public static void exception(String s) {
		throw new CatchExceptions(s);
	}

	private static class CatchExceptions extends RuntimeException {
		public CatchExceptions(String message) {
			super(message);
		}
	}
}
