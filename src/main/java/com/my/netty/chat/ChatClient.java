package com.my.netty.chat;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

import java.util.Scanner;

public class ChatClient {

    private String address;

    private int port;

    public ChatClient(String address, int port) {
        this.address = address;
        this.port = port;
    }

    public void run() {

        NioEventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new ClientHandler());
            ChannelFuture future = bootstrap.connect(address, port).sync();

            Channel channel=future.channel();
            System.out.println("------------"+channel.localAddress().toString().substring(1)+"-------");

            Scanner scanner=new Scanner(System.in);
            while (scanner.hasNextLine()){
                String s=scanner.nextLine();
                if ("exit".equals(s)){
                    break;
                }
                channel.writeAndFlush(s+"\r\n");
            }

            future.channel().closeFuture().sync();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            group.shutdownGracefully();
        }

    }

    private class ClientHandler extends ChannelInitializer<SocketChannel> {

        @Override
        protected void initChannel(SocketChannel ch) throws Exception {
            //编解码器一定要在自定义handler前面
            ChannelPipeline pipeline = ch.pipeline();//拿到链
            pipeline.addLast("decoder", new StringDecoder());
            pipeline.addLast("encoder",new StringEncoder());
            pipeline.addLast(new ChatClientHandler());
        }
    }

    public static void main(String[] args) {
        //for (int i=0;i<3;i++){
            new Thread(() -> new ChatClient("127.0.0.1", 8081).run()).start();
       // }
    }

}
