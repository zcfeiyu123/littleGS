package server;

import domain.entity.UserConfig;
import domain.log.Logger;
import domain.manager.EventManager;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.QueryStringDecoder;

import static io.netty.handler.codec.http.HttpHeaders.Names.*;
import static io.netty.handler.codec.http.HttpHeaders.*;
import static io.netty.handler.codec.http.HttpResponseStatus.*;
import static io.netty.handler.codec.http.HttpVersion.*;

import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * Author: zhangcen
 * Date: 13-10-31
 * Time: 下午4:17
 */
public class LittleGameServiceHandler extends ChannelInboundHandlerAdapter {
    private Logger logger = Logger.getInstance();
    private EventManager manager = EventManager.getManager();

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof HttpRequest) {
            HttpRequest req = (HttpRequest) msg;
            QueryStringDecoder queryStringDecoder = new QueryStringDecoder(req.getUri());
            String operation = queryStringDecoder.path().substring(1);
            logger.debug("in debug model, path = " + operation);
            //detect what kind of operation in process
            if(UserOperations.isUserOperation(operation))
            {
                this.processUserOperation(ctx, queryStringDecoder, req, operation);
            }
            else if(SystemOperations.isSystemOperation(operation))
            {
                this.processSystemOperation(ctx, req, operation);
            }
            else //this is an unrecognized operation
            {
                this.unrecognizedOperation(ctx, req, operation);
            }
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }

    /*-----------------------------------------user operation -----------------------------------------------*/

    /**
     * process with user operation
     * @param ctx
     * @param queryStringDecoder
     * @param req
     * @param operation
     */
    private void processUserOperation(ChannelHandlerContext ctx, QueryStringDecoder queryStringDecoder, HttpRequest req, String operation)
    {
        UserOperations userOperation = UserOperations.valueOf(operation);
        //process
        switch (userOperation)
        {
            case Create:
                this.createOperation(ctx, queryStringDecoder, req);
                break;
            case Refresh:
                this.refreshOperation(ctx, queryStringDecoder, req);
                break;
            case GetWeapon:
                this.getWeaponOperation(ctx, queryStringDecoder, req);
                break;
            case UserInstantWeapon:
                this.useInstantWeaponOperation(ctx, queryStringDecoder, req);
                break;
            case UseDelayedWeapon:
                this.useDelayedWeaponOperation(ctx, queryStringDecoder, req);
                break;
            case PublishEvents:
                this.publishEventsOperation(ctx, queryStringDecoder, req);
                break;
            case icon:
                break;
            default:
                break;
        }
    }

    /**
     * create a user when she logs in for the first time today
     * @param ctx
     * @param queryStringDecoder
     * @param req
     */
    private void createOperation(ChannelHandlerContext ctx, QueryStringDecoder queryStringDecoder, HttpRequest req)
    {
        //store all the parameters in map
        Map<String, List<String>> params = queryStringDecoder.parameters();
        String userName = getParameter("userName", params);
        this.writeResponse(ctx, req, manager.create(userName));
    }

    /**
     * refresh operation for user
     * @param ctx
     * @param queryStringDecoder
     * @param req
     */
    private void refreshOperation(ChannelHandlerContext ctx, QueryStringDecoder queryStringDecoder, HttpRequest req)
    {
        //store all the parameters in a map
        Map<String, List<String>> params = queryStringDecoder.parameters();
        //parameters we need are user name, the position information of user
        String userName = getParameter("userName", params).trim();
        String longitudeStr = getParameter("longitude", params);
        String latitudeStr = getParameter("latitude", params);
        this.writeResponse(ctx, req, manager.refresh(userName, longitudeStr, latitudeStr));
    }

    /**
     * get weapon operation for user
     * @param ctx
     * @param queryStringDecoder
     * @param req
     */
    private void getWeaponOperation(ChannelHandlerContext ctx, QueryStringDecoder queryStringDecoder, HttpRequest req)
    {
        //store all the parameters in a map
        Map<String, List<String>> params = queryStringDecoder.parameters();
        //the only parameter we need to get a weapon for user is its userName
        String userName = getParameter("userName", params);
        this.writeResponse(ctx, req, manager.getWeapon(userName));
    }

    private void useInstantWeaponOperation(ChannelHandlerContext ctx, QueryStringDecoder queryStringDecoder, HttpRequest req)
    {
        //store all the parameters in a map
        Map<String, List<String>> params = queryStringDecoder.parameters();
        //instant weapon needs three kinds of parameters, user, targetUsers, weaponID
        String userName = getParameter("userName", params);
        String targetUsers = getParameter("targetUsers", params);
        String weaponID = getParameter("weaponID", params);

        this.writeResponse(ctx, req, manager.useInstantWeapon(userName, targetUsers, weaponID));
    }

    private void useDelayedWeaponOperation(ChannelHandlerContext ctx, QueryStringDecoder queryStringDecoder, HttpRequest req)
    {
        //store all the parameters in a map
        Map<String, List<String>> params = queryStringDecoder.parameters();
        //timed weapon, we have to have the exact time the user set, and the position information of the weapon
        String userName = getParameter("userName", params);
        String longitude = getParameter("longitude", params);
        String latitude = getParameter("latitude", params);
        String weaponID = getParameter("weaponID", params);
        String launchTime = getParameter("launchTime", params);
        this.writeResponse(ctx, req, manager.useDelayedWeapon(userName, longitude, latitude,weaponID,launchTime));
    }

    private void publishEventsOperation(ChannelHandlerContext ctx, QueryStringDecoder queryStringDecoder, HttpRequest req)
    {
        //store all the parameters in a map
        Map<String, List<String>> params = queryStringDecoder.parameters();
        //only information we need is userName
        String userName = getParameter("userName",params);
        this.writeResponse(ctx, req, manager.publishMessages(userName));
    }

    /*-------------------------------------system operation-------------------------------------------------*/
    private void processSystemOperation(ChannelHandlerContext ctx,HttpRequest req, String operation)
    {
        SystemOperations systemOperation = SystemOperations.valueOf(operation);
        switch (systemOperation){
            case ReloadEventConfig:
                this.reloadEventConfig(ctx,req);
                break;
            case ReloadServerConfig:
                this.reloadServerConfig(ctx,req);
                break;
            case ReloadUserConfig:
                this.reloadUserConfig(ctx,req);
                break;
            default:
                break;
        }
    }

    private void reloadEventConfig(ChannelHandlerContext ctx,HttpRequest req)
    {
        this.writeResponse(ctx, req, SystemOperationManager.getInstance().reloadEventConfig());
    }

    private void reloadServerConfig(ChannelHandlerContext ctx,HttpRequest req)
    {
        this.writeResponse(ctx, req, SystemOperationManager.getInstance().reloadServerConfig());
    }

    private void reloadUserConfig(ChannelHandlerContext ctx,HttpRequest req)
    {
        this.writeResponse(ctx, req, SystemOperationManager.getInstance().reloadUserConfig());
    }

    /*----------------------------------------unrecognized operation---------------------------------------*/

    /**
     * deal with unrecognized operation
     * @param ctx
     * @param req
     * @param path
     */
    private void unrecognizedOperation(ChannelHandlerContext ctx, HttpRequest req, String path)
    {
        writeResponse(ctx, req, "{status:fail,reason:unrecognized path as " + path + "}");
    }

    /*-----------------------------------------basic operation ----------------------------------------------*/
    /**
     * get parameters from url path
     * @param key parameter name
     * @param params
     * @return
     */
    public String getParameter(String key,  Map<String, List<String>> params) {
        List<String> values = params.get(key);
        if(values == null || values.size() < 1)
        {
            return null;
        }
        else
        {
            return values.get(0);
        }
    }

    /**
     * write response back to client
     * @param ctx
     * @param req
     * @param resultStr
     */
    private void writeResponse(ChannelHandlerContext ctx, HttpRequest req, String resultStr)
    {
        if (is100ContinueExpected(req)) {
            ctx.write(new DefaultFullHttpResponse(HTTP_1_1, CONTINUE));
        }
        boolean keepAlive = isKeepAlive(req);
        FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, OK, Unpooled.wrappedBuffer(resultStr.getBytes()));
        response.headers().set(CONTENT_TYPE, "text/plain");
        response.headers().set(CONTENT_LENGTH, response.content().readableBytes());

        if (!keepAlive) {
            ctx.write(response).addListener(ChannelFutureListener.CLOSE);
        } else {
            response.headers().set(CONNECTION, Values.KEEP_ALIVE);
            ctx.write(response);
        }
    }
}
