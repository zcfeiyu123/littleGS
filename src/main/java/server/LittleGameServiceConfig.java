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
    public boolean load()
    {
        return loadConfig();
    }

    public boolean reload()
    {
        clearParameters();
        return loadConfig();
    }

    /**
     * main method to read config file
     * @return success or fail
     */
    private boolean loadConfig()
    {
        try {
            File file = new File(configFile);
            if(!file.exists()||file.isDirectory())
                return false;
            InputStream in = new FileInputStream(file);
            Properties props = new Properties();
            props.load(in);
            //debug level
            debugLevel = Integer.parseInt(props.getProperty("debugLevel","0"));
            //timed task
            hour = Integer.parseInt(props.getProperty("restartHour","0"));
            minute = Integer.parseInt(props.getProperty("restartMinute","0"));
            second =Integer.parseInt( props.getProperty("restartSecond","0"));
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
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
