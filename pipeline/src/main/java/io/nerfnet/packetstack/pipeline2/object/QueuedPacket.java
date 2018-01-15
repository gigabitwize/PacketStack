package io.nerfnet.packetstack.pipeline2.object;

/**
 * Created by Giovanni on 15/01/2018.
 */
public class QueuedPacket {

    private final Packet packet;
    private final int targetEnd;

    public QueuedPacket(Packet packet, int targetEnd) {
        this.packet = packet;
        this.targetEnd = targetEnd;
    }

    public Packet getPacket() {
        return packet;
    }

    public int getTargetEnd() {
        return targetEnd;
    }
}
