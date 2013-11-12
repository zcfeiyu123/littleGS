package domain.manager;

/**
 * Created with IntelliJ IDEA.
 * Author: zhangcen
 * Date: 13-11-12
 * Time: 上午11:09
 */
public class EventFailMessageBox {
    public static String getFailMessageWithCode(EventResultCode code)
    {
        switch (code){
            case UserNameNullOrEmpty:
                return EventFailMessages.UserNameNullOrEmpty;
            case UserNotExist:
                return EventFailMessages.UserNotExist;
            case LongitudeFormatWrong:
                return EventFailMessages.LongitudeFormatWrong;
            default:
                return EventFailMessages.UnknownError;
        }
    }
}
