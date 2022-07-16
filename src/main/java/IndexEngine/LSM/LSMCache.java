package IndexEngine.LSM;

import org.apache.log4j.Logger;
import utils.ConfigLoader;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * LSMCache lsm树缓存
 * 接收写请求时，会写到
 */
public class LSMCache {
    private Logger logger = Logger.getLogger(LSMCache.class);

    private TreeMap<String, Long> activeMemtable = new TreeMap<>();
    private LinkedList<TreeMap<String, Long>> immuMemtables = new LinkedList<>();
    private ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    private long cnt = 0;

    // 接收写请求，线程安全
    public void insertCache(String key, Long offset) {
        try {
            lock.writeLock().lock();
            activeMemtable.put(key, offset);
            cnt++;
            if (cnt >= ConfigLoader.getInstance().getCacheSize()) {
                immuMemtables.offer(activeMemtable);
                activeMemtable = new TreeMap<>();
                cnt = 0;
            }
        } catch (Exception e) {
            logger.error("LSMCache insertCache error, err: " + e.getMessage());
        } finally {
            lock.writeLock().unlock();
        }
    }

    // 查cache流程
    public Long getKeyOffset(String key) {
        Long offset = this.activeMemtable.get(key);
        if (offset != null) {
            return offset;
        }
        int tableCnt = this.immuMemtables.size();
        for (int index = tableCnt - 1; index >= 0; index--) {
            Long val = this.immuMemtables.get(index).get(key);
            if (val != null) {
                return val;
            }
        }
        return null;
    }

    // 线程不安全
    public TreeMap<String, Long> getCache() {
        return immuMemtables.poll();
    }

    public int immuCacheNum() {
        return immuMemtables.size();
    }

    public TreeMap<String, Long> getActiveCache() {
        return this.activeMemtable;
    }

}
