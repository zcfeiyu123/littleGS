package littleGS;

import domain.manager.EventFailMessageBox;
import domain.manager.EventResultCode;
import utils.NumericalUtils;

import java.util.LinkedHashSet;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        EventResultCode code = EventResultCode.PASS;
        if(code == EventResultCode.PASS)
        {
            System.out.println("true");
        }
        System.out.println(code==EventResultCode.CoordinatesNotExist);
    }

    private static void addToSet(LinkedHashSet<String> set)
    {
        for(int i = 0; i < 10; i++)
        {
            set.add(String.valueOf(i));
        }
    }
}
