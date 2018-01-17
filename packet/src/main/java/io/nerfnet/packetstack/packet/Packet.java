package io.nerfnet.packetstack.packet;

import javax.annotation.Nonnull;
import java.io.Serializable;
import java.util.UUID;

/**
 * Created by Giovanni on 17/01/2018.
 */
public interface Packet extends Serializable {

    UUID packetId();

    /**
     * Returns the identifier of the target pipeline ending.
     *
     * @return PipelineEnd ID
     */
    int toEnd();

    /**
     * Returns the Pipeline ID of the current one this packet is on.
     *
     * @return Pipeline ID
     */
    String currentPipelineId();

    /**
     * Creates a {@link WritePacket} with the set data.
     * <p>
     * `::WRITE {key} VALUE {object} DUPLICATES {allowDuplicate}`
     *
     * @param key            The key to write at on the end of a pipeline.
     * @param object         The object to write to the key.
     * @param allowDuplicate Whether the given key can have duplicate values of the written object.
     * @return {@link WritePacket}
     */
    WritePacket write(@Nonnull String key, @Nonnull Serializable object, boolean allowDuplicate);
}
