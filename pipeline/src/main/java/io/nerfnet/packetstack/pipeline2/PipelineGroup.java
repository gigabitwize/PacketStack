package io.nerfnet.packetstack.pipeline2;


import com.google.common.collect.ImmutableList;
import io.nerfnet.packetstack.common.Group;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.function.Consumer;

/**
 * Created by Giovanni on 15/01/2018.
 */
public class PipelineGroup extends AbstractSet<Pipeline> implements Group<String, Pipeline> {

    private final int id;
    private final ConcurrentHashMap<String, Pipeline> pipelineMap = new ConcurrentHashMap<>();
    private final CopyOnWriteArraySet<Thread> closedThreads = new CopyOnWriteArraySet<>();

    private volatile Thread creator;

    public PipelineGroup(int id, @Nonnull Set<Pipeline> pipelines) {
        this.id = id;
        this.creator = Thread.currentThread();
        pipelines.forEach(this::add);
    }

    @Override
    public boolean add(@Nonnull Pipeline pipeline) {
        return pipelineMap.putIfAbsent(pipeline.identifier(), pipeline) == null;
    }

    @Override
    public boolean remove(Object o) {
        if(onLockedThread()) return false;
        if (o instanceof String) {
            String identifier = (String) o;
            if (!pipelineMap.containsKey(identifier))
                return false;

            pipelineMap.remove(identifier);
            return true;
        } else if (o instanceof Pipeline) {
            Pipeline pipeline = (Pipeline) o;
            if (!pipelineMap.containsValue(pipeline))
                return false;

            pipelineMap.remove(pipeline.identifier());
            return true;
        }
        return false;
    }

    @Override
    public void clear() {
        if (onLockedThread()) return;
        pipelineMap.clear();
        closedThreads.clear();
    }

    @Override
    @Nonnull
    public Object[] toArray() {
        List<Pipeline> pipelines = new ArrayList<>(size());
        pipelines.addAll(pipelineMap.values());
        return pipelines.toArray();
    }

    @Override
    @Nonnull
    @SuppressWarnings("all")
    public <T> T[] toArray(T[] a) {
        List<Pipeline> pipelines = new ArrayList<>(size());
        pipelines.addAll(pipelineMap.values());
        return pipelines.toArray(a);
    }

    @Override
    @Nonnull
    public Iterator<Pipeline> iterator() {
        return pipelineMap.values().iterator();
    }

    @Override
    public int identifier() {
        return id;
    }

    @Override
    public Optional<Pipeline> find(@Nonnull String keyId) {
        if (onLockedThread())
            return Optional.empty();

        return Optional.ofNullable(pipelineMap.get(keyId));
    }

    @Override
    public Group<String, Pipeline> perform(Consumer<Pipeline> action) {
        if (onLockedThread()) return this;
        pipelineMap.values().forEach(action);
        return this;
    }

    @Override
    public Group<String, Pipeline> flush() {
        if (onLockedThread()) return this;
        pipelineMap.values().forEach(Pipeline::close);

        // Flush closed threads
        closedThreads.clear();
        return null;
    }

    @Override
    public Group<String, Pipeline> close(Thread thread) {
        if (onLockedThread()) return this;
        if (thread == creator) return this;
        closedThreads.add(thread);
        return this;
    }

    @Override
    public Group<String, Pipeline> closeCurrentThread() {
        if (Thread.currentThread() == creator) return this;
        if (onLockedThread()) return this;
        closedThreads.add(Thread.currentThread());
        return this;
    }

    @Override
    public int size() {
        return pipelineMap.size();
    }

    @Override
    public Thread getCreator() {
        return creator;
    }

    @Override
    public ImmutableList<Thread> closedThreads() {
        return ImmutableList.copyOf(closedThreads);
    }
}
