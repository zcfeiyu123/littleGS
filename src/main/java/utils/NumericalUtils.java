package utils;

import java.text.DecimalFormat;

public class NumericalUtils {

	private static final DecimalFormat df = (DecimalFormat)DecimalFormat.getInstance();
	
	public static String formatDecimal(String pattern, double number)
	{
		df.applyPattern(pattern);
		return df.format(number);
	}

    public static double toDouble(String number)
    {
        try{
            return Double.parseDouble(number);
        }catch (Exception e)
        {
            return Double.NaN;
        }
    }

    public static int toInteger(String number)
    {
        try{
            return Integer.parseInt(number);
        }catch (Exception e)
        {
            return -1;
        }
    }
}
