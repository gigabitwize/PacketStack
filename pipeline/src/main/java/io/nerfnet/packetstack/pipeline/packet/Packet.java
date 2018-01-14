package io.nerfnet.packetstack.pipeline.packet;

import java.util.UUID;

/**
 * Created by Giovanni on 14/01/2018.
 */
public class Packet<E> {

    private final E object;
    private final UUID transferId = UUID.randomUUID();

    public Packet(E object) {
        this.object = object;
    }

    public E getObject() {
        return object;
    }

    public UUID getTransferId() {
        return transferId;
    }
}
