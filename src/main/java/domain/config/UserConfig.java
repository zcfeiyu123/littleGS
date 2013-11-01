package domain.config;

/**
 * Created with IntelliJ IDEA.
 * User: Aaron
 * Date: 13-11-1
 * Time: 下午10:32
 * To change this template use File | Settings | File Templates.
 */
public class UserConfig {

    //properties file path
    private final String propertiesFile = "./conf/user.properties";

    /**
     * default value of a new user if we do not have any information about her
     */
    private int defaultHP = -1;
    private double attr1 = -1;
    private double attr2 = -1;
    private double attr3 = -1;
    private double attr4 = -1;
    private double attr5 = -1;
    private double attr6 = -1;

    private static UserConfig instance = null;
    private UserConfig(){}

    public static UserConfig getInstance()
    {
        if(instance == null)
        {
            instance = new UserConfig();
        }
        return instance;
    }

    public boolean loadConfig()
    {
        return loadPropertiesFromConfig();
    }

    public boolean reloadConfig()
    {
        this.clearParameters();
        return this.loadPropertiesFromConfig();
    }

    private void clearParameters()
    {
        this.defaultHP = -1;
        this.attr1 = -1;
        this.attr2 = -1;
        this.attr3 = -1;
        this.attr4 = -1;
        this.attr5 = -1;
        this.attr6 = -1;
    }

    private boolean loadPropertiesFromConfig()
    {
        //TODO we need to fill the content of this method
        return true;
    }

}
