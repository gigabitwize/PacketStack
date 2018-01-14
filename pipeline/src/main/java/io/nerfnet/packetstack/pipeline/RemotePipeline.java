package io.nerfnet.packetstack.pipeline;

import io.nerfnet.packetstack.pipeline.packet.Packet;

/**
 * Created by Giovanni on 14/01/2018.
 */
public abstract class RemotePipeline {

    protected abstract String getRemote();

    protected abstract void handle(Packet packet);
}
