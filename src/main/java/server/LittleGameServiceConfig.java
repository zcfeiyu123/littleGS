package server;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created with IntelliJ IDEA.
 * User: zhangcen@youku.com
 * Date: 13-10-31
 * Time: 下午4:30
 * To change this template use File | Settings | File Templates.
 */
public class LittleGameServiceConfig {
    //config file
    private final String configFile = "conf/server.properties";
    //instance
    private static LittleGameServiceConfig instance = null;
    //parameters
    private int debugLevel = 0;
    private int hour = -1;
    private int minute = -1;
    private int second = -1;
    //constructor
    private LittleGameServiceConfig(){}

    public static LittleGameServiceConfig getInstance()
    {
        if(instance == null)
        {
            instance = new LittleGameServiceConfig();
        }
        return instance;
    }

    //load and reload config file
    public String load()
    {
        return loadConfig();
    }

    public String reload()
    {
        clearParameters();
        return loadConfig();
    }

    /**
     * main method to read config file
     * @return success or fail
     */
    private String loadConfig()
    {
        try {
            File file = new File(configFile);
            if(!file.exists()||file.isDirectory())
            {
                setDefaultValues();
                return "{status:fail,reason:file " + configFile + " does not exist,result:use default value instead}";
            }
            InputStream in = new FileInputStream(file);
            Properties props = new Properties();
            props.load(in);
            //debug level
            debugLevel = Integer.parseInt(props.getProperty("debugLevel","0"));
            //timed task
            hour = Integer.parseInt(props.getProperty("restartHour","23"));
            minute = Integer.parseInt(props.getProperty("restartMinute","59"));
            second =Integer.parseInt( props.getProperty("restartSecond","0"));
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
            setDefaultValues();
            return "{status:fail,result:use default value instead}";
        }
        return "{status:success,result:server config reload success}";
    }

    private void setDefaultValues()
    {
        this.debugLevel = 0;
        this.hour = 23;
        this.minute = 59;
        this.second = 0;
    }

    private void clearParameters()
    {
        this.debugLevel = 0;
        this.hour = -1;
        this.minute = -1;
        this.second = -1;
    }

    /*-----------------------------------------getters of parameters--------------------------------------------------*/
    public int getHour()
    {
        return hour;
    }

    public int getMinute()
    {
        return minute;
    }

    public int getSecond()
    {
        return second;
    }

    public Boolean isDebug()
    {
        return debugLevel > 0;
    }

}
