package net.thesimpleteam.pluginapi.utils;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;

public class BlockingHashMap<K, V> extends ConcurrentHashMap<K, V> {
    private final CountDownLatch latch = new CountDownLatch(1);

    public V get(Object key) {
        V value = super.get(key);
        if (value == null) {
            try {
                latch.await();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException(e);
            }
            value = super.get(key);
        }
        return value;
    }

    public V put(@NotNull K key, @NotNull V value) {
        V oldValue = super.put(key, value);
        latch.countDown();
        return oldValue;
    }
}
