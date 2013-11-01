package domain.manager;

import domain.config.UserConfig;

/**
 * Created with IntelliJ IDEA.
 * User: Aaron
 * Date: 13-11-1
 * Time: 下午10:42
 * To change this template use File | Settings | File Templates.
 */
public class ConfigManager {

    private static ConfigManager instance = null;
    private ConfigManager(){
        this.init();
    }

    public static ConfigManager getInstance()
    {
        if(instance == null)
        {
            instance = new ConfigManager();
        }
        return instance;
    }

    /**
     * initialize all the config instances here
     */
    private void init()
    {
        this.userConfigInstance = UserConfig.getInstance();
    }

    //all kinds of config
    private UserConfig userConfigInstance = null;

    public boolean loadAllConfig()
    {
        if(!userConfigInstance.loadConfig())
        {
            return false;
        }
        return true;
    }

    public String reloadUserConfig()
    {
        boolean flag = userConfigInstance.reloadConfig();
        if(flag)
        {
            return "{status:success,result:reload user config succeed}";
        }
        return "{status:fail,result:reload user config fail}";
    }

}
