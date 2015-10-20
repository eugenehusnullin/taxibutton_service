package tb.utils;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class DatetimeUtils {


	public static Date localTimeToOtherTimeZone(Date date, String timeZoneId) {
		return new Date(date.getTime()
				- TimeZone.getDefault().getOffset(date.getTime())
				+ TimeZone.getTimeZone(timeZoneId).getOffset(date.getTime()));
	}

	public static Date localTimeToUtc(Date date) {
		return new Date(date.getTime()
				- TimeZone.getDefault().getOffset(date.getTime()));
	}

	public static boolean checkTimeout(Date utcDate, int timeoutMilisecond, Date checkDateLocalTimezone) {
		Calendar booking = utcToLocal(utcDate);
		booking.add(Calendar.MILLISECOND, timeoutMilisecond);
		return checkDateLocalTimezone.after(booking.getTime());
	}

	public static Calendar utcToLocal(Date utcDate) {
		// 1) utcDate without time zone
		Calendar localCalendar = Calendar.getInstance();
		localCalendar.setTime(utcDate);

		// 2) now apply UTC time zone
		Calendar utcCalendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
		utcCalendar.set(localCalendar.get(Calendar.YEAR),
				localCalendar.get(Calendar.MONTH),
				localCalendar.get(Calendar.DAY_OF_MONTH),
				localCalendar.get(Calendar.HOUR_OF_DAY),
				localCalendar.get(Calendar.MINUTE));

		return utcCalendar;
	}
}
