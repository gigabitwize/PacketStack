package io.nerfnet.packetstack.pipeline;

import io.nerfnet.packetstack.pipeline.packet.Packet;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import javax.annotation.Nonnull;

/**
 * Created by Giovanni on 14/01/2018.
 */
@ChannelHandler.Sharable
public class NettyObjectPipeline extends SimpleChannelInboundHandler<Packet> {

    private final Pipeline pipeline;

    public NettyObjectPipeline(@Nonnull Pipeline pipeline) {
        this.pipeline = pipeline;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Packet packet) throws Exception {
        pipeline.write(packet);
    }
}
