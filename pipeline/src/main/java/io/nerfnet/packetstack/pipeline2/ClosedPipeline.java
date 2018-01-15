package io.nerfnet.packetstack.pipeline2;

/**
 * Created by Giovanni on 15/01/2018.
 */
public interface ClosedPipeline {

    /**
     * Returns the identifier of the now closed {@link Pipeline}
     *
     * @return The identifier.
     */
    String deadPipeline();
}
