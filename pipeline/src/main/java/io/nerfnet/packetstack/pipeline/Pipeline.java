package io.nerfnet.packetstack.pipeline;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import io.nerfnet.packetstack.pipeline.packet.Packet;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

/**
 * Created by Giovanni on 14/01/2018.
 */
public class Pipeline extends Thread {

    private final int id;
    private final ThreadLocal<RemotePipeline> remotePipeline;
    private final HashMap<UUID, Packet> remoteObjectQueue = Maps.newHashMap();
    private final List<Class> registeredClasses = Lists.newArrayList();

    private int exceptionCaught = 0;
    private int maxExceptions;

    public Pipeline(int id, int maxExceptions, RemotePipeline remotePipeline) {
        this.id = id;
        this.maxExceptions = maxExceptions;
        this.remotePipeline = new ThreadLocal<RemotePipeline>() {
            @Override
            protected RemotePipeline initialValue() {
                return remotePipeline;
            }
        };
    }

    @Override
    public void run() {
        try {
            if (isInterrupted()) {
                System.out.println("Pipeline interrupted, could not handle data exchange.");
                return;
            }

            synchronized (remoteObjectQueue) {
                remoteObjectQueue.keySet().forEach(uuid -> {
                    remotePipeline.get().handle(remoteObjectQueue.get(uuid));
                });
            }


        } catch (Exception e) {
            if (exceptionCaught == 10) return;
            exceptionCaught++;

            System.out.println("An exception was caught in pipeline with id " + id + "!");
            System.out.println("Current exception: " + exceptionCaught);
            System.out.println("Max. exception print = " + maxExceptions);
            System.out.println("WARN: " + e.getMessage());
        }
    }

    public void write(Packet packet) {
        synchronized (remoteObjectQueue) {
            // Don't handle on this pipeline twice if the packet is in a circle
            if (remoteObjectQueue.containsKey(packet.getTransferId())) return;
            remoteObjectQueue.put(packet.getTransferId(), packet);
        }
    }

    public void loadClass(Class clazz) {
        registeredClasses.add(clazz);
    }

    public ImmutableList<Class> getClasses() {
        return ImmutableList.copyOf(registeredClasses);
    }
}
