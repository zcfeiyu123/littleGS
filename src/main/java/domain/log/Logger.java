package domain.log;

import utils.TimeUtils;

/**
 * Created with IntelliJ IDEA.
 * Author: zhangcen
 * Date: 13-11-7
 * Time: 上午10:56
 */
public class Logger {
    //set up parameters for logger
    private LogConfig config = null;
    private static Logger instance = null;
    //print level
    private int info = -1;
    private int debug = -1;
    private int notice = -1;
    private int error = -1;
    private int fatal = -1;
    private int printLevel = -2;
    private LogLevel currentLevel = LogLevel.ALL;

    private Logger(){

    }

    public static Logger getInstance()
    {
        if(instance == null)
        {
            instance = new Logger();
        }
        return instance;
    }
    //init logger
    public void init()
    {
        config = LogConfig.getInstance();
        this.info = config.getInfo();
        this.debug = config.getDebug();
        this.notice = config.getNotice();
        this.error = config.getError();
        this.fatal = config.getFatal();
        this.printLevel = config.getPrintLevel();
    }
    //leave an interface to change print level in code
    public void setPrintLevel(LogLevel logLevel)
    {
        switch (logLevel)
        {
            case INFO:
                this.printLevel = info;
                this.currentLevel = LogLevel.INFO;
                break;
            case DEBUG:
                this.printLevel = debug;
                this.currentLevel = LogLevel.DEBUG;
                break;
            case NOTICE:
                this.printLevel = notice;
                this.currentLevel = LogLevel.NOTICE;
                break;
            case ERROR:
                this.printLevel = error;
                this.currentLevel = LogLevel.ERROR;
                break;
            case FATAL:
                this.printLevel = fatal;
                this.currentLevel = LogLevel.FATAL;
                break;
            case NONE:
                this.printLevel = Integer.MAX_VALUE;
                this.currentLevel = LogLevel.NONE;
                break;
            case ALL:
                this.printLevel = Integer.MIN_VALUE;
                this.currentLevel = LogLevel.ALL;
                break;
            default://do nothing
                break;
        }
    }

    public String getPrintLevelName()
    {
        return this.currentLevel.name();
    }

    public void info(String message)
    {
        if(printLevel < info)
        {
            System.out.printf("[%s] %s [%s]\n", "INFO", message, TimeUtils.getTimeStr());
        }
    }

    public void debug(String message)
    {
        if(printLevel < debug)
        {
            System.out.printf("[%s] %s [%s]\n", "DEBUG", message, TimeUtils.getTimeStr());
        }
    }

    public void notice(String message)
    {
        if(printLevel < notice)
        {
            System.out.printf("[%s] %s [%s]\n", "NOTICE", message, TimeUtils.getTimeStr());
        }
    }

    public void error(String message)
    {
        if(printLevel < error)
        {
            System.out.printf("[%s] %s [%s]\n", "ERROR", message, TimeUtils.getTimeStr());
        }
    }

    public void fatal(String message)
    {
        if(printLevel < fatal)
        {
            System.out.printf("[%s] %s [%s]\n", "FATAL", message, TimeUtils.getTimeStr());
        }
    }

    public void mark()
    {
        System.out.println("=================================================================");
    }
}
