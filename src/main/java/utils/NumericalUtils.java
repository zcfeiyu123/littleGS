package utils;

import java.text.DecimalFormat;

public class NumericalUtils {

	private static final DecimalFormat df = (DecimalFormat)DecimalFormat.getInstance();
	
	public static String formatDecimal(String pattern, double number)
	{
		df.applyPattern(pattern);
		return df.format(number);
	}
	
}
