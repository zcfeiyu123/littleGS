package utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class TimeUtils {

	/**
	 * get time string of current time in a given time format, when the time format is not supported, return an empty string
	 * @param format time format, for instance "yyyy-MM-dd HH:mm:ss"
	 * @return
	 */
	public static String getTimeStr(String format)
	{
		try
		{
		    return new SimpleDateFormat(format).format(Calendar.getInstance().getTime());
		}catch(Exception e)
		{
			e.printStackTrace();
			return "";
		}
	}
	
	/**
	 * return a time string of current time in a fixed time format "yyyy-MM-dd HH:mm:ss", for instance "2013-05-21 13:36:20"
	 * @return
	 */
	public static String getTimeStr()
	{
		return getTimeStr("yyyy-MM-dd HH:mm:ss");
	}
	
	/**
	 * get today's date string in a given dateFormat
	 * @param dateFormat
	 * @return
	 */
	public static String getToday(String dateFormat)
	{
		return  new SimpleDateFormat(dateFormat).format(Calendar.getInstance().getTime());
	}
	
	/**
	 * translate a date into a string with a given format 
	 * @param d date
	 * @param formatStr date format
	 * @return
	 */
	public static String DateToString(Date d, String formatStr)
	{
		return new SimpleDateFormat(formatStr).format(d);
	}
	
	/**
	 * translate a string into a valid date, return null if there is an exception
	 * @param dateStr date string, for instance "20130512"
	 * @param formatStr the format of the give string, for instance "yyyyMMdd"
	 * @return
	 */
	public static Date StringToDate(String dateStr,String formatStr){
		DateFormat dd = new SimpleDateFormat(formatStr);
		Date date=null;
		try {
			date = dd.parse(dateStr);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return date;
	}
		
	
	/**
	 * add days to current date d
	 * @param d date
	 * @param days days to add
	 * @return
	 */
	public static Date addDay(Date d, int days) {
		Date date = new Date();
		date.setTime(d.getTime() + days * TimeUnit.DAYS.toMillis(1L));
		return date;		
	}
	
	public static Date addYear(Date date, int years) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.YEAR, years);
		return cal.getTime();		
	}
	
	/**
	 * compute days between two dates 
	 * @param d1 date1
	 * @param d2 date2
	 * @return
	 */
	public static int dateDiff(Date d1, Date d2) {
		return (int) ((d1.getTime() - d2.getTime()) / TimeUnit.DAYS.toMillis(1L));
	}
	
	public static int getDay(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		return cal.get(Calendar.DAY_OF_MONTH);
	}
	
	public static int getDaysOfCurrentMonth(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.set(Calendar.DATE, 1);
		cal.roll(Calendar.DATE, -1);
		return cal.get(Calendar.DATE);
	}
	
	public static double getIndexOfMonth(Date date) {
		int curDay = getDay(date);
		int curDays = getDaysOfCurrentMonth(date);
		return (-0.5 + curDay - curDays) / curDays;
	}
	
	public static String getYearMonth(Date date) {
		return DateToString(date, "yyyyMM");
	}
	
	public static boolean isSameMonth(Date date1, Date date2) {
		return getYearMonth(date1).equals(getYearMonth(date2));
	}
	public static Date getMonthHead(Date date) {
		return StringToDate(DateToString(date, "yyyyMM"), "yyyyMM");		
	}
	public static boolean isMonthHead(Date date) {
		return (DateToString(date, "dd").equals("01"));
	}
	public static int monthCount(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		return cal.get(Calendar.YEAR) * 12 + cal.get(Calendar.MONTH) - Calendar.JANUARY;
	}
	public static String monthToID(int monthCount) {
		return String.format("%04d%02d", monthCount / 12, monthCount % 12 + 1);		
	}

    /**
     * check the date
     */
    public static boolean isDateRightFormat(String dateStr, String pattern)
    {
        if(StringToDate(dateStr, pattern) == null)
        {
            return false;
        }
        return true;
    }
}
