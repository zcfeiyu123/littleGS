package domain.log;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created with IntelliJ IDEA.
 * Author: zhangcen
 * Date: 13-11-7
 * Time: 上午10:57
 */
public class LogConfig {
    private final String configFile = "conf/logger.properties";
    private static LogConfig instance = null;
    //parameters
    private int Info = -1;
    private int Debug = -1;
    private int Notice = -1;
    private int Error = -1;
    private int Fatal = -1;
    private int printLevel = -2;

    //constructor
    private LogConfig()
    {
        loadConfig();
    }

    public static LogConfig getInstance()
    {
        if(instance == null)
        {
            instance = new LogConfig();
        }
        return instance;
    }

    private void loadConfig()
    {
        try {
            File file = new File(configFile);
            if(!file.exists()||file.isDirectory())
            {
                System.out.println("[FATAL] config file " + configFile + " does not exist");
                System.exit(-1);
            }
            InputStream in = new FileInputStream(file);
            Properties props = new Properties();
            props.load(in);
            //print level
            Info = Integer.parseInt(props.getProperty("info"));
            Debug = Integer.parseInt(props.getProperty("debug"));
            Notice = Integer.parseInt(props.getProperty("notice"));
            Error = Integer.parseInt(props.getProperty("error"));
            Fatal = Integer.parseInt(props.getProperty("fatal"));
            printLevel = Integer.parseInt(props.getProperty("printLevel"));
            in.close();
        } catch (Exception e) {
            System.out.println("[FATAL] loading config file " + configFile + " exception");
            e.printStackTrace();
            System.exit(-1);
        }
    }

    public int getInfo()
    {
        return Info;
    }

    public int getDebug()
    {
        return Debug;
    }

    public int getNotice()
    {
        return Notice;
    }

    public int getError()
    {
        return Error;
    }

    public int getFatal()
    {
        return Fatal;
    }

    public int getPrintLevel()
    {
        return printLevel;
    }
}
