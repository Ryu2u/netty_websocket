package com.ryu2u;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.ryu2u.obj.SingleChatMessage;
import com.ryu2u.obj.LoginMessage;
import com.ryu2u.obj.Message;
import com.ryu2u.obj.ResultMessage;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.websocketx.*;
import io.netty.util.CharsetUtil;
import org.apache.log4j.Logger;

import javax.xml.transform.Result;
import java.text.SimpleDateFormat;
import java.util.Date;

import static io.netty.handler.codec.http.HttpUtil.isKeepAlive;

/**
 * 消息处理类
 *
 * @author Administrator
 * @Description:
 * @date 2023/5/9 11:33
 */
public class WebSocketHandler extends SimpleChannelInboundHandler<Object> {

    private final Logger logger = Logger.getLogger(this.getClass());

    private WebSocketServerHandshaker handShaker;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        logger.debug("收到消息：" + msg);
        if (msg instanceof FullHttpRequest) {
            logger.debug("http请求消息...");
            //以http请求形式接入，但是走的是websocket
            handleHttpRequest(ctx, (FullHttpRequest) msg);
        } else if (msg instanceof WebSocketFrame) {
            logger.debug("websocket请求消息...");
            //处理websocket客户端的消息
            handlerWebSocketFrame(ctx, (WebSocketFrame) msg);
        }

    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        //添加连接
        logger.debug("客户端加入连接：" + ctx.channel());
        ChannelSupervise.addChannel(ctx.channel());
        ctx.channel().writeAndFlush(new TextWebSocketFrame("success!"));
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        //断开连接
        logger.debug("客户端断开连接：" + ctx.channel());
        ChannelSupervise.removeChannel(ctx.channel());
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    private void handlerWebSocketFrame(ChannelHandlerContext ctx, WebSocketFrame frame) {
        // 判断是否关闭链路的指令
        if (frame instanceof CloseWebSocketFrame) {
            handShaker.close(ctx.channel(), (CloseWebSocketFrame) frame.retain());
            return;
        }
        // 判断是否ping消息
        if (frame instanceof PingWebSocketFrame) {
            ctx.channel().write(
                    new PongWebSocketFrame(frame.content().retain()));
            return;
        }
        // 本例程仅支持文本消息，不支持二进制消息
        if (!(frame instanceof TextWebSocketFrame)) {
            logger.debug("本例程仅支持文本消息，不支持二进制消息");
            throw new UnsupportedOperationException(String.format(
                    "%s frame types not supported", frame.getClass().getName()));
        }
        // 返回应答消息
        String request = ((TextWebSocketFrame) frame).text();
        logger.debug("服务端收到：" + request);
        TextWebSocketFrame tws = new TextWebSocketFrame(request);
        ctx.channel().writeAndFlush(tws);

//        JSONObject data = (JSONObject) message.getData();
//        if (data == null) {
//            ctx.channel().writeAndFlush(ResultMessage.createErrMessageString("发送信息不存在!"));
//            return;
//        }

//        if (Message.SINGLE_MESSAGE.equals(message.getType())) {
//            // 处理聊天消息
//            SingleChatMessage to = data.to(SingleChatMessage.class);
//            handlerChatMessage(ctx, to);
//        } else if (Message.LOGIN_MESSAGE.equals(message.getType())) {
//            // 处理登录消息
//            LoginMessage to = data.to(LoginMessage.class);
//            handlerLoginMessage(ctx, to);
//        }

    }


    /**
     * 唯一的一次http请求，用于创建websocket
     */
    private void handleHttpRequest(ChannelHandlerContext ctx,
                                   FullHttpRequest req) {
        //要求Upgrade为websocket，过滤掉get/Post
        if (!req.decoderResult().isSuccess()
                || (!"websocket".equals(req.headers().get("Upgrade")))) {
            //若不是websocket方式，则创建BAD_REQUEST的req，返回给客户端
            sendHttpResponse(ctx, req, new DefaultFullHttpResponse(
                    HttpVersion.HTTP_1_1, HttpResponseStatus.BAD_REQUEST));
            return;
        }
        WebSocketServerHandshakerFactory wsFactory = new WebSocketServerHandshakerFactory(
                "ws://localhost:8081/ws", null, false);
        handShaker = wsFactory.newHandshaker(req);
        if (handShaker == null) {
            WebSocketServerHandshakerFactory
                    .sendUnsupportedVersionResponse(ctx.channel());
        } else {
            handShaker.handshake(ctx.channel(), req);
        }
    }

    /**
     * 拒绝不合法地请求，并返回错误信息
     */
    private static void sendHttpResponse(ChannelHandlerContext ctx,
                                         FullHttpRequest req, DefaultFullHttpResponse res) {
        // 返回应答给客户端
        if (res.status().code() != 200) {
            ByteBuf buf = Unpooled.copiedBuffer(res.status().toString(),
                    CharsetUtil.UTF_8);
            res.content().writeBytes(buf);
            buf.release();
        }
        ChannelFuture f = ctx.channel().writeAndFlush(res);
        // 如果是非Keep-Alive，关闭连接
        if (!isKeepAlive(req) || res.status().code() != 200) {
            f.addListener(ChannelFutureListener.CLOSE);
        }
    }


    private void handlerChatMessage(ChannelHandlerContext ctx, SingleChatMessage singleChatMessage) {
        logger.info("聊天请求....");
        ChannelSupervise.putChannelId(ctx.channel(), singleChatMessage.getSenderId());
        if (singleChatMessage.getReceiverId() == null) {
            ctx.channel().writeAndFlush(ResultMessage.createErrMessageString("接受方不存在!"));
            return;
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
        String dateStr = sdf.format(new Date());
        ResultMessage rm = new ResultMessage();
        rm.setType("chat_message");
        rm.setBackDate(dateStr);
        rm.setMessage(singleChatMessage);
        TextWebSocketFrame tws = new TextWebSocketFrame(JSON.toJSONString(rm));
        logger.info("发送的文本:" + tws.text());
        // 群发
        if ("all".equalsIgnoreCase(singleChatMessage.getReceiverId())) {
            ChannelSupervise.send2All(tws);
        }
        Channel channel = ChannelSupervise.findChannel(singleChatMessage.getReceiverId());
        if (channel == null) {
            tws = new TextWebSocketFrame(ResultMessage.createErrMessageString("用户[" + singleChatMessage.getReceiverName() + "]未登录!"));
            // 返回[谁发的发给谁]
            ctx.channel().writeAndFlush(tws);
        } else {
            // 发送消息给该用户
            channel.writeAndFlush(tws);
        }
    }

    /**
     * 处理登录请求
     *
     * @param ctx
     * @param to
     */
    private void handlerLoginMessage(ChannelHandlerContext ctx, LoginMessage to) {
        logger.info("登录请求...");
        Integer userId = to.getUserId();
        Boolean isSuccess = ChannelSupervise.putChannelId(ctx.channel(), userId == null ? null : userId.toString());
        if (isSuccess == null) {
            logger.debug("用户信息不存在!");
            ctx.channel().writeAndFlush(ResultMessage.createErrMessageString("连接失败!"));
            ctx.channel().close();

        } else if (isSuccess) {
            logger.info("用户{" + to.getUsername() + "},连接成功!");
            ctx.channel().writeAndFlush(ResultMessage.createSuccessMessageString("连接成功!"));
        }
    }

}
