package io.vawke.packetstack;

import io.nerfnet.packetstack.pipeline2.PipelineEndGroup;
import io.nerfnet.packetstack.pipeline2.object.Packet;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;

/**
 * Created by Giovanni on 14/01/2018.
 */
public class StackService {

    private final PipelineEndGroup pipelineEndings;

    private final EventLoopGroup eventBossGroup;
    private final EventLoopGroup eventWorkerGroup;

    StackService() {
        this.pipelineEndings = new PipelineEndGroup(0); // 0 = host group.
        this.eventBossGroup = new NioEventLoopGroup(5);
        this.eventWorkerGroup = new NioEventLoopGroup(5);
    }

    void start() {
        System.out.println("Starting PacketStack service..");

        // TODO clean-up and implement packet protocol, this is here for testing so ignore.
        ServerBootstrap bootstrap = new ServerBootstrap()
                .channel(NioServerSocketChannel.class)
                .group(eventBossGroup, eventWorkerGroup)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        ChannelPipeline pipeline = socketChannel.pipeline();
                        pipeline
                                .addLast(new ObjectEncoder())
                                .addLast(new ObjectDecoder(ClassResolvers.cacheDisabled(Packet.class.getClassLoader())))
                                .addLast(new PipelineEndLauncher() {
                                    @Override
                                    public StackService service() {
                                        return ServerMain.getService();
                                    }
                                });
                    }
                });
    }

    public PipelineEndGroup endingPipelines() {
        return pipelineEndings;
    }
}
