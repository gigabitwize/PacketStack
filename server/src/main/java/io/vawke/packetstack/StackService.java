package io.vawke.packetstack;

import io.nerfnet.packetstack.pipeline.PipelineGroup;
import io.nerfnet.packetstack.pipeline.RemotePipeline;
import io.nerfnet.packetstack.pipeline.packet.Packet;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;

import java.util.Collections;
import java.util.Map;

/**
 * Created by Giovanni on 14/01/2018.
 */
public class StackService {

    private final PipelineGroup pipelineGroup;

    private final EventLoopGroup eventBossGroup;
    private final EventLoopGroup eventWorkerGroup;

    public StackService() {
        RemotePipeline testPipeline = new RemotePipeline() {
            @Override
            protected void handle(Packet packet) {
                System.out.println("Received packet on pipeline-" + getRemote());
            }

            @Override
            protected String getRemote() {
                return "eu0001";
            }
        };

        this.pipelineGroup = PipelineGroup.of(Collections.singletonList(testPipeline));
        this.eventBossGroup = new NioEventLoopGroup(5);
        this.eventWorkerGroup = new NioEventLoopGroup(5);
    }

    void start() {
        System.out.println("Starting PacketStack service..");

        // test
        pipelineGroup.all().get(0).loadClass(Packet.class);
        pipelineGroup.all().get(0).loadClass(Map.class);

        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel socketChannel) throws Exception {
                ChannelPipeline channelPipeline = socketChannel.pipeline();
                pipelineGroup.all().forEach(pipeline -> {
                    pipeline.getClasses().forEach(clazz -> {
                        channelPipeline.addLast(new ObjectDecoder(ClassResolvers.cacheDisabled(clazz.getClassLoader())));
                    });
                });
            }
        });
    }
}
