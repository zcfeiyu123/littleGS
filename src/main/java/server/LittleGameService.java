package server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import utils.SimpleLogger;

import domain.manager.ConfigManager;

/**
 * Created with IntelliJ IDEA.
 * User: zhangcen@youku.com
 * Date: 13-10-31
 * Time: 下午4:16
 * To change this template use File | Settings | File Templates.
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

        if(initConfigManager())
        {
            System.out.println("all config loaded");
        }

        //start server
        new LittleGameService(port).run();
    }

    private static boolean initConfigManager()
    {
        ConfigManager configManager = ConfigManager.getInstance();
        return configManager.loadAllConfig();
    }

}
