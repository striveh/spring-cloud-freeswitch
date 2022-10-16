/** */
package com.striveh.callcenter.common.util;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DateTool {
	/** 日志对象 */
	protected static Logger logger = LogManager.getLogger(DateTool.class);

	/**
	 * 用默认格式格式化当前时间，见formatDate(String fmt, Date date)
	 * 
	 * @return
	 */
	public static String formatDate() {
		return formatDate(null, null);
	}

	/**
	 * 用默认格式格式化当前时间，见formatDate(String fmt, Date date)
	 * 
	 * @return
	 */
	public static String formatDate(String fmt) {
		return formatDate(fmt, null);
	}

	/***
	 * 格式化时间
	 * 
	 * @param fmt 默认yyyy-MM-dd HH:mm:ss
	 * @param date 默认当前时间
	 * @return
	 */
	public static String formatDate(String fmt, Date date) {
		if (date == null) {
			date = new Date();
		}

		SimpleDateFormat format = null;
		if (fmt == null) {
			format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		} else {
			format = new SimpleDateFormat(fmt);
		}
		return format.format(date);
	}

	/***
	 * 字符串转日期，默认yyyy-MM-dd HH:mm:ss
	 * 
	 * @param dateStr 默认当前时间
	 * @return
	 * @throws ParseException
	 */
	public static Date parseDate(String dateStr) throws ParseException {
		return parseDate(null, dateStr);
	}

	/***
	 * 字符串转日期
	 * 
	 * @param fmt 默认yyyy-MM-dd HH:mm:ss
	 * @param dateStr 默认当前时间
	 * @return
	 * @throws ParseException
	 */
	public static Date parseDate(String fmt, String dateStr) throws ParseException {
		SimpleDateFormat format = null;
		if (fmt == null) {
			format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		} else {
			format = new SimpleDateFormat(fmt);
		}
		return format.parse(dateStr);
	}

	/**
	 * 计算两个日期之前的月数，忽略天,时,分,秒的计算
	 * @param fromDate
	 * @param toDate
	 * @return
	 */
	public static Integer monthCompare(Date fromDate, Date toDate){
		Calendar from  =  Calendar.getInstance();
		from.setTime(fromDate);
		Calendar  to  =  Calendar.getInstance();
		to.setTime(toDate);
		//只要年月
		int fromYear = from.get(Calendar.YEAR);
		int fromMonth = from.get(Calendar.MONTH);
		int toYear = to.get(Calendar.YEAR);
		int toMonth = to.get(Calendar.MONTH);
		int month = toYear *  12  + toMonth  -  (fromYear  *  12  +  fromMonth);
		return Math.abs(month);
	}
	/**
	 * 根据出生计算当前年龄
	 * 
	 * @param birthday
	 * @return
	 */
	public static int getCurrAge(Date birthday) {
		Calendar now = Calendar.getInstance();
		Calendar c = Calendar.getInstance();
		c.setTime(birthday);

		int age = now.get(Calendar.YEAR) - c.get(Calendar.YEAR);
		if (now.get(Calendar.MONTH) < c.get(Calendar.MONTH)) {// 不满月
			age--;
		} else if (now.get(Calendar.MONTH) == c.get(Calendar.MONTH)) {// 不满日
			if (now.get(Calendar.DAY_OF_MONTH) < c.get(Calendar.DAY_OF_MONTH)) {
				age--;
			}
		}
		return age;
	}

	/**
	 * 判断是否是新的一天
	 * 
	 * @param firstDay
	 * @param secondDay
	 * @return
	 */
	public static boolean isNewDay(Date firstDay, Date secondDay) {
		Calendar c1 = Calendar.getInstance();
		c1.setTime(firstDay);
		c1.set(Calendar.HOUR_OF_DAY, 0);
		c1.set(Calendar.MINUTE, 0);
		c1.set(Calendar.SECOND, 0);
		c1.set(Calendar.MILLISECOND, 0);

		Calendar c2 = Calendar.getInstance();
		c2.setTime(secondDay);
		c2.set(Calendar.HOUR_OF_DAY, 0);
		c2.set(Calendar.MINUTE, 0);
		c2.set(Calendar.SECOND, 0);
		c2.set(Calendar.MILLISECOND, 0);
		if (c1.getTimeInMillis() < c2.getTimeInMillis()) {
			return true;
		}
		return false;
	}

	/**
	 * 取得当前时间戳
	 * 
	 * @return
	 */
	public static Timestamp getTimestamp() {
		return new Timestamp(System.currentTimeMillis());
	}

	/**
	 * 计算两个日期之前的天数，忽略时分秒的计算
	 * 
	 * @param smallDay
	 * @param largeDay
	 * @return
	 */
	public static long getBetweenDays(Date smallDay, Date largeDay) {
		Calendar c1 = Calendar.getInstance();
		c1.setTime(smallDay);
		c1.set(Calendar.HOUR_OF_DAY, 0);
		c1.set(Calendar.MINUTE, 0);
		c1.set(Calendar.SECOND, 0);
		c1.set(Calendar.MILLISECOND, 0);

		Calendar c2 = Calendar.getInstance();
		c2.setTime(largeDay);
		c2.set(Calendar.HOUR_OF_DAY, 0);
		c2.set(Calendar.MINUTE, 0);
		c2.set(Calendar.SECOND, 0);
		c2.set(Calendar.MILLISECOND, 0);
		return (c2.getTimeInMillis() - c1.getTimeInMillis()) / (24 * 3600 * 1000);
	}

	/**
	 * 判断当前时间是否在两个时段（时分秒）中间
	 * isBetweenTime("10:24:00","15:00:00")
	 * 
	 * @param startStr
	 * @param endStr
	 * @return
	 */
	public static boolean isBetweenTime(String startStr, String endStr) {
		return isBetweenTime(new Date(), startStr, endStr);
	}

	/**
	 * 判断时间是否在两个时段（时分秒）中间
	 * isBetweenTime("10:30:00","15:00:00")
	 * isBetweenTime("15:00:00","10:30:00")
	 * 
	 * @param startStr
	 * @param endStr
	 * @return
	 */
	public static boolean isBetweenTime(Date date, String startStr, String endStr) {
		Calendar s = Calendar.getInstance();// 开始时间
		String[] start = startStr.split(":");
		s.set(Calendar.HOUR_OF_DAY, new Integer(start[0]));
		s.set(Calendar.MINUTE, new Integer(start[1]));
		s.set(Calendar.SECOND, start.length < 3 ? 0 : new Integer(start[2]));
		s.set(Calendar.MILLISECOND, 0);

		Calendar e = Calendar.getInstance();// 结束时间
		String[] end = endStr.split(":");
		e.set(Calendar.HOUR_OF_DAY, new Integer(end[0]));
		e.set(Calendar.MINUTE, new Integer(end[1]));
		e.set(Calendar.SECOND, start.length < 3 ? 0 : new Integer(start[2]));
		e.set(Calendar.MILLISECOND, 0);

		if (e.after(s)) {
			// 10:30:00~15:00:00
			return isBetweenTime(date, s.getTime(), e.getTime());
		} else {
			// 15:00:00~10:30:00==>15:00:00~23:59:59 || 00:00:00~10:30:00
			Calendar e1 = Calendar.getInstance();// 当天23:59:59.999
			e1.set(Calendar.HOUR_OF_DAY, 23);
			e1.set(Calendar.MINUTE, 59);
			e1.set(Calendar.SECOND, 59);
			e1.set(Calendar.MILLISECOND, 999);

			Calendar s1 = Calendar.getInstance();// 当天00:00:00.000
			s1.set(Calendar.HOUR_OF_DAY, 0);
			s1.set(Calendar.MINUTE, 0);
			s1.set(Calendar.SECOND, 0);
			s1.set(Calendar.MILLISECOND, 0);
			return isBetweenTime(date, s.getTime(), e1.getTime()) || isBetweenTime(date, s1.getTime(), e.getTime());
		}
	}

	/**
	 * 判断当前时间是否在两个时段（时分秒）中间
	 * 
	 * @param start
	 * @param end
	 * @return
	 */
	public static boolean isBetweenTime(Date start, Date end) {
		return isBetweenTime(new Date(), start, end);
	}

	/**
	 * 判断当前时间是否在两个时段（时分秒）中间
	 * 
	 * @param start
	 * @param end
	 * @return
	 */
	public static boolean isBetweenTime(Date date, Date start, Date end) {
		return start.getTime() <= date.getTime() && date.getTime() <= end.getTime();
	}

	/**
	 * 取得离今天n天以后的日期
	 *
	 * @param n
	 * @return
	 */
	public static Date getDateAfterNow(int n) {
		Calendar now = Calendar.getInstance();
		now.add(Calendar.DAY_OF_MONTH, n);
		return now.getTime();
	}
	
	
	/**
	 * 取得离某个日起n天以后的日期
	 *
	 * @param n
	 * @return
	 */
	public static Date getDateAfterNow(Date date ,int n) {
		Calendar now = Calendar.getInstance();
		now.setTime(date);
		now.add(Calendar.DAY_OF_MONTH, n);
		return now.getTime();
	}

	public static void main(String[] args) throws InterruptedException {
//		Calendar c = Calendar.getInstance();// 开始时间
//		c.set(Calendar.HOUR_OF_DAY, 21);
//		System.out.println(isBetweenTime(c.getTime(), "22:00:00", "02:00:00"));
//		c.set(Calendar.HOUR_OF_DAY, 22);
//		System.out.println(isBetweenTime(c.getTime(), "22:00:00", "02:00"));
//		c.set(Calendar.HOUR_OF_DAY, 01);
//		System.out.println(isBetweenTime(c.getTime(), "22:00", "02:00:00"));
//		c.set(Calendar.HOUR_OF_DAY, 02);
//		System.out.println(isBetweenTime(c.getTime(), "22:00", "02:00"));
//		c.set(Calendar.HOUR_OF_DAY, 02);
//		System.out.println(isBetweenTime(c.getTime(), "02:00", "22:00"));
//		c.add(Calendar.DAY_OF_MONTH, -1);
//		System.out.println(getBetweenDays(new Date(), c.getTime()));
		Calendar calendar=Calendar.getInstance();
		calendar.add(Calendar.DAY_OF_MONTH,5);
		Thread.sleep(1000L);
		System.out.println(isAfterNow(new java.sql.Date(calendar.getTimeInMillis())));

	}


	/**
	 *  在当前时间之后
	 *
	 * @param expireDate
	 * @return
	 */
	public static boolean isAfterNow(Timestamp expireDate) {
		return expireDate.after(new Timestamp(System.currentTimeMillis()));
	}

	/**
	 *  过期
	 * @param expireDate
	 * @return
	 */
	public static boolean isAfterNow(java.sql.Date expireDate) {
		return expireDate.after(new Timestamp(System.currentTimeMillis()));
	}


	private static Calendar initCalendar(Date day) {
		Calendar c = Calendar.getInstance();
		if (day != null) {
			c.setTime(day);
		}
		c.set(Calendar.HOUR_OF_DAY, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MILLISECOND, 0);

		return c;
	}

	public static Date getTheDayEnd(long time) {
		Calendar c = initCalendar(new Date(time));
		c.add(Calendar.DAY_OF_YEAR, 1);
		c.add(Calendar.MILLISECOND, -1);
		return c.getTime();
	}

	/**
	 * 获取今天剩余的秒数
	 *
	 * @return
	 */
	public static int getTodayRemainSeconds() {
		return (int) (getTheDayEnd(new Date().getTime()).getTime() - System.currentTimeMillis()) / 1000;
	}
}
