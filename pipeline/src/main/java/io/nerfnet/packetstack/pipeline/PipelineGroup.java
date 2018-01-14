package io.nerfnet.packetstack.pipeline;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import java.util.List;

/**
 * Created by Giovanni on 14/01/2018.
 */
public class PipelineGroup {

    private final List<Pipeline> availablePipelines = Lists.newArrayList();

    public PipelineGroup loadGroup(List<Pipeline> pipelines) {
        availablePipelines.addAll(pipelines);
        return this;
    }

    public PipelineGroup loadPipeline(Pipeline pipeline) {
        availablePipelines.add(pipeline);
        return this;
    }

    public ImmutableList<Pipeline> all() {
        return ImmutableList.copyOf(availablePipelines);
    }


    public static PipelineGroup of(List<RemotePipeline> remotePipelines) {
        PipelineGroup group = new PipelineGroup();
        remotePipelines.forEach(remote -> {
            group.loadPipeline(new Pipeline(5, 5, remote));
        });
        return group;
    }

    public static PipelineGroup from(List<Pipeline> pipelines) {
        PipelineGroup group = new PipelineGroup();
        group.loadGroup(pipelines);
        return group;
    }
}
