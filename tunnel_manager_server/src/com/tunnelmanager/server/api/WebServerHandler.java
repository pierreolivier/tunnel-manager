package com.tunnelmanager.server.api;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.DecoderResult;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.netty.handler.codec.http.HttpResponseStatus.*;
import static io.netty.handler.codec.http.HttpVersion.*;

/**
 * Class WebServerHandler
 *
 * @author Pierre-Olivier on 06/04/2014.
 */
public class WebServerHandler extends SimpleChannelInboundHandler<Object> {
    /**
     * Netty channel context
     */
    private ChannelHandlerContext context = null;

    /**
     * Http request
     */
    private HttpRequest request;

    /**
     * Http object
     */
    private LastHttpContent trailer = null;

    /**
     * Response buffer
     */
    private final StringBuilder buffer = new StringBuilder();

    /**
     * Header list
     */
    private List<Map.Entry<String, String>> headers = null;

    /**
     * GET map
     */
    private Map<String, List<String>> params = null;

    /**
     * POST map
     */
    private Map<String, List<String>> post = null;

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    @Override
    protected void messageReceived(ChannelHandlerContext ctx, Object msg) {
        this.context = ctx;

        if (msg instanceof HttpRequest) {
            HttpRequest request = this.request = (HttpRequest) msg;

            if (HttpHeaders.is100ContinueExpected(request)) {
                send100Continue(ctx);
            }

            this.buffer.setLength(0);

            this.headers = request.headers().entries();

            QueryStringDecoder queryStringDecoder = new QueryStringDecoder(request.getUri());
            this.params = queryStringDecoder.parameters();

            appendDecoderResult(this.buffer, request);
        }

        if (msg instanceof HttpContent) {
            HttpContent httpContent = (HttpContent) msg;

            ByteBuf content = httpContent.content();
            if (content.isReadable()) {

                QueryStringDecoder queryStringDecoder = new QueryStringDecoder("index.php?" + content.toString(CharsetUtil.UTF_8));
                this.post = queryStringDecoder.parameters();
                if(this.post == null) {
                    this.post = new HashMap<>();
                }

                appendDecoderResult(this.buffer, request);
            }

            if (msg instanceof LastHttpContent) {
                this.trailer = (LastHttpContent) msg;

                /*Action action = AndroidManager.getAction(this);

                String json;

                try {
                    json = action.execute();
                } catch (SQLException | ClassNotFoundException e) {
                    e.printStackTrace();
                    json = JsonFactory.error("error", "database_error");
                }

                this.buffer.append(json);*/

                writeResponse(this.trailer, ctx);
            }
        }
    }

    private static void appendDecoderResult(StringBuilder buf, HttpObject o) {
        DecoderResult result = o.getDecoderResult();
        if (result.isSuccess()) {
            return;
        }

        buf.append(".. WITH DECODER FAILURE: ");
        buf.append(result.cause());
        buf.append("\r\n");
    }

    private boolean writeResponse(HttpObject currentObj, ChannelHandlerContext ctx) {
        boolean keepAlive = HttpHeaders.isKeepAlive(request);
        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, currentObj.getDecoderResult().isSuccess()? HttpResponseStatus.OK : HttpResponseStatus.BAD_REQUEST, Unpooled.copiedBuffer(this.buffer.toString(), CharsetUtil.UTF_8));

        response.headers().set(HttpHeaders.Names.CONTENT_TYPE, "text/plain; charset=UTF-8");

        if (keepAlive) {
            response.headers().set(HttpHeaders.Names.CONTENT_LENGTH, response.content().readableBytes());
            response.headers().set(HttpHeaders.Names.CONNECTION, HttpHeaders.Values.KEEP_ALIVE);
        }

        ctx.write(response);

        return keepAlive;
    }

    private static void send100Continue(ChannelHandlerContext ctx) {
        FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, CONTINUE);
        ctx.write(response);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }

    public ChannelHandlerContext getContext() {
        return this.context;
    }

    public Map<String, List<String>> getParams() {
        return this.params;
    }

    public Map<String, List<String>> getPost() {
        return this.post;
    }
}
