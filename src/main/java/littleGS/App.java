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
        LinkedHashSet<String> set = new LinkedHashSet<String>();
        System.out.println(set.size());
        addToSet(set);
        System.out.println(set.size());
    }

    private static void addToSet(LinkedHashSet<String> set)
    {
        for(int i = 0; i < 10; i++)
        {
            set.add(String.valueOf(i));
        }
    }
}
