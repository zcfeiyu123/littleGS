package server;

/**
 * Created with IntelliJ IDEA.
 * User: zhangcen@youku.com
 * Date: 13-10-31
 * Time: 下午4:30
 * To change this template use File | Settings | File Templates.
 */
public class LittleGameServiceConfig {

    private static LittleGameServiceConfig instance = null;

    private LittleGameServiceConfig(){}

    public static LittleGameServiceConfig getInstance()
    {
        if(instance == null)
        {
            instance = new LittleGameServiceConfig();
        }
        return instance;
    }

    public Boolean isDebug()
    {
        return true;
    }

}
