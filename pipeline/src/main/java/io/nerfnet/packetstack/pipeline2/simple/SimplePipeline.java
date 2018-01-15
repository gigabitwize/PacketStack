package io.nerfnet.packetstack.pipeline2.simple;

import com.google.common.collect.Lists;
import io.nerfnet.packetstack.pipeline2.ClosedPipeline;
import io.nerfnet.packetstack.pipeline2.Pipeline;
import io.nerfnet.packetstack.pipeline2.PipelineEnd;
import io.nerfnet.packetstack.pipeline2.object.Packet;
import io.nerfnet.packetstack.pipeline2.object.QueuedPacket;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.UUID;

/**
 * Created by Giovanni on 15/01/2018.
 */
public class SimplePipeline implements Pipeline<Packet> {

    private final String identifier;
    private PipelineEnd pipelineEnd;

    private volatile boolean closed;
    private List<QueuedPacket> queuedPackets = Lists.newArrayList();

    public SimplePipeline(String identifier, @Nonnull PipelineEnd pipelineEnd) {
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
    public Pipeline<Packet> transfer(Packet data, PipelineEnd end) {
        if (closed)
            return this;

        if (packetInQueue(data.getGridId())) return this;

        data.setCurrentPipeline(this);

        if (end.channel() == null || !end.channel().isOpen()) {
            QueuedPacket queuedPacket = new QueuedPacket(data, end.identifier());
            queuedPackets.add(queuedPacket);
            return this;
        }
        end.channel().writeAndFlush(data);
        return this;
    }

    @Override
    public Pipeline<Packet> movePipelineEnding(PipelineEnd newEnd) {
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

    @Override
    public List<QueuedPacket> queuedPackets() {
        return queuedPackets;
    }

    @Override
    public boolean packetInQueue(UUID gridId) {
        for (QueuedPacket queuedPacket : queuedPackets) {
            if (queuedPacket.getPacket().getGridId().toString().equalsIgnoreCase(gridId.toString()))
                return true;
        }
        return false;
    }
}
