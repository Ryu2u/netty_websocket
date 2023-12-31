package com.ryu2u;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import message.Message;
import org.apache.log4j.Logger;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.concurrent.Executors;

import static java.lang.Thread.sleep;

/**
 * WebSocket 启动类
 *
 * @author Administrator
 * @Description:
 * @date 2023/5/9 13:38
 */
public class WebSocketServer {

    public final Logger logger = Logger.getLogger(this.getClass());

    public void run() {
        logger.info("正在启动websocket服务器");
        NioEventLoopGroup boss = new NioEventLoopGroup();
        NioEventLoopGroup work = new NioEventLoopGroup();
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(boss, work);
            bootstrap.channel(NioServerSocketChannel.class);
            bootstrap.childHandler(new WebSocketChannelInitializer());
            Channel channel = bootstrap.bind(8084).sync().channel();
            logger.info("webSocket服务器启动成功：" + channel);
            channel.closeFuture().sync();
            logger.info("webSocket服务器正在关闭...");
        } catch (InterruptedException e) {
            e.printStackTrace();
            logger.info("运行出错：" + e);
        } finally {
            boss.shutdownGracefully();
            work.shutdownGracefully();
            logger.info("websocket服务器已关闭");
        }
    }

    public static void main(String[] args) {

        Executors.newFixedThreadPool(1).execute(() -> {
            while (true) {
                try {
                    Message.ChatMessagePack.Builder builder = Message.ChatMessagePack.newBuilder();
                    builder.setToken("Token");
                    builder.setMsgType(Message.MsgType.LOGIN_MESSAGE_TYPE);
                    Message.LoginMessage.Builder loginBuilder = Message.LoginMessage.newBuilder();
                    loginBuilder.setUserId(1);
                    loginBuilder.setUsername("Tauri");
                    builder.setLoginMessage(loginBuilder);
                    Message.ChatMessagePack data = builder.build();
                    byte[] bytes = data.toByteArray();
                    ByteBuf buf = Unpooled.buffer();
                    buf.writeBytes(bytes);
                    ChannelSupervise.send2All(new BinaryWebSocketFrame(buf));
                    sleep(4000);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        });

        new WebSocketServer().run();
    }


}
