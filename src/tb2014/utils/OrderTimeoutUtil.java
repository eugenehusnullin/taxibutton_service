package tb2014.utils;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import tb2014.domain.order.Order;

public class OrderTimeoutUtil {

	public static boolean isTimeoutExpired(Order order, int cancelOrderTimeout, Date checkTime) {
		// 1) date in db in utc without time zone
		Calendar fromDB = Calendar.getInstance();
		fromDB.setTime(order.getBookingDate());

		// 2) now apply UTC time zone
		TimeZone utc = TimeZone.getTimeZone("UTC");
		Calendar booking = Calendar.getInstance(utc);
		booking.set(fromDB.get(Calendar.YEAR), fromDB.get(Calendar.MONTH), fromDB.get(Calendar.DAY_OF_MONTH),
				fromDB.get(Calendar.HOUR_OF_DAY), fromDB.get(Calendar.MINUTE));

		// 3) add cancel order timeout
		booking.add(Calendar.MILLISECOND, cancelOrderTimeout);
		
		return checkTime.after(booking.getTime());
	}
}
