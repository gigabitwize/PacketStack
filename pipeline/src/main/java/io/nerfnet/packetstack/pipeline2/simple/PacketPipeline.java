package io.nerfnet.packetstack.pipeline2.simple;

import com.google.common.collect.Lists;
import io.nerfnet.packetstack.packet.PipelinePacket;
import io.nerfnet.packetstack.pipeline2.ClosedPipeline;
import io.nerfnet.packetstack.pipeline2.Pipeline;
import io.nerfnet.packetstack.pipeline2.PipelineEnd;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.UUID;

/**
 * Created by Giovanni on 15/01/2018.
 */
public class PacketPipeline implements Pipeline<PipelinePacket> {

    private final String identifier;
    private PipelineEnd pipelineEnd;

    private volatile boolean closed;

    public PacketPipeline(String identifier, @Nonnull PipelineEnd pipelineEnd) {
        this.identifier = identifier;
        this.pipelineEnd = pipelineEnd;
    }

    @Override
    public String identifier() {
        return identifier;
    }

    @Override
    public PipelineEnd ending() {
        return pipelineEnd;
    }

    @Override
    public Pipeline<PipelinePacket> transfer(PipelinePacket data, PipelineEnd end) {
        if (closed)
            return this;

        if(!end.channel().isOpen()) {
            // TODO queue
            return this;
        }

        data.onPipeline(identifier());
        end.channel().writeAndFlush(data);
        return this;
    }

    @Override
    public Pipeline<PipelinePacket>movePipelineEnding(PipelineEnd newEnd) {
        newEnd.connectedPipelines().remove(this);
        return this;
    }

    @Override
    public ClosedPipeline close() {
        ending().notifyClose(this);
        ClosedPipeline closedPipeline = new ClosedPipeline() {
            @Override
            public String deadPipeline() {
                return identifier();
            }

            @Override
            public String closedAt() {
                return String.valueOf(System.currentTimeMillis());
            }
        };
        closed = true;
        return closedPipeline;
    }

    @Override
    public boolean closed() {
        return closed;
    }

}
