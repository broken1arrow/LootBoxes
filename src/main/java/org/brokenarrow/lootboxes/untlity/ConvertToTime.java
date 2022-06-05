package org.brokenarrow.lootboxes.untlity;

import static org.brokenarrow.lootboxes.settings.ChatMessages.*;

/**
 * Convert millseconds to days, houers,minutes,seconds.
 */

public class ConvertToTime {

	private static final long millisToSeconds = 1000;
	private static final long millisToMinute = millisToSeconds * 60;
	private static final long millisTohours = millisToMinute * 60;
	private static final long millisToDays = millisTohours * 24;

	public static String toTimeFromMillis(long milliseconds) {
		return toTime(milliseconds / 1000);
	}

	public static String toTime(long seconds) {

		long time = System.currentTimeMillis() + (1000 * seconds);
		long currentTime = System.currentTimeMillis();
		long second = 0;
		long min = 0;
		long hours = 0;
		long days = 0;

		if (!(time - currentTime / millisToSeconds % 60 == 0))
			second = (time - currentTime) / millisToSeconds % 60;
		if (!(time - currentTime / millisToMinute % 60 == 0))
			min = (time - currentTime) / millisToMinute % 60;
		if (!(time - currentTime / millisTohours % 60 == 0))
			hours = (time - currentTime) / millisTohours % 60;
		if (!(time - currentTime / millisToDays % 60 == 0))
			days = (time - currentTime) / millisToDays % 60;

		return (days == 0 ? "" : days + day(days)) + (hours == 0 ? "" : (days != 0 ? " " : "") + hours + hour(hours)) + (min == 0 ? "" : (hours != 0 ? " " : "") + min + minute(min)) + (second == 0 ? "" : (min != 0 ? " " : "") + second + second(second));
	}

	public static String day(long amount) {
		if (amount <= 1)
			return DAY.languageMessages();
		else
			return DAYS.languageMessages();
	}

	public static String hour(long amount) {
		if (amount <= 1)
			return HOUR.languageMessages();
		else
			return HOURS.languageMessages();
	}

	public static String minute(long amount) {
		if (amount <= 1)
			return MINUTE.languageMessages();
		else
			return MINUTES.languageMessages();
	}

	public static String second(long amount) {
		if (amount <= 1)
			return SECOND.languageMessages();
		else
			return SECONDS.languageMessages();
	}

}
