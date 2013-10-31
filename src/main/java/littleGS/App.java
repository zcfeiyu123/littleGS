package littleGS;

import io.netty.bootstrap.Bootstrap;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        Bootstrap bootstrap = new Bootstrap();
        Logger logger = LogManager.getLogger();
        System.out.println(logger.isInfoEnabled());
        logger.info("some thing");
        System.out.println( "Hello World!" );
    }
}
