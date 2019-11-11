package com.example.udp.encoder;

import com.example.udp.pojo.LogEvent;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.DatagramPacket;
import io.netty.handler.codec.MessageToMessageEncoder;
import io.netty.util.CharsetUtil;

import java.net.InetSocketAddress;
import java.util.List;

/**
 * 自定义的MessageToMessageEncoder,将LogEvent消息转换为DatagramPacket
 *
 * @author QiShuo
 * @version 1.0
 * @create 2019-11-07 17:02
 */
public class LogEventEncoder extends MessageToMessageEncoder<LogEvent> {
    private final InetSocketAddress remoteAddress;

    /**
     * LogEventEncoder创建了即将被发送到指定的InetSocketAddress的消息DatagramPacket消息
     *
     * @param remoteAddress
     */
    public LogEventEncoder(InetSocketAddress remoteAddress) {
        this.remoteAddress = remoteAddress;
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, LogEvent logEvent, List<Object> out) throws Exception {
        byte[] file = logEvent.getLogfile().getBytes(CharsetUtil.UTF_8);
        byte[] msg = logEvent.getMsg().getBytes(CharsetUtil.UTF_8);
        ByteBuf buf = ctx.alloc().buffer(file.length + msg.length + 1);
        //将文件名写入到ByteBuf中
        buf.writeBytes(file);
        //添加一个separator(分割器)
        buf.writeByte(LogEvent.SEPARATOR);
        //将日志消息写入到ByteBuf中
        buf.writeBytes(msg);
        //将一个拥有数据和目的地址的新DatagramPacket添加到出站消息列表中
        out.add(new DatagramPacket(buf, remoteAddress));

    }
}
