package io.nerfnet.packetstack.packet;

import io.nerfnet.packetstack.data.Data;
import org.apache.commons.lang.SerializationUtils;

import javax.annotation.Nonnull;
import java.io.Serializable;
import java.util.UUID;

/**
 * Created by Giovanni on 17/01/2018.
 */
public class PipelinePacket implements Packet {

    private final int toEnd; // end id can't change whilst transporting
    private final UUID packetId = UUID.randomUUID();
    private String pipeline;

    public PipelinePacket(int to) {
        this.toEnd = to;
    }

    @Override
    public UUID packetId() {
        return packetId;
    }

    @Override
    public int toEnd() {
        return toEnd;
    }

    @Override
    public String currentPipelineId() {
        return pipeline == null ? "unknown-pipeline" : pipeline;
    }

    @Override
    public WritePacket write(@Nonnull String key, @Nonnull Serializable object, boolean allowDuplicate) {
        return new WritePacket() {
            @Override
            public int toEnd() {
                return toEnd;
            }

            @Override
            public String key() {
                return key;
            }

            @Override
            public boolean canDuplicate() {
                return allowDuplicate;
            }

            @Override
            public byte[] getObject() {
                return SerializationUtils.serialize(object);
            }

            @Override
            public Data getData() {
                return new Data(key, getObject());
            }
        };
    }

    public PipelinePacket onPipeline(String pipeline) {
        this.pipeline = pipeline;
        return this;
    }
}
