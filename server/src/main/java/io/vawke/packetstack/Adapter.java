package io.vawke.packetstack;

/**
 * Created by Giovanni on 15/01/2018.
 */
public interface Adapter {

    /**
     * Returns the PacketStack service.
     *
     * @return The service.
     */
    StackService service();
}
