package domain.manager;

import domain.log.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created with IntelliJ IDEA.
 * Author: zhangcen
 * Date: 13-11-7
 * Time: 下午9:29
 */
public class EventManagerConfig {
    private static EventManagerConfig instance = null;
    private EventManagerConfig()
    {

    }

    public static EventManagerConfig getInstance()
    {
        if(instance == null)
        {
            instance = new EventManagerConfig();
        }
        return instance;
    }

    //business part
    private final String configFile = "conf/eventManager.properties";
    private int numOfPeople = 0;
    private double stepSize = 0;
    private String numberPattern;

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
        numOfPeople = 0;
        stepSize = 0;
        numberPattern="";
    }

    private String loadConfig()
    {
        try {
            File file = new File(configFile);
            if(!file.exists()||file.isDirectory())
            {
                Logger.getInstance().fatal("config file " + configFile + " does not exist");
            }
            InputStream in = new FileInputStream(file);
            Properties props = new Properties();
            props.load(in);
            //print level
            numOfPeople = Integer.parseInt(props.getProperty("numOfPeople", "50"));
            stepSize = Double.parseDouble(props.getProperty("stepSize","0.0001"));
            numberPattern = props.getProperty("numberPattern","#.0000");
            in.close();
            Logger.getInstance().info("load event manager config succeed");
            return "load event manager config succeed";
        } catch (Exception e) {
            Logger.getInstance().error("load event manager config fail, use default value instead");
            setDefaultValues();
            e.printStackTrace();
            return "load event manager config fail, use default value instead";
        }
    }

    private void setDefaultValues()
    {
        numOfPeople = 50;
        stepSize = 0.0001;
        numberPattern="#.0000";
    }

    public int getNumOfPeople()
    {
        return numOfPeople;
    }

    public double getStepSize()
    {
        return stepSize;
    }

    public String getNumberPattern()
    {
        return numberPattern;
    }
}
