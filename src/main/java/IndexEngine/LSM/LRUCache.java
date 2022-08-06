package IndexEngine.LSM;

import java.util.LinkedHashMap;
import java.util.Map;

public class LRUCache<K, V> {
    private LinkedHashMap<K, V> cache;

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
        cache.put(key, val);
    }

    public V get(K key) {
        return cache.get(key);
    }

}
