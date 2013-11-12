package domain.manager;

/**
 * Created with IntelliJ IDEA.
 * Author: zhangcen
 * Date: 13-11-12
 * Time: 上午10:55
 */
public class EventFailMessages {

    public static final String UserNameNullOrEmpty = "{status:fail,reason:user name is null or empty}";
    public static final String UserNotExist = "{status:fail,reason:user does not exist}";

    public static final String LongitudeFormatWrong = "{status:fail,reason:longitude format wrong}";
    public static final String UnknownError = "{status:fail,reason:unknown error during process}";



}
