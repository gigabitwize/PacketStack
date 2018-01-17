package io.nerfnet.packetstack.packet;

import io.nerfnet.packetstack.data.Data;

import java.io.Serializable;

/**
 * Created by Giovanni on 17/01/2018.
 */
public interface WritePacket extends Serializable {

    /**
     * @see Packet#toEnd()
     * <p>
     * This is to let the current pipeline know to which end this packet is going.
     */
    int toEnd();

    /**
     * The key to which we're writing to at the end of a pipeline.
     * *
     *
     * @return The key
     */
    String key();

    /**
     * Whether the {@link WritePacket#key()} can hold duplicate values of the {@link WritePacket#getObject()}
     *
     * @return bool
     */
    boolean canDuplicate();

    /**
     * Returns the bytes of the object this packet is transporting.
     *
     * @return The bytes
     */
    byte[] getObject();

    /**
     * Returns the data of the object.
     *
     * @return {@link Data}
     */
    Data getData();
}
