package littleGS;

import io.netty.bootstrap.Bootstrap;
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
        logger.info("some thing");
        logger.fatal("eng??");
        System.out.println( "Hello World!" );
    }
}
