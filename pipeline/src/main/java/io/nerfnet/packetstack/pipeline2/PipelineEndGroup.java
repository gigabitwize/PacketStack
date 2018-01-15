package io.nerfnet.packetstack.pipeline2;

import com.google.common.collect.ImmutableCollection;
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
public class PipelineEndGroup extends AbstractSet<PipelineEnd> implements Group<Integer, PipelineEnd> {

    private final int id;
    private final ConcurrentHashMap<Integer, PipelineEnd> pipelineEndMap = new ConcurrentHashMap<>();
    private final CopyOnWriteArraySet<Thread> closedThreads = new CopyOnWriteArraySet<>();

    private volatile Thread creator;

    public PipelineEndGroup(int id) {
        this.id = id;
    }

    @Override
    public boolean add(@Nonnull PipelineEnd pipelineEnd) {
        return pipelineEndMap.putIfAbsent(pipelineEnd.identifier(), pipelineEnd) == null;
    }

    @Override
    public boolean remove(Object o) {
        if (onLockedThread()) return false;
        if (o instanceof Integer) {
            int identifier = (int) o;
            if (!pipelineEndMap.containsKey(identifier))
                return false;

            pipelineEndMap.remove(identifier);
            return true;
        } else if (o instanceof PipelineEnd) {
            PipelineEnd pipelineEnd = (PipelineEnd) o;
            if (!pipelineEndMap.containsValue(pipelineEnd))
                return false;

            pipelineEndMap.remove(pipelineEnd.identifier());
            return true;
        }
        return false;
    }

    @Override
    public void clear() {
        if (onLockedThread()) return;
        pipelineEndMap.clear();
        closedThreads.clear();
    }

    @Override
    public int identifier() {
        return id;
    }

    @Override
    @Nonnull
    public Iterator<PipelineEnd> iterator() {
        return pipelineEndMap.values().iterator();
    }

    @Override
    @Nonnull
    public Object[] toArray() {
        List<PipelineEnd> endings = new ArrayList<>(size());
        endings.addAll(pipelineEndMap.values());
        return endings.toArray();
    }

    @Override
    @Nonnull
    @SuppressWarnings("all")
    public <T> T[] toArray(T[] a) {
        List<PipelineEnd> endings = new ArrayList<>(size());
        endings.addAll(pipelineEndMap.values());
        return endings.toArray(a);
    }

    @Override
    public Optional<PipelineEnd> find(@Nonnull Integer keyId) {
        if (onLockedThread())
            return Optional.empty();

        return Optional.ofNullable(pipelineEndMap.get(keyId));
    }

    @Override
    public Group<Integer, PipelineEnd> perform(Consumer<PipelineEnd> action) {
        if (onLockedThread()) return this;
        pipelineEndMap.values().forEach(action);
        return this;
    }

    @Override
    public Group<Integer, PipelineEnd> flush() {
        if (onLockedThread()) return this;
        // Move all pipelines to a new PipelineEnd
        pipelineEndMap.values().forEach(pipelineEnd -> {
            PipelineGroup group = pipelineEnd.connectedPipelines();
            group.perform(pipeline -> {
                if (!pipeline.closed())
                    pipeline.movePipelineEnding(null);
            });
        });

        closedThreads.clear();
        return this;
    }

    @Override
    public Group<Integer, PipelineEnd> close(Thread thread) {
        if (onLockedThread()) return this;
        if (thread == creator) return this;
        closedThreads.add(thread);
        return this;
    }

    @Override
    public Group<Integer, PipelineEnd> closeCurrentThread() {
        if (Thread.currentThread() == creator) return this;
        if (onLockedThread()) return this;
        closedThreads.add(Thread.currentThread());
        return this;
    }

    @Override
    public int size() {
        return pipelineEndMap.size();
    }

    @Override
    public Thread getCreator() {
        return creator;
    }

    @Override
    public ImmutableCollection<Thread> closedThreads() {
        return ImmutableList.copyOf(closedThreads);
    }
}
