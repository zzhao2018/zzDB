package IndexEngine.LSM;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class LRUCache<K, V> {
    private LinkedHashMap<K, V> cache;
    private ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    public LRUCache(int cacheSize, float loadFactor) {
        int capacity = (int) Math.ceil(cacheSize / loadFactor);
        this.cache = new LinkedHashMap<K, V>(capacity, loadFactor, true) {
            @Override
            protected boolean removeEldestEntry(Map.Entry eldest) {
                return size() > cacheSize;
            }
        };
    }

    public void set(K key, V val) {
        try {
            this.lock.writeLock().lock();
            cache.put(key, val);
        } finally {
            this.lock.writeLock().unlock();
        }
    }

    public V get(K key) {
        try {
            this.lock.readLock().lock();
            return cache.get(key);
        } finally {
            this.lock.readLock().unlock();
        }
    }

}
