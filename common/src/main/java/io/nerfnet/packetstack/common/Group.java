package io.nerfnet.packetstack.common;

import com.google.common.collect.ImmutableCollection;

import javax.annotation.Nonnull;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;

/**
 * Created by Giovanni on 15/01/2018.
 */
public interface Group<K, E> extends Set<E> {

    /**
     * Returns the identifier of this group.
     *
     * @return Group id.
     */
    int identifier();

    /**
     * Find a component by it's identifier, returns {@link Optional<E>} optional component.
     *
     * @param keyId The component id
     * @return Component if found.
     */
    Optional<E> find(@Nonnull K keyId);

    /**
     * Performs the specified action on all components in this group.
     *
     * @param action The action to perform
     * @return Itself
     */
    Group<K, E> perform(Consumer<E> action);

    /**
     * Removes all components from the group
     *
     * @return An empty group instance.
     */
    Group<K, E> flush();

    /**
     * Closes the group for the specified thread, making it immutable by that thread.
     * If the specified thread is the creator thread, the thread won't close.
     *
     * @param thread The thread to close this group for
     * @return The group instance with an immutable status for the specified thread.
     */
    Group<K, E> close(Thread thread);

    /**
     * Closes the group for the current thread it's available on.
     * If the current thread is the creator thread, the thread won't close.
     *
     * @return The group instance with an immutable status for the parent thread.
     */
    Group<K, E> closeCurrentThread();

    /**
     * Get the thread that created this group.
     *
     * @return The thread.
     */
    Thread getCreator();

    /**
     * Get the threads this group is immutable for.
     *
     * @return Immutable collection of closed threads.
     */
    ImmutableCollection<Thread> closedThreads();
}
