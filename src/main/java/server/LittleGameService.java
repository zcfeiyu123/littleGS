package server;

import domain.manager.EventManager;
import domain.timedtask.TimeTaskManager;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.concurrent.EventExecutor;

import java.util.Iterator;

/**
 * Created with IntelliJ IDEA.
 * Author: zhangcen
 * Date: 13-10-31
 * Time: 下午4:16
 */
public class LittleGameService {

    private final int port;

    public LittleGameService(int port) {
        this.port = port;
    }

    public void run() throws Exception {
        // Configure the server.
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.option(ChannelOption.SO_BACKLOG, 1024);
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new LittleGameServiceInitializer());

            Channel ch = b.bind(port).sync().channel();
            ch.closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    public static void main(String[] args) throws Exception {
        //init server
        int port;
        if (args.length > 0) {
            port = Integer.parseInt(args[0]);
        } else {
            port = 9999;
        }
        //load config
        loadSystemConfig();
        //init domain event manager
        initEventsManager();
        //start timed task
        TimeTaskManager timeTaskManager = new TimeTaskManager();
        //start server
        new LittleGameService(port).run();
    }

    private static void loadSystemConfig()
    {
        LittleGameServiceConfig config = LittleGameServiceConfig.getInstance();
        config.load();
    }

    private static void initEventsManager()
    {
        EventManager manager = EventManager.getManager();
        manager.initialization();
    }

}
