package server;

import domain.entity.User;
import domain.manager.ConfigManager;
import domain.manager.UserManager;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.QueryStringDecoder;
import utils.SimpleLogger;

import static io.netty.handler.codec.http.HttpHeaders.Names.*;
import static io.netty.handler.codec.http.HttpHeaders.*;
import static io.netty.handler.codec.http.HttpResponseStatus.*;
import static io.netty.handler.codec.http.HttpVersion.*;

import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: zhangcen@youku.com
 * Date: 13-10-31
 * Time: 下午4:17
 * To change this template use File | Settings | File Templates.
 */
public class LittleGameServiceHandler extends ChannelInboundHandlerAdapter {

    /**
     * system related operations
     */

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
            if(LittleGameServiceConfig.getInstance().isDebug())
            {
                SimpleLogger.getLogger().debug("in debug model, path = " + operation);
            }
            //detect what kind of operation in process
            if(LittleServiceConstants.isUserOperation(operation))
            {
                //process with user operation
                this.processUserOperation(ctx, queryStringDecoder, req, operation);
            }
            else if(LittleServiceConstants.isSystemOperation(operation))
            {
                //process with system operation
                this.processSystemOperation(ctx, queryStringDecoder, req, operation);
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
        //process
        if(operation.equals(LittleServiceConstants.UserOperations.create))
        {
            //create a user if user not exists
            this.createOperation(ctx, queryStringDecoder, req);
        }
        else if(operation.equals(LittleServiceConstants.UserOperations.refresh))
        {
            //deal with refresh operation
            this.refreshOperation(ctx, queryStringDecoder, req);
        }
        else if(operation.equals(LittleServiceConstants.UserOperations.getWeapon))
        {
            //deal with getWeapon Operation
            this.getWeaponOperation(ctx, queryStringDecoder, req);
        }
        else if(operation.equals(LittleServiceConstants.UserOperations.useWeapon))
        {
            //deal with useWeapon Operation, which is the most complex
            this.useWeaponOperation(ctx, queryStringDecoder, req);
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
        if(userName == null || userName.length() < 1)
        {
            SimpleLogger.getLogger().fatal("userName is null while creating user!");
            this.writeResponse(ctx, req, "{status:fail, reason:userName is null}");
            return;
        }
        String response = UserManager.getInstance().createUser(userName);
        this.writeResponse(ctx, req, response);
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
        double longitude;
        double latitude;
        //params detected
        try{
            longitude = Double.parseDouble(getParameter("longitude", params));
            latitude = Double.parseDouble(getParameter("latitude", params));
        }catch(Exception e)
        {
            this.writeResponse(ctx, req, "{status:fail,reason:parameter is wrong}");
            SimpleLogger.getLogger().fatal(e.getMessage());
            return ;
        }
        String userName = getParameter("userName", params);
        String response = UserManager.getInstance().refresh(userName, longitude, latitude);
        this.writeResponse(ctx, req, response);
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
        if(userName == null || userName.length() < 1)
        {
            this.writeResponse(ctx, req, "{status:fail,reason:userName is null or empty}");
            return;
        }
        //params detected
        String response = "{status:success,weapons:" + UserManager.getInstance().getWeapon(userName) + "}";
        this.writeResponse(ctx, req, response);
    }

    /**
     * use weapon operation for user
     * @param ctx
     * @param queryStringDecoder
     * @param req
     */
    private void useWeaponOperation(ChannelHandlerContext ctx, QueryStringDecoder queryStringDecoder, HttpRequest req)
    {
        //store all the parameters in a map
        Map<String, List<String>> params = queryStringDecoder.parameters();
        //params detected
        String type = getParameter("type", params);
        if(type == null || type.length() < 1)
        {
            this.writeResponse(ctx, req, "{fail:type is null}");
            return;
        }

        String response = "{success: type = " + type +"}";
        this.writeResponse(ctx, req, response);
    }

    /*-------------------------------------system operation-------------------------------------------------*/

    /**
     * process with system operation like reload config or something like that
     * @param ctx
     * @param queryStringDecoder
     * @param req
     * @param operation
     */
    private void processSystemOperation(ChannelHandlerContext ctx, QueryStringDecoder queryStringDecoder, HttpRequest req, String operation)
    {
        if(operation.equals(LittleServiceConstants.SystemOperations.reloadUserConfig))
        {
            this.reloadUserConfigOperation(ctx, queryStringDecoder, req);
        }
    }

    /**
     * reload user config
     * @param ctx
     * @param queryStringDecoder
     * @param req
     */
    private void reloadUserConfigOperation(ChannelHandlerContext ctx, QueryStringDecoder queryStringDecoder, HttpRequest req)
    {
        String response = ConfigManager.getInstance().reloadUserConfig();
        writeResponse(ctx, req, response);
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
