package io.nerfnet.packetstack.pipeline2;

import io.nerfnet.packetstack.data.DataCacheGroup;
import io.netty.channel.Channel;

/**
 * Created by Giovanni on 15/01/2018.
 */
public interface PipelineEnd {

    /**
     * Returns the identifier of this PipelineEnd.
     *
     * @return The identifier of the PipelineEnd.
     */
    int identifier();

    /**
     * Returns the pipelines that are connected to this PipelineEnd as a {@link PipelineGroup}.
     *
     * @return PipelineGroup with the connected pipelines.
     */
    PipelineGroup connectedPipelines();

    /**
     * Notifies this ending that a connected pipeline is closed.
     *
     * @param pipeline The pipeline that has been closed.
     * @return itself
     */
    PipelineEnd notifyClose(Pipeline pipeline);

    /**
     * Gets whether this ending has multiple {@link Pipeline}s connected to it.
     *
     * @return bool
     */
    boolean hasMultiplePipelines();

    /**
     * Returns the channel of this ending.
     *
     * @return The channel.
     */
    Channel channel();

    /**
     * Returns the {@link DataCacheGroup} of this ending pipeline.
     *
     * @return The data group.
     */
    DataCacheGroup getDataGroup();
}