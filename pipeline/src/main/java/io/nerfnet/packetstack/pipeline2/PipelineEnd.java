package io.nerfnet.packetstack.pipeline2;

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
}