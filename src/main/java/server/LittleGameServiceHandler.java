package server;

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
    private final String userUpdate = "userupdate";

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof HttpRequest) {
            HttpRequest req = (HttpRequest) msg;
            QueryStringDecoder queryStringDecoder = new QueryStringDecoder(req.getUri());
            String path = queryStringDecoder.path().substring(1);
            if(LittleGameServiceConfig.getInstance().isDebug())
            {
                SimpleLogger.getLogger().debug("in debug model, path = " + path);
            }
            //process
            if(path.equals(userUpdate))
            {
                this.userUpdateProcess(ctx, queryStringDecoder, req);
            }
            else
            {
                this.defaultProcess(ctx, req, path);
            }

        }
    }

    /**
     * deal with price
     * @param ctx
     * @param queryStringDecoder
     * @param req
     */
    private void userUpdateProcess(ChannelHandlerContext ctx, QueryStringDecoder queryStringDecoder, HttpRequest req)
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

    private void defaultProcess(ChannelHandlerContext ctx, HttpRequest req, String path)
    {
        writeResponse(ctx, req, "{success:fail,reason:unrecognized path as " + path + "}");
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }

    public String getParameter(String key,  Map<String, List<String>> params) {
        List<String> vals = params.get(key);
        if(vals == null) {
            return null;
        }
        String retStr = null;
        StringBuilder buf = new StringBuilder();
        if (key.equals("targetDates") || key.equals("cities")) {
            for (int i = 0; i < vals.size(); i++) {
                if (i < vals.size() - 1)
                    buf.append(vals.get(i)).append(",");
                else
                    buf.append(vals.get(i));
            }
            retStr = buf.toString();
        }
        else {
            for (String val : vals) {
                retStr = val;
            }
        }
        return retStr;
    }

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
