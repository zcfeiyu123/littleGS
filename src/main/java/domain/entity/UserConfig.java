package domain.entity;

import domain.log.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created with IntelliJ IDEA.
 * User: Aaron
 * Date: 13-11-1
 * Time: 下午10:32
 * To change this template use File | Settings | File Templates.
 */
public class UserConfig {

    //properties file path
    private final String configFile = "conf/user.properties";

    /**
     * default value of a new user if we do not have any information about her
     */
    private int defaultHP = -1;
    private int defaultAttr1 = -1;
    private int defaultAttr2 = -1;
    private int defaultAttr3 = -1;
    private int defaultAttr4 = -1;
    private int defaultAttr5 = -1;
    private int defaultAttr6 = -1;

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

    public String load()
    {
        return loadConfig();
    }

    public String reload()
    {
        clearParameters();
        return loadConfig();
    }

    private void clearParameters()
    {
        this.defaultHP = -1;
        this.defaultAttr1 = -1;
        this.defaultAttr2 = -1;
        this.defaultAttr3 = -1;
        this.defaultAttr4 = -1;
        this.defaultAttr5 = -1;
        this.defaultAttr6 = -1;
    }

    private String loadConfig()
    {
        try {
            File file = new File(configFile);
            if(!file.exists()||file.isDirectory())
            {
                System.out.println("[FATAL] config file " + configFile + " does not exist");
            }
            InputStream in = new FileInputStream(file);
            Properties props = new Properties();
            props.load(in);
            //print level
            defaultHP = Integer.parseInt(props.getProperty("defaultHP","5"));
            defaultAttr1 = Integer.parseInt(props.getProperty("defaultAttr1","10"));
            defaultAttr2 = Integer.parseInt(props.getProperty("defaultAttr2","10"));
            defaultAttr3 = Integer.parseInt(props.getProperty("defaultAttr3","10"));
            defaultAttr4 = Integer.parseInt(props.getProperty("defaultAttr4","10"));
            defaultAttr5 = Integer.parseInt(props.getProperty("defaultAttr5","10"));
            defaultAttr6 = Integer.parseInt(props.getProperty("defaultAttr6","10"));
            in.close();
            return "loading user config succeed";
        } catch (Exception e) {
            Logger.getInstance().error("loading config file for user from " + configFile + "fail, use default value instead");
            setDefaultValues();
            e.printStackTrace();
            return "loading user config fail, use default value instead";
        }
    }

    private void setDefaultValues()
    {
        this.defaultHP = 5;
        this.defaultAttr1 = 10;
        this.defaultAttr2 = 10;
        this.defaultAttr3 = 10;
        this.defaultAttr4 = 10;
        this.defaultAttr5 = 10;
        this.defaultAttr6 = 10;
    }

    public int getDefaultHP()
    {
        return this.defaultHP;
    }

    public int getDefaultAttr1()
    {
        return this.defaultAttr1;
    }

    public int getDefaultAttr2()
    {
        return this.defaultAttr2;
    }

    public int getDefaultAttr3()
    {
        return this.defaultAttr3;
    }

    public int getDefaultAttr4()
    {
        return this.defaultAttr4;
    }

    public int getDefaultAttr5()
    {
        return this.defaultAttr5;
    }

    public int getDefaultAttr6()
    {
        return this.defaultAttr6;
    }
}
