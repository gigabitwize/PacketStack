package io.nerfnet.packetstack.data;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import io.nerfnet.packetstack.common.Group;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Consumer;

/**
 * Created by Giovanni on 17/01/2018.
 */
public class DataCacheGroup extends AbstractSet<MultiData> implements Group<String, MultiData> {

    private final int id;
    private final ConcurrentHashMap<Integer, List<MultiData>> dataTree = new ConcurrentHashMap<>();
    private final CopyOnWriteArraySet<Thread> closedThreads = new CopyOnWriteArraySet<>();

    private volatile Thread creator;

    public DataCacheGroup(int id) {
        this.id = id;
        this.creator = Thread.currentThread();
    }

    @Override
    public boolean remove(Object o) {
        throw new UnsupportedOperationException("Tried to remove piece of MultiData from group " + id);
    }

    @Override
    public boolean add(@Nonnull MultiData multiData) {
        // We'll add the data to the collection with the least elements
        HashMap<Integer, Integer> sizeMap = Maps.newHashMap();
        int addId = -1;

        // Get sizes of all data collections
        for (int id : dataTree.keySet())
            sizeMap.put(id, dataTree.get(id).size());

        Map.Entry<Integer, Integer> leastData =
                Collections.min(sizeMap.entrySet(),
                        Comparator.comparingInt(Map.Entry::getValue));

        if (leastData == null) {
            dataTree.get(ThreadLocalRandom.current().nextInt(dataTree.size())).add(multiData);
            return true;
        }
        dataTree.get(leastData.getKey()).add(multiData);
        return true;
    }

    @Override
    public void clear() {
        if (onLockedThread()) return;
        dataTree.clear();
        closedThreads.clear();
    }

    @Override
    @Nonnull
    public Object[] toArray() {
        List<List<MultiData>> multiDataList = new ArrayList<>(size());
        multiDataList.addAll(dataTree.values());
        return multiDataList.toArray();
    }

    @Override
    @Nonnull
    @SuppressWarnings("all")
    public <T> T[] toArray(T[] a) {
        List<List<MultiData>> multiDataList = new ArrayList<>(size());
        multiDataList.addAll(dataTree.values());
        return multiDataList.toArray(a);
    }

    @Override
    public int identifier() {
        return id;
    }

    @Override
    public Group<String, MultiData> perform(Consumer<MultiData> action) {
        return this;
    }

    @Override
    public Group<String, MultiData> flush() {
        // TODO store all data on service pstack
        clear();
        return this;
    }

    @Override
    @Nonnull
    public Iterator<MultiData> iterator() {
        return dataTree.get(0).iterator();
    }

    public Iterator<MultiData> iteratorOf(int id) {
        return dataTree.get(id).iterator();
    }

    @Override
    public Optional<MultiData> find(@Nonnull String keyId) {
        if (onLockedThread())
            return Optional.empty();
        // This will be used to store single Data objects
        // group#find("players")*multidata*#.add(data, false)

        //         |--|--|
        // keyId---|--|--|
        //         |--|--|
        for (List<MultiData> dataMap : dataTree.values()) {
            for (MultiData multiData : dataMap) {
                if (multiData.getKey().equalsIgnoreCase(keyId))
                    return Optional.of(multiData);
            }
        }

        return Optional.empty();
    }

    @Override
    public Group<String, MultiData> closeCurrentThread() {
        if (Thread.currentThread() == creator) return this;
        if (onLockedThread()) return this;
        closedThreads.add(Thread.currentThread());
        return this;
    }

    @Override
    public Group<String, MultiData> close(Thread thread) {
        if (onLockedThread()) return this;
        if (thread == creator) return this;
        closedThreads.add(thread);
        return this;
    }

    @Override
    public ImmutableCollection<Thread> closedThreads() {
        return ImmutableList.copyOf(closedThreads);
    }

    @Override
    public Thread getCreator() {
        return creator;
    }

    @Override
    public int size() {
        return dataTree.size();
    }

    public int sizeOf(int id) {
        return dataTree.get(id).size();
    }
}
