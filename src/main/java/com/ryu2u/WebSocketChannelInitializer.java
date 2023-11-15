package com.ryu2u;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.stream.ChunkedWriteHandler;

/**
 * 配置channel 数据通道参数
 * @author Administrator
 * @Description:
 * @date 2023/5/9 13:41
 */
public class WebSocketChannelInitializer extends ChannelInitializer<SocketChannel> {
    @Override
    protected void initChannel(SocketChannel ch){
        ch.pipeline()
                // 设置log监听器，并且日志级别为debug，方便观察运行流程
                .addLast("logging", new LoggingHandler("DEBUG"))
                // 设置解码器
                .addLast("http-codec", new HttpServerCodec())
                // 聚合器，使用websocket会用到
                .addLast("aggregator", new HttpObjectAggregator(65536))
                // 自定义url 的路径 注意aggregator 需要在WebSocketServerProtocolHandler 之前定义
                .addLast(new WebSocketServerProtocolHandler("/ws"))
                // 用于大数据的分区传输
                .addLast("http-chunked", new ChunkedWriteHandler())
                // 自定义的业务handler
                .addLast("handler", new WebSocketHandler());

    }
}
