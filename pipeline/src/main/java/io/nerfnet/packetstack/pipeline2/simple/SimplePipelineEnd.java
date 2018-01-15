package io.nerfnet.packetstack.pipeline2.simple;

import io.nerfnet.packetstack.pipeline2.Pipeline;
import io.nerfnet.packetstack.pipeline2.PipelineEnd;
import io.nerfnet.packetstack.pipeline2.PipelineGroup;
import io.netty.channel.Channel;

/**
 * Created by Giovanni on 15/01/2018.
 */
public class SimplePipelineEnd implements PipelineEnd {

    private final int id;
    private final Channel channel;
    private PipelineGroup connectedPipelines;

    public SimplePipelineEnd(int id, Channel channel, PipelineGroup group) {
        this.id = id;
        this.channel = channel;
        this.connectedPipelines = group;
    }

    public SimplePipelineEnd(int id, Channel channel) {
        this.id = id;
        this.channel = channel;
    }

    @Override
    public int identifier() {
        return id;
    }

    @Override
    public Channel channel() {
        return channel;
    }

    @Override
    public PipelineGroup connectedPipelines() {
        return connectedPipelines;
    }

    @Override
    public boolean hasMultiplePipelines() {
        return connectedPipelines.size() > 1;
    }

    @Override
    public PipelineEnd notifyClose(Pipeline pipeline) {
        connectedPipelines.remove(pipeline);
        channel.writeAndFlush("$close::" + pipeline.identifier());
        return this;
    }
}
