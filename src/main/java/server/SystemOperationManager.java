package server;

import domain.entity.UserConfig;
import domain.manager.EventManager;
/**
 * Created with IntelliJ IDEA.
 * Author: zhangcen
 * Date: 13-11-12
 * Time: 上午10:27
 */
public class SystemOperationManager {
    private static SystemOperationManager instance;
    private SystemOperationManager(){}

    public static SystemOperationManager getInstance()
    {
        if(instance == null)
        {
            instance = new SystemOperationManager();
        }
        return instance;
    }

    public String reloadServerConfig()
    {
        return LittleGameServiceConfig.getInstance().reload();
    }

    public String reloadUserConfig()
    {
        return UserConfig.getInstance().reload();
    }

    public String reloadEventConfig()
    {
        return EventManager.getManager().reloadConfig();
    }
}
