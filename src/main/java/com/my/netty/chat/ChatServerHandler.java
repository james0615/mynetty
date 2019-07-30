package com.my.netty.chat;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.ArrayList;
import java.util.List;

public class ChatServerHandler extends SimpleChannelInboundHandler<String> {

    public static List<Channel> channelListist=new ArrayList<>();


    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        Channel inChannel=ctx.channel();
        channelListist.add(inChannel);
        System.out.println("【服务器消息】："+inChannel.remoteAddress().toString().substring(1)+"上线了");
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, String s) throws Exception {
        Channel inChannel=ctx.channel();
        for (Channel channel:channelListist){
            if(channel!=inChannel){
                channel.writeAndFlush("【"+inChannel.remoteAddress().toString().substring(1)+"】 说："+s+"\n");
            }
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        Channel inChannel=ctx.channel();
        channelListist.remove(inChannel);
        System.out.println("【服务器消息】："+inChannel.remoteAddress().toString().substring(1)+"离线了");

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
    }
}
