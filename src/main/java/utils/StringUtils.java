package utils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: zhangcen@youku.com
 * Date: 13-11-1
 * Time: 下午2:27
 * To change this template use File | Settings | File Templates.
 */
public class StringUtils {
    /**
     * split a given string with a given char c, use it carefully
     * @param str  string
     * @param c  split char
     * @return String array
     */
    public static String[] splitStr(String str, char c)
    {
        if(str.length() == 1)
        {
            if(str.charAt(0) == c)
            {
                return new String[0];
            }
            else
            {
                return new String[]{str};
            }
        }

        String[] tmpContents = new String[str.length()/2];
        int startIndex = 0;
        int index = 0;
        while(startIndex < str.length())
        {
            int endIndex = str.indexOf(c, startIndex);
            if(endIndex > 0)
            {
                if(endIndex > startIndex)
                {
                    tmpContents[index] = str.substring(startIndex, endIndex);
                    index++;
                }
                startIndex = endIndex+1;
            }
            else
            {
                tmpContents[index] = str.substring(startIndex);
                index++;
                break;
            }
        }

        String[] contents = new String[index];
        System.arraycopy(tmpContents, 0, contents, 0, index);

        return contents;
    }

    /**
     * object转换为String
     * add by zhangcen
     * @param obj object
     * @return String
     */
    public static String ObjectToStr(Object obj) {
        if (obj == null || "null".equals(obj)) {
            return "";
        }else {
            return String.valueOf(obj);
        }
    }

    public static void main(String[] args)
    {
       ArrayList<Integer> tmpList = new ArrayList<Integer>();
        for(int i = 0; i < 5; i++)
        {
            tmpList.add(i);
        }

        System.out.println(tmpList.toString());
        long start = System.currentTimeMillis();
        for(int i = 0; i < 1000000; i++)
        {
            tmpList.toString();
        }
        System.out.println(System.currentTimeMillis() - start);
    }
}
