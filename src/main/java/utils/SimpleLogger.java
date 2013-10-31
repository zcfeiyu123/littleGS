package utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Created with IntelliJ IDEA.
 * User: zhangcen@youku.com
 * Date: 13-10-31
 * Time: 下午4:36
 * To change this template use File | Settings | File Templates.
 */
public class SimpleLogger {

    private static Logger logger = LogManager.getLogger();

    public static Logger getLogger()
    {
        return logger;
    }
}
